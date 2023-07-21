package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.InfoPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.valueAsState

@Composable
internal fun InfoCommand(
    command: InfoPropertyUpdate,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(command.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(8.dp))
        Text(command.valueAsState().value, style = MaterialTheme.typography.bodyMedium)
    }
}