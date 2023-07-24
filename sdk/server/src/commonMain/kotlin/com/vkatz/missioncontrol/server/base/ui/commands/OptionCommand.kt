package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.OptionPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.components.PlatformOptionDialog
import com.vkatz.missioncontrol.server.base.ui.valueAsState

@Composable
internal fun OptionCommand(
    command: OptionPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        val value by command.valueAsState()
        var isDialogVisible by remember { mutableStateOf(false) }

        Text(command.name, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.size(8.dp))
        Button({ isDialogVisible = true }) {
            Text(
                text = command.options[value.coerceIn(command.options.indices)],
                style = MaterialTheme.typography.bodyMedium
            )
        }
        PlatformOptionDialog(
            visible = isDialogVisible,
            onCloseRequest = { isDialogVisible = false },
            title = command.name,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(command.options) { index, item ->
                    Button(
                        onClick = {
                            isDialogVisible = false
                            command.value = index
                            onChanged()
                        },
                        enabled = index != value,
                        content = {
                            Text(item)
                        }
                    )
                }
            }
        }
    }
}