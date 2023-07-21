package com.vkatz.missioncontrol.server.base.connection

import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.MissionControlTPCChannel
import com.vkatz.missioncontrol.common.ValueCommand
import io.ktor.network.sockets.*

class SocketConnection(socket: Socket) : AbsConnection() {
    private val channel = MissionControlTPCChannel(socket)
    private val activeCommands = ArrayList<Command>()
    private var onCommandsChangedListener: ((List<Command>) -> Unit)? = null

    override fun setOnCommandsChangeListener(listener: ((List<Command>) -> Unit)?) {
        this.onCommandsChangedListener = listener
        onCommandsChangedListener?.invoke(activeCommands)
    }

    override suspend fun run(onConnectionChanged: () -> Unit) {
        channel.collect { command ->
            when (command) {
                is ValueCommand<*> -> {
                    val pos = activeCommands.indexOfFirst { it.uuid == command.uuid }
                    if (pos >= 0) activeCommands[pos] = command
                    else {
                        activeCommands.add(command)
                        activeCommands.sortBy { it.orderId }
                    }
                    onCommandsChangedListener?.invoke(activeCommands)
                }

                is Command.PropertyRemove -> {
                    activeCommands.removeIf { it.uuid == command.removeUUID }
                    onCommandsChangedListener?.invoke(activeCommands)
                }

                is Command.ConnectionName -> {
                    this.name = command.name
                    onConnectionChanged()
                }

                else -> Unit
            }
        }
    }

    override suspend fun sendCommand(command: Command) {
        channel.sendCommand(command)
    }
}