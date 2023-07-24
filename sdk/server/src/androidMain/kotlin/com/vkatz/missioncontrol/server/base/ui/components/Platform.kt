package com.vkatz.missioncontrol.server.base.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun PlatformVerticalScrollbar(
    state: LazyListState,
    modifier: Modifier,
    styleMinimalHeight: Dp,
    styleThickness: Dp,
    styleShape: Shape,
    styleHoverDurationMillis: Int,
    styleUnhoverColor: Color,
    styleHoverColor: Color
) = Unit

@Composable
actual fun PlatformOptionDialog(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit
) {
    if (visible) Dialog(
        onDismissRequest = onCloseRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    content()
                }
            }
        }
    }
}