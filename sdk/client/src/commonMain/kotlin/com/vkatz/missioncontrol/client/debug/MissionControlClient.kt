package com.vkatz.missioncontrol.client.debug

import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.ConnectionDefaults
import com.vkatz.missioncontrol.common.MissionControlTPCChannel
import com.vkatz.missioncontrol.common.ValueCommand
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readUTF8Line
import io.ktor.utils.io.core.writeText
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.io.use

open class MissionControlClient private constructor() {

    companion object Default : MissionControlClient() {
        private const val RECONNECT_DELAY = 5000L
    }

    private var name: String? = null

    private var isStarted = false
    private val channelMutex = Mutex()
    private val commandsMutex = Mutex()
    private val activeCommands = HashMap<String, CommandData<*, *>>()
    private var activeMissionControlChannel: MissionControlTPCChannel? = null
    private val protocolCommandScope = CoroutineScope(Dispatchers.IO)

    fun setConnectionName(name: String) {
        this.name = name
        updateProperty(Command.ConnectionName(name))
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun start(port: Int = ConnectionDefaults.UDP_DEFAULT_PORT) = GlobalScope.launch {
        if (!isStarted) {
            isStarted = true
            while (true) {
                runCatching {
                    var address: SocketAddress? = null
                    while (address == null) {
                        address = withTimeoutOrNull(RECONNECT_DELAY) { getServerAddress(port) }
                        if (address == null) delay(1000)
                    }
                    openServerConnection(address)
                }
                delay(1000)
            }
        }
    }

    suspend fun <T, C> registerProperty(command: T, onChanged: () -> Unit)
            where T : Command, T : ValueCommand<C> {
        commandsMutex.withLock {
            activeCommands[command.uuid] = CommandData(command, onChanged)
        }
        updateProperty(command)
    }

    fun updateProperty(command: Command) {
        protocolCommandScope.launch {
            channelMutex.withLock { activeMissionControlChannel }?.sendCommand(command)
        }
    }

    fun removeProperty(command: Command) {
        protocolCommandScope.launch {
            commandsMutex.withLock { activeCommands.remove(command.uuid) }
            channelMutex
                .withLock { activeMissionControlChannel }
                ?.sendCommand(Command.PropertyRemove(command.uuid))
        }
    }

    private suspend fun getServerAddress(udpPort: Int): SocketAddress? {
        val selector = SelectorManager(Dispatchers.IO)
        aSocket(selector)
            .udp()
            .bind { broadcast = true }
            .use { socket ->
                socket.send(
                    Datagram(
                        buildPacket { writeText(ConnectionDefaults.UDP_REQUEST) },
                        InetSocketAddress("255.255.255.255", udpPort)
                    )
                )
                val response = socket.receive()
                val content = response.packet.readUTF8Line()
                val accepted = content == ConnectionDefaults.UDP_RESPONSE
                return if (accepted) response.address else null
            }
    }

    private suspend fun openServerConnection(address: SocketAddress) {
        val selector = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selector)
            .tcp()
            .connect(address) { keepAlive = true }
        val channel = MissionControlTPCChannel(socket)
        channelMutex.withLock { activeMissionControlChannel = channel }
        if (name != null) channel.sendCommand(Command.ConnectionName(name!!))
        commandsMutex.withLock { activeCommands.values.onEach { channel.sendCommand(it.command) } }
        channel.collect {
            val record = commandsMutex.withLock { activeCommands[it.uuid] }
            if (record != null && record.command::class == it::class) {
                @Suppress("UNCHECKED_CAST")
                (record.command as ValueCommand<Any?>).value = (it as ValueCommand<Any?>).value
                record.onChanged()
            }
        }
        channelMutex.withLock { activeMissionControlChannel = null }
    }

    data class CommandData<T, C>(var command: T, val onChanged: () -> Unit) where T : Command, T : ValueCommand<C>
}