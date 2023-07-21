package com.vkatz.missioncontrol.server.base.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.server.base.MissionControlServer
import com.vkatz.missioncontrol.server.base.connection.AbsConnection
import com.vkatz.missioncontrol.server.base.ui.commands.Command
import com.vkatz.missioncontrol.server.base.ui.components.PlatformVerticalScrollbar
import kotlinx.coroutines.launch

@Composable
fun ServerUI(
    server: MissionControlServer,
    modifier: Modifier = Modifier,
    theme: (@Composable (@Composable () -> Unit) -> Unit)? = null
) {
    if (theme != null) theme {
        ServerUIBody(server)
    } else MissionControlTheme(modifier) {
        ServerUIBody(server)
    }
}

@Composable
private fun ServerUIBody(
    server: MissionControlServer,
) {
    var connections by remember {
        mutableStateOf<List<AbsConnection>>(
            emptyList(),
            neverEqualPolicy()
        )
    }
    var activeConnection by remember { mutableStateOf<AbsConnection?>(null, neverEqualPolicy()) }
    DisposableEffect(Unit) {
        server.setOnConnectionsChangedListener {
            connections = it
            if (activeConnection !in connections)
                activeConnection = connections.firstOrNull()
        }
        onDispose { server.setOnConnectionsChangedListener(null) }
    }
    if (connections.isNotEmpty()) Column(Modifier.fillMaxSize()) {
        if (connections.size > 1) {
            ConnectionsTabs(connections, activeConnection) {
                activeConnection = it
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
        key(activeConnection) {
            if (activeConnection != null)
                ConnectionUI(activeConnection!!)
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active connections")
        }
    }
}

@Composable
private fun ConnectionsTabs(
    connections: List<AbsConnection>,
    activeConnection: AbsConnection?,
    onClick: (AbsConnection) -> Unit
) {
    val selected = connections.indexOf(activeConnection).let { if (it < 0) 0 else it }
    ScrollableTabRow(
        selectedTabIndex = selected,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 16.dp,
        divider = {},
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        connections.onEach { item ->
            Tab(
                selected = item == activeConnection,
                onClick = { onClick(item) },
            ) {
                Text(
                    item.name,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ConnectionUI(connection: AbsConnection) {
    val scope = rememberCoroutineScope()
    var commands by remember {
        mutableStateOf<List<Command>>(
            emptyList(),
            neverEqualPolicy()
        )
    }
    DisposableEffect(Unit) {
        connection.setOnCommandsChangeListener { commands = it }
        onDispose { connection.setOnCommandsChangeListener(null) }
    }
    Box(Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            state = listState
        ) {
            items(commands, { it.uuid }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large,
                    shadowElevation = 4.dp,
                    tonalElevation = 8.dp
                ) {
                    Box(Modifier.padding(8.dp)) {
                        Command(it, Modifier.fillMaxWidth()) {
                            scope.launch {
                                connection.sendCommand(it)
                            }
                        }
                    }
                }
            }
        }
        PlatformVerticalScrollbar(
            state = listState,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            styleMinimalHeight = 16.dp,
            styleThickness = 4.dp,
            styleShape = RoundedCornerShape(1.dp),
            styleHoverDurationMillis = 300,
            styleUnhoverColor = LocalContentColor.current.copy(alpha = 0.12f),
            styleHoverColor = LocalContentColor.current.copy(alpha = 0.50f),
        )
    }
}