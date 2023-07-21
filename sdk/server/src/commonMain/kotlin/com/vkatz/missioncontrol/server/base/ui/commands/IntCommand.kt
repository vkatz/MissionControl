package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vkatz.missioncontrol.common.Command.IntPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.valueAsState

@Composable
internal fun IntCommand(
    command: IntPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    NumberCommand(
        valueState = command.valueAsState(),
        name = command.name,
        minValue = command.min,
        maxValue = command.max,
        steps = if (command.min != null && command.max != null) command.max!! - command.min!! - 1 else 0,
        nullable = command.nullable,
        modifier = modifier,
        toFloat = { it?.toFloat() },
        fromFloat = { it?.toInt() },
        toString = { it?.toString() ?: "" },
        fromString = { it.toIntOrNull() },
        toDisplayValue = { it?.toString() ?: "null" },
        onChanged = {
            command.value = it
            onChanged()
        })
}