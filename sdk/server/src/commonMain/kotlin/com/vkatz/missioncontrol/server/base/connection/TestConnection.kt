package com.vkatz.missioncontrol.server.base.connection

import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.Command.*
import com.vkatz.missioncontrol.common.ValueCommand

class TestConnection : AbsConnection() {

    private infix fun ValueCommand<*>.group(name: String) = apply { this.group = name }

    private val testCommands = listOf(
        ColorPropertyUpdate(0xFFFF0000.toInt(), "Color"),
        OptionPropertyUpdate(0, "Options", listOf("Option1", "Option2", "Option3")),
        ActionPropertyUpdate("Action", "Action description", "Run"),
        ActionPropertyUpdate("Action (no description)", "", "Run"),
        StringPropertyUpdate("Initial value", "String"),
        InfoPropertyUpdate("Some info", "String") group "Info",
        IntPropertyUpdate(1, "Int", false) group "Int",
        IntPropertyUpdate(1, "Int?", true) group "Int",
        IntPropertyUpdate(1, "Int..", false, 1, 10) group "Int",
        IntPropertyUpdate(1, "Int?..", true, 1, 10) group "Int",
        FloatPropertyUpdate(1f, "Float", false) group "Float",
        FloatPropertyUpdate(1f, "Float?", true) group "Float",
        FloatPropertyUpdate(1f, "Float..", false, 1f, 10f) group "Float",
        FloatPropertyUpdate(1f, "Float?..", true, 1f, 10f) group "Float",
        BooleanPropertyUpdate(false, "Boolean", false) group "Bool",
        BooleanPropertyUpdate(false, "Boolean?", true) group "Bool",
    )

    override fun setOnCommandsChangeListener(listener: ((List<ValueCommand<*>>) -> Unit)?) {
        listener?.invoke(testCommands)
    }

    override suspend fun run(onConnectionChanged: () -> Unit) = Unit
    override suspend fun sendCommand(command: Command) = Unit
}