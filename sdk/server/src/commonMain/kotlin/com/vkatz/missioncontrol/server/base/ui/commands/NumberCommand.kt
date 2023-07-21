package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.server.base.ui.components.CompactTextField

@Composable
@OptIn(ExperimentalComposeUiApi::class)
internal fun <T> NumberCommand(
    valueState: State<T?>,
    name: String,
    minValue: T?,
    maxValue: T?,
    steps: Int = 0,
    nullable: Boolean,
    modifier: Modifier = Modifier,
    toFloat: (T?) -> Float?,
    fromFloat: (Float?) -> T?,
    toString: (T?) -> String,
    fromString: (String) -> T?,
    toDisplayValue: (T?) -> String,
    onChanged: (T?) -> Unit
) where T : Number, T : Comparable<T> {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val value by valueState
        var displayValue by remember(value) { mutableStateOf(toDisplayValue(value)) }

        Text(name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(8.dp))
        val min = toFloat(minValue)
        val max = toFloat(maxValue)
        if (min != null && max != null && min < max) {
            var isNull by remember(value) { mutableStateOf(nullable && value == null) }
            var state by remember { mutableStateOf(toFloat(value) ?: 0f) }
            val alpha by animateFloatAsState(if (isNull) 0.25f else 1f)

            LaunchedEffect(value) { if (value != null) state = toFloat(value) ?: 0f }

            Text("$min", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.size(8.dp))
            Slider(
                value = state,
                modifier = Modifier.weight(1f).graphicsLayer { this.alpha = alpha },
                onValueChange = {
                    isNull = false
                    state = it
                    val newValue = fromFloat(it)
                    displayValue = toDisplayValue(newValue)
                    onChanged(newValue)
                },
                valueRange = min..max,
                steps = steps
            )
            Spacer(Modifier.size(8.dp))
            Text("$max", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.size(8.dp))
            if (nullable) {
                Spacer(Modifier.size(8.dp))
                IconButton(
                    onClick = {
                        isNull = true
                        displayValue = toDisplayValue(null)
                        onChanged(null)
                    },
                ) {
                    Icon(Icons.Default.Clear, null)
                }
            }
        } else {
            var input by remember(value) { mutableStateOf(toString(value)) }
            fun submit() {
                val parsedValue = if (input.isBlank()) null else fromString(input) ?: value
                val newValue = if (nullable) parsedValue else parsedValue ?: value
                displayValue = toDisplayValue(newValue)
                onChanged(newValue)
            }
            CompactTextField(
                value = input,
                onValueChange = {
                    input = when {
                        it.isBlank() -> it.trim()
                        fromString(it) != null -> it
                        else -> input
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .onPreviewKeyEvent {
                        if (it.key == Key.Enter || it.key == Key.NumPadEnter) {
                            submit()
                            true
                        } else false
                    },
                contentPaddings = PaddingValues(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onAny = { submit() })
            )
            Spacer(Modifier.size(8.dp))
            IconButton(
                enabled = input.isNotBlank() || !nullable,
                onClick = { input = if (nullable) "" else toString(value) },
                content = {
                    val icon = if (nullable) Icons.Default.Clear else Icons.Default.Replay
                    Icon(icon, null)
                }
            )
            Spacer(Modifier.size(8.dp))
            IconButton(
                onClick = { submit() },
                content = { Icon(Icons.Default.Send, null) }
            )
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = displayValue,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .sizeIn(minWidth = 36.dp)
        )
    }
}