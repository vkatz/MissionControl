package com.vkatz.missioncontrol.server.base.connection

import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.Command.*

class TestConnection : AbsConnection() {

    private val testCommands = listOf<Command>(
        ColorPropertyUpdate(0xFFFF0000.toInt(), "Color"),
        OptionPropertyUpdate(0, "Options", listOf("Option1", "Option2", "Option3")),
        ActionPropertyUpdate("Action", "Action description", "Run"),
        ActionPropertyUpdate("Action (no description)", "", "Run"),
        StringPropertyUpdate("Initial value", "String"),
        InfoPropertyUpdate("Some info", "String"),
        IntPropertyUpdate(1, "Int", false),
        IntPropertyUpdate(1, "Int?", true),
        IntPropertyUpdate(1, "Int..", false, 1, 10),
        IntPropertyUpdate(1, "Int?..", true, 1, 10),
        FloatPropertyUpdate(1f, "Float", false),
        FloatPropertyUpdate(1f, "Float?", true),
        FloatPropertyUpdate(1f, "Float..", false, 1f, 10f),
        FloatPropertyUpdate(1f, "Float?..", true, 1f, 10f),
        BooleanPropertyUpdate(false, "Boolean", false),
        BooleanPropertyUpdate(false, "Boolean?", true),
    )

    override fun setOnCommandsChangeListener(listener: ((List<Command>) -> Unit)?) {
        listener?.invoke(testCommands)
    }

    override suspend fun run(onConnectionChanged: () -> Unit) = Unit
    override suspend fun sendCommand(command: Command) = Unit
}