package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vkatz.missioncontrol.common.ValueCommand

@Composable
fun NotImplementedCommand(command: ValueCommand<*>) {
    Text("Command type ${command::class.simpleName} is not implemented")
}