package com.vkatz.missioncontrol.server.base.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.vkatz.missioncontrol.server.base.ui.toHsl
import com.vkatz.missioncontrol.server.base.ui.toRgba
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
internal fun HSLPanel(
    color: Color,
    onColorChanged: (Color) -> Unit
) {
    Column {
        var hsl by remember { mutableStateOf(color.toHsl()) }

        LaunchedEffect(color) {
            // allow minimal precision loss during rgb->hsl->rgb transform
            val input = hsl.toRgba()
            val gap = 1.5f / 255f
            val dr = (color.red - input.red).absoluteValue
            val dg = (color.green - input.green).absoluteValue
            val db = (color.blue - input.blue).absoluteValue
            val da = (color.alpha - input.alpha).absoluteValue
            if (dr > gap || dg > gap || db > gap || da > gap) {
                hsl = color.toHsl()
            }
        }

        SliderContainer(name = "H", description = "${hsl.h.toInt()}°") {
            SliderColorfulCompact(
                value = hsl.h,
                valueRange = 0f..360f,
                steps = 358,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.hsl(hue = 0f, saturation = hsl.s, lightness = hsl.l),
                        Color.hsl(hue = 60f, saturation = hsl.s, lightness = hsl.l),
                        Color.hsl(hue = 120f, saturation = hsl.s, lightness = hsl.l),
                        Color.hsl(hue = 180f, saturation = hsl.s, lightness = hsl.l),
                        Color.hsl(hue = 240f, saturation = hsl.s, lightness = hsl.l),
                        Color.hsl(hue = 300f, saturation = hsl.s, lightness = hsl.l),
                        Color.hsl(hue = 360f, saturation = hsl.s, lightness = hsl.l)
                    )
                ),
                onValueChange = {
                    hsl = hsl.copy(h = it)
                    onColorChanged(hsl.toRgba())
                }
            )
        }
        SliderContainer(name = "S", description = "${(hsl.s * 100f).roundToInt()}%") {
            SliderColorfulCompact(
                value = hsl.s * 100f,
                valueRange = 0f..100f,
                steps = 98,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.hsl(hue = hsl.h, saturation = 0f, lightness = hsl.l),
                        Color.hsl(hue = hsl.h, saturation = 1f, lightness = hsl.l),
                    ),
                ),
                onValueChange = {
                    hsl = hsl.copy(s = it / 100f)
                    onColorChanged(hsl.toRgba())
                }
            )
        }
        SliderContainer(name = "L", description = "${(hsl.l * 100f).roundToInt()}%") {
            SliderColorfulCompact(
                value = hsl.l * 100f,
                valueRange = 0f..100f,
                steps = 98,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.hsl(hue = hsl.h, saturation = hsl.s, lightness = 0f),
                        Color.hsl(hue = hsl.h, saturation = hsl.s, lightness = .5f),
                        Color.hsl(hue = hsl.h, saturation = hsl.s, lightness = 1f)
                    )
                ),
                onValueChange = {
                    hsl = hsl.copy(l = it / 100f)
                    onColorChanged(hsl.toRgba())
                }
            )
        }

        SliderContainer(name = "A", description = "${(hsl.a * 100.0f).roundToInt()}") {
            SliderColorfulCompact(
                value = hsl.a * 100f,
                valueRange = 0f..100f,
                steps = 98,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.hsl(hue = hsl.h, saturation = hsl.s, lightness = hsl.l, alpha = 0f),
                        Color.hsl(hue = hsl.h, saturation = hsl.s, lightness = hsl.l, alpha = 1f)
                    ),
                ),
                onValueChange = {
                    hsl = hsl.copy(a = it / 100f)
                    onColorChanged(hsl.toRgba())
                },
                drawTransparencyGrid = true
            )
        }
    }
}

@Composable
internal fun RGBPanel(
    color: Color,
    onColorChanged: (Color) -> Unit
) {
    fun Float.rgbValue() = (this * 255f).toInt().toString()
    Column {
        SliderContainer(name = "R", nameColor = Color.Red, description = color.red.rgbValue()) {
            SliderColorfulCompact(
                value = color.red * 255f,
                valueRange = 0f..255f,
                brush = Brush.linearGradient(listOf(Color.Red, Color.Red)),
                onValueChange = {
                    onColorChanged(color.copy(red = it / 255f))
                }
            )
        }
        SliderContainer(name = "G", nameColor = Color.Green, description = color.green.rgbValue()) {
            SliderColorfulCompact(
                value = color.green * 255f,
                valueRange = 0f..255f,
                brush = Brush.linearGradient(listOf(Color.Green, Color.Green)),
                onValueChange = {
                    onColorChanged(color.copy(green = it / 255f))
                }
            )
        }
        SliderContainer(name = "B", nameColor = Color.Blue, description = color.blue.rgbValue()) {
            SliderColorfulCompact(
                value = color.blue * 255f,
                valueRange = 0f..255f,
                brush = Brush.linearGradient(listOf(Color.Blue, Color.Blue)),
                onValueChange = {
                    onColorChanged(color.copy(blue = it / 255f))
                }
            )
        }
        SliderContainer(name = "A", description = "${(color.alpha * 100.0f).roundToInt()}") {
            SliderColorfulCompact(
                value = color.alpha * 255f,
                valueRange = 0f..255f,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(color.red, color.green, color.blue, 0f),
                        Color(color.red, color.green, color.blue, 1f)
                    )
                ),
                onValueChange = {
                    onColorChanged(color.copy(alpha = it / 255f))
                },
                drawTransparencyGrid = true
            )
        }
    }
}