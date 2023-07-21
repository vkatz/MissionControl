package com.vkatz.missioncontrol.server.base.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun SliderContainer(
    name: String,
    description: String,
    modifier: Modifier = Modifier,
    nameWidth: Dp = Dp.Unspecified,
    nameColor: Color = Color.LightGray,
    descriptionWidth: Dp = 42.dp,
    descriptionColor: Color = Color.LightGray,
    slider: @Composable () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.width(nameWidth),
            style = MaterialTheme.typography.bodyMedium,
            text = name,
            textAlign = TextAlign.Center,
            color = nameColor
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            slider()
        }
        Text(
            modifier = Modifier.width(descriptionWidth),
            style = MaterialTheme.typography.bodyMedium,
            text = description,
            textAlign = TextAlign.Center,
            color = descriptionColor
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SliderCompact(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(30.dp),
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        thumb = remember(interactionSource, colors, enabled) {
            {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    colors = colors,
                    enabled = enabled,
                    modifier = Modifier.scale(.66f)
                )
            }
        },
        track = remember(colors, enabled) {
            { sliderPositions ->
                SliderDefaults.Track(
                    colors = colors,
                    enabled = enabled,
                    sliderPositions = sliderPositions,
                    modifier = Modifier.scale(1.0f, .66f)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SliderColorfulCompact(
    value: Float,
    brush: Brush,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(thumbColor = LocalContentColor.current),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    drawTransparencyGrid: Boolean = false,
    trackSize: Dp = 4.dp
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        thumb = remember(interactionSource, colors, enabled) {
            {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    colors = colors,
                    enabled = enabled,
                    modifier = Modifier
                )
            }
        },
        track = { _ ->
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackSize)
            ) {
                if (drawTransparencyGrid) {
                    val cellCount = 40
                    val cellWidth = size.width / cellCount

                    for (x in 0 until cellCount) {
                        val isGrayTile = (x % 2 == 1)

                        drawLine(
                            color = if (isGrayTile) Color.LightGray else Color.White,
                            start = Offset(x * cellWidth, center.y),
                            end = Offset(x * cellWidth + cellWidth, center.y),
                            strokeWidth = size.height,
                            cap = if (x == 0 || x == cellCount - 1) {
                                StrokeCap.Round
                            } else {
                                StrokeCap.Butt
                            }
                        )
                    }
                }

                drawLine(
                    brush = brush,
                    start = Offset(0f, center.y),
                    end = Offset(size.width, center.y),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }
        }
    )
}