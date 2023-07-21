package com.vkatz.missioncontrol.server.base.connection

import com.vkatz.missioncontrol.common.Command
import java.util.*

abstract class AbsConnection {
    companion object {
        private var connGlobalIndex = 0
    }

    var name = "Conn #${connGlobalIndex++}"
        protected set
    val uuid = UUID.randomUUID().toString()

    abstract fun setOnCommandsChangeListener(listener: ((List<Command>) -> Unit)?)
    abstract suspend fun run(onConnectionChanged: () -> Unit)
    abstract suspend fun sendCommand(command: Command)
}