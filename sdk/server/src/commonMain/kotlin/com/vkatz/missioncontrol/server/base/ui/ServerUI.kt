package com.vkatz.missioncontrol.server.base.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.ValueCommand
import com.vkatz.missioncontrol.server.base.MissionControlServer
import com.vkatz.missioncontrol.server.base.connection.AbsConnection
import com.vkatz.missioncontrol.server.base.ui.commands.Command
import com.vkatz.missioncontrol.server.base.ui.components.PlatformVerticalScrollbar
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import androidx.compose.material.icons.Icons.Default as DefIcons

@Composable
fun ServerUI(
    server: MissionControlServer,
    modifier: Modifier = Modifier,
    theme: (@Composable (@Composable () -> Unit) -> Unit)? = null
) {
    if (theme != null) theme {
        Surface(modifier) {
            ServerUIBody(server)
        }
    } else MissionControlTheme {
        Surface(modifier) {
            ServerUIBody(server)
        }
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
    var groupItems by remember { mutableStateOf(true) }
    if (connections.isNotEmpty()) Column(Modifier.fillMaxSize()) {
        Surface(
            shadowElevation = 4.dp,
            tonalElevation = 8.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConnectionsTabs(connections, activeConnection) {
                    activeConnection = it
                }
                IconButton(onClick = { groupItems = !groupItems }) {
                    Icon(if (groupItems) DefIcons.ViewAgenda else DefIcons.TableRows, null)
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        key(activeConnection) {
            if (activeConnection != null)
                ConnectionUI(activeConnection!!, groupItems)
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
private fun RowScope.ConnectionsTabs(
    connections: List<AbsConnection>,
    activeConnection: AbsConnection?,
    onClick: (AbsConnection) -> Unit
) {
    val selected = connections.indexOf(activeConnection).let { if (it < 0) 0 else it }
    ScrollableTabRow(
        selectedTabIndex = selected,
        modifier = Modifier.weight(1f),
        edgePadding = 16.dp,
        divider = {},
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        connections.onEach { item ->
            Tab(
                selected = item == activeConnection,
                onClick = { onClick(item) },
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .sizeIn(minHeight = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.name)
                }
            }
        }
    }
}

@Composable
private fun ConnectionUI(
    connection: AbsConnection,
    groupItems: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    var invalidateKey by remember { mutableStateOf(0) }
    var commands by remember {
        mutableStateOf<List<ValueCommand<*>>>(
            emptyList(),
            neverEqualPolicy()
        )
    }
    DisposableEffect(Unit) {
        connection.setOnCommandsChangeListener { commands = it; invalidateKey++ }
        onDispose { connection.setOnCommandsChangeListener(null) }
    }
    Box(Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        val stableCommands = remember(groupItems, invalidateKey) {
            if (!groupItems) commands
                .sortedBy { v -> v.orderId }
                .toImmutableList()
            else commands
                .groupBy { it.group }
                .map { it.key to it.value.sortedBy { v -> v.orderId } }
                .sortedBy { it.second[0].orderId }
                .flatMap { it.second }
                .toImmutableList()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            state = listState
        ) {
            itemsIndexed(
                items = stableCommands,
                key = { _, i -> i.uuid }
            ) { index, item ->
                fun near(index: Int) = if (index in stableCommands.indices) stableCommands[index] else null
                val isGroupStart = near(index - 1)?.group != item.group
                val isGroupEnd = near(index + 1)?.group != item.group
                val topCorner by animateDpAsState(if (isGroupStart || !groupItems) 16.dp else 0.dp)
                val bottomCorner by animateDpAsState(if (isGroupEnd || !groupItems) 16.dp else 0.dp)
                val shape = remember(topCorner, bottomCorner) {
                    RoundedCornerShape(
                        topStart = topCorner,
                        topEnd = topCorner,
                        bottomStart = bottomCorner,
                        bottomEnd = bottomCorner
                    )
                }

                AnimatedVisibility(
                    visible = groupItems && isGroupStart && item.group.isNotBlank(),
                    enter = fadeIn() + expandIn(expandFrom = Alignment.CenterStart),
                    exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.CenterStart)
                ) {
                    Text(
                        text = item.group,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = shape,
                    shadowElevation = 4.dp,
                    tonalElevation = 8.dp,
                ) {
                    Column {
                        Command(item, Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp)) {
                            scope.launch {
                                connection.sendCommand(item)
                            }
                        }
                        AnimatedVisibility(groupItems && !isGroupEnd) {
                            Divider()
                        }
                    }
                }
                AnimatedVisibility(!groupItems && index != stableCommands.lastIndex) {
                    Spacer(Modifier.size(8.dp))
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