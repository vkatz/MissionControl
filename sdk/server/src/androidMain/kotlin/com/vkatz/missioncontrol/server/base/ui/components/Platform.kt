package com.vkatz.missioncontrol.server.base.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

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