package com.vkatz.missioncontrol.common

import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readFully
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.LinkedList

/**
 * Tcp protocol
 *
 * Message: |[int: message length][string :message content]
 */
class MissionControlTPCChannel(private val socket: Socket) {
    companion object {
        private const val PING_DELAY = 1000L
    }

    private val writeLock = Semaphore(1, 1)
    private val commandsMutex = Mutex()
    private val reader = socket.openReadChannel()
    private val writer = socket.openWriteChannel(true)
    private val commandsQueue = LinkedList<String>()

    suspend fun collect(onCommandReceived: suspend (command: Command) -> Unit) {
        runCatching {
            coroutineScope {
                socket.use {
                    val pingJob = launch {
                        while (true) {
                            sendCommand("")
                            delay(PING_DELAY)
                        }
                    }
                    val sendCommandsJob = launch {
                        while (true) {
                            val command: String? = commandsMutex.withLock { commandsQueue.pollFirst() }
                            if (command != null) {
                                val data = command.toByteArray()
                                writer.writeInt(data.size)
                                writer.writeFully(data, 0, data.size)
                            } else writeLock.acquire()
                        }
                    }
                    val readCommandsJob = launch {
                        while (true) {
                            val rawCommand = readCommand()
                            val command = runCatching {
                                Json.decodeFromString<Command>(rawCommand)
                            }.getOrNull()
                            if (command != null) onCommandReceived(command)
                        }
                    }
                    joinAll(pingJob, sendCommandsJob, readCommandsJob)
                }
            }
        }
    }

    private suspend fun readCommand(): String {
        val messageLength = reader.readInt()
        if (messageLength == 0) return ""
        val data = ByteArray(messageLength)
        reader.readFully(data)
        return data.decodeToString()
    }

    suspend fun sendCommand(command: Command) {
        sendCommand(Json.encodeToString(command))
    }

    private suspend fun sendCommand(rawCommand: String) {
        commandsMutex.withLock { commandsQueue.add(rawCommand) }
        runCatching { writeLock.release() }
    }
}