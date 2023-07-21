package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.StringPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.components.CompactTextField
import com.vkatz.missioncontrol.server.base.ui.valueAsState

@Composable
@OptIn(ExperimentalComposeUiApi::class)
internal fun StringCommand(
    command: StringPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        val value by command.valueAsState()
        var input by remember(value) { mutableStateOf(value) }
        fun submit() {
            command.value = input
            onChanged()
        }
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(command.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            IconButton(
                enabled = value.isNotBlank(),
                onClick = { input = "" },
                content = { Icon(Icons.Default.Clear, null) }
            )
            Spacer(Modifier.size(8.dp))
            IconButton(
                enabled = value != input,
                onClick = { submit() },
                content = { Icon(Icons.Default.Send, null) }
            )
        }
        Spacer(Modifier.size(8.dp))
        CompactTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .fillMaxWidth()
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
    }
}