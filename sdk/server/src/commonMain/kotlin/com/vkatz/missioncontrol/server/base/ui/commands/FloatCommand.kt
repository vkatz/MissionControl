package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.vkatz.missioncontrol.common.Command.FloatPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.valueAsState
import java.util.*

@Composable
internal fun FloatCommand(
    command: FloatPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    val formattingRegexp = remember { Regex("\\.?0*$") }
    fun Float?.str(digits: Int = 6) =
        if (this == null) ""
        else String.format("%.${digits}f", this, Locale.ROOT)
            .replace(",", ".")
            .replace(formattingRegexp, "")
    NumberCommand(
        valueState = command.valueAsState(),
        name = command.name,
        minValue = command.min,
        maxValue = command.max,
        nullable = command.nullable,
        modifier = modifier,
        toFloat = { it },
        fromFloat = { it },
        toString = { it?.str() ?: "" },
        fromString = { it.toFloatOrNull() },
        toDisplayValue = { it?.str(2) ?: "null" },
        onChanged = {
            command.value = it
            onChanged()
        })
}