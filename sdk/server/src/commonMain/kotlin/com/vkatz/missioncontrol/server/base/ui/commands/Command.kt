package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vkatz.missioncontrol.common.Command.*
import com.vkatz.missioncontrol.common.ValueCommand

@Composable
internal fun Command(
    command: ValueCommand<*>,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    when (command) {
        is IntPropertyUpdate -> IntCommand(command, modifier, onChanged)
        is FloatPropertyUpdate -> FloatCommand(command, modifier, onChanged)
        is BooleanPropertyUpdate -> BooleanCommand(command, modifier, onChanged)
        is StringPropertyUpdate -> StringCommand(command, modifier, onChanged)
        is InfoPropertyUpdate -> InfoCommand(command, modifier)
        is ColorPropertyUpdate -> ColorCommand(command, modifier, onChanged)
        is OptionPropertyUpdate -> OptionCommand(command, modifier, onChanged)
        is ActionPropertyUpdate -> ActionCommand(command, modifier, onChanged)
        else -> NotImplementedCommand(command)
    }
}