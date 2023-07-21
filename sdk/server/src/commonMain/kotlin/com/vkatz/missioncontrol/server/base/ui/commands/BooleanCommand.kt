package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.BooleanPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.valueAsState


@Composable
internal fun BooleanCommand(
    command: BooleanPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val value by command.valueAsState()
        var state by remember { mutableStateOf(value ?: true) }
        var isNull by remember(value) { mutableStateOf(command.nullable && value == null) }
        val alpha by animateFloatAsState(if (isNull) 0.25f else 1f)

        LaunchedEffect(value) { if (value != null) state = value!! }

        Text(command.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.weight(1f))
        if (command.nullable) {
            IconButton(
                onClick = {
                    isNull = true
                    command.value = null
                    onChanged()
                },
            ) {
                Icon(Icons.Default.Clear, null)
            }
            Spacer(Modifier.size(8.dp))
        }
        Switch(
            checked = state,
            modifier = Modifier.graphicsLayer {
                this.alpha = alpha
            },
            onCheckedChange = {
                if (isNull) {
                    command.value = state
                    isNull = false
                } else {
                    state = it
                    command.value = it
                }
                onChanged()
            }
        )
    }
}