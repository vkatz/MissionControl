package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.OptionPropertyUpdate
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
        val value = command.valueAsState()
        Text(command.name, style = MaterialTheme.typography.titleMedium)
        LazyColumn(userScrollEnabled = false, modifier = Modifier.heightIn(max = 500.dp)) {
            items(command.options) {
                Button({ }) {
                    Text(it)
                }
            }
        }
    }
}