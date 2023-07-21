package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vkatz.missioncontrol.common.Command

@Composable
fun NotImplementedCommand(command: Command, modifier: Modifier) {
    Text("Command type ${command::class.simpleName} is not implemented")
}