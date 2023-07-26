package com.vkatz.missioncontrol.client.debug

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.Command.*
import com.vkatz.missioncontrol.common.ValueCommand

@Composable
fun rememberMissionControlState(
    value: Int,
    name: String,
    range: ClosedRange<Int>? = null,
    group: String = "",
) = rememberMissionControlState {
    IntPropertyUpdate(value, name, false, range?.start, range?.endInclusive, group)
}.nn(value)

@Composable
fun rememberMissionControlState(
    value: Int?,
    name: String,
    range: ClosedRange<Int>? = null,
    group: String = "",
) = rememberMissionControlState {
    IntPropertyUpdate(value, name, true, range?.start, range?.endInclusive, group)
}

@Composable
fun rememberMissionControlState(
    value: Float,
    name: String,
    range: ClosedRange<Float>? = null,
    group: String = "",
) = rememberMissionControlState {
    FloatPropertyUpdate(value, name, false, range?.start, range?.endInclusive, group)
}.nn(value)

@Composable
fun rememberMissionControlState(
    value: Float?,
    name: String,
    range: ClosedRange<Float>? = null,
    group: String = "",
) = rememberMissionControlState {
    FloatPropertyUpdate(value, name, true, range?.start, range?.endInclusive, group)
}

@Composable
fun rememberMissionControlState(
    value: Boolean,
    name: String,
    group: String = "",
) = rememberMissionControlState {
    BooleanPropertyUpdate(value, name, false, group)
}.nn(value)

@Composable
fun rememberMissionControlState(
    value: Boolean?,
    name: String,
    group: String = "",
) = rememberMissionControlState {
    BooleanPropertyUpdate(value, name, true, group)
}

@Composable
fun rememberMissionControlState(
    value: String,
    name: String,
    group: String = "",
) = rememberMissionControlState {
    StringPropertyUpdate(value, name, group)
}

@Composable
fun rememberMissionControlState(
    value: Color,
    name: String,
    group: String = "",
): MutableState<Color> {
    val rawState = rememberMissionControlState {
        ColorPropertyUpdate(value.toArgb(), name, group)
    }
    val state = remember { mutableStateOf(Color(rawState.value)) }
    LaunchedEffect(state.value) {
        val newValue = state.value.toArgb()
        if (rawState.value != newValue)
            rawState.value = newValue
    }
    LaunchedEffect(rawState.value) {
        state.value = Color(rawState.value)
    }
    return state
}

@Composable
fun rememberMissionControlInfoState(
    value: String,
    name: String,
    group: String = "",
) = rememberMissionControlState {
    InfoPropertyUpdate(value, name, group)
}

@Composable
fun rememberMissionControlOptionState(
    value: Int,
    name: String,
    options: List<String>,
    group: String = "",
) = rememberMissionControlState {
    OptionPropertyUpdate(value, name, options, group)
}

@Composable
fun rememberMissionControlActionState(
    name: String,
    description: String,
    actionButton: String,
    group: String = "",
    action: (() -> Unit)? = null,
): State<Long> {
    val state = rememberMissionControlState {
        ActionPropertyUpdate(name, description, actionButton, group)
    }
    var skips by remember { mutableStateOf(1) }
    LaunchedEffect(state.value) {
        if (skips == 0) action?.invoke() else skips--
    }
    return state
}

// -------------- helpers --------------

@Composable
private fun <T, C> rememberMissionControlState(
    commandBuilder: () -> C,
): MutableState<T> where C : Command, C : ValueCommand<T> {
    val order = currentComposer.currentMarker
    val command = remember { commandBuilder().apply { orderId = -order } }
    val state = remember { mutableStateOf(command.value) }
    LaunchedEffect(Unit) {
        MissionControlClient.start()
        MissionControlClient.registerProperty(command) {
            state.value = command.value
        }
    }
    LaunchedEffect(state.value) {
        if (command.value != state.value) {
            command.value = state.value
            MissionControlClient.updateProperty(command)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            MissionControlClient.removeProperty(command)
        }
    }
    return state
}

@Composable
private fun <T> MutableState<T?>.nn(default: T): MutableState<T> {
    val state = remember { mutableStateOf(this.value ?: default) }
    LaunchedEffect(this.value) {
        if (state.value != this@nn.value)
            state.value = this@nn.value ?: default
    }
    LaunchedEffect(state.value) {
        if (state.value != this@nn.value)
            this@nn.value = state.value
    }
    return state
}