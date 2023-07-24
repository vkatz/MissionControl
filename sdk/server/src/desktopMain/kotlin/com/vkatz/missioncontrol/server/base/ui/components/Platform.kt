package com.vkatz.missioncontrol.server.base.ui.components

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState

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
) = VerticalScrollbar(
    adapter = rememberScrollbarAdapter(state),
    style = ScrollbarStyle(
        minimalHeight = styleMinimalHeight,
        thickness = styleThickness,
        shape = styleShape,
        hoverDurationMillis = styleHoverDurationMillis,
        unhoverColor = styleUnhoverColor,
        hoverColor = styleHoverColor
    ),
    modifier = modifier
)

@Composable
actual fun PlatformOptionDialog(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit
) {
    Dialog(
        visible = visible,
        onCloseRequest = onCloseRequest,
        title = title,
        state = rememberDialogState(width = 400.dp, height = 600.dp),
        content = {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }
            }
        }
    )
}