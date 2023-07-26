package com.vkatz.missioncontrol.server.base.connection

import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.ValueCommand
import java.util.*

abstract class AbsConnection {
    companion object {
        private var connGlobalIndex = 0
    }

    var name = "Conn #${connGlobalIndex++}"
        protected set
    val uuid = UUID.randomUUID().toString()

    abstract fun setOnCommandsChangeListener(listener: ((List<ValueCommand<*>>) -> Unit)?)
    abstract suspend fun run(onConnectionChanged: () -> Unit)
    abstract suspend fun sendCommand(command: Command)

    suspend fun sendCommand(command: ValueCommand<*>) {
        sendCommand(command as Command)
    }
}