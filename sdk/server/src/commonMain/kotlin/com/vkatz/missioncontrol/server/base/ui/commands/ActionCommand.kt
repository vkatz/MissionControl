package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.ActionPropertyUpdate

@Composable
internal fun ActionCommand(
    command: ActionPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    if (command.description.isNotBlank()) Column(
        modifier = modifier,
    ) {
        Text(command.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(8.dp))
        Text(command.description, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = {
                    command.value = System.currentTimeMillis()
                    onChanged()
                },
                content = { Text(command.actionButton) }
            )
        }
    } else Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(command.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = {
                command.value = System.currentTimeMillis()
                onChanged()
            },
            content = { Text(command.actionButton) }
        )
    }
}