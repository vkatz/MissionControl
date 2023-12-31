@file:Suppress("MatchingDeclarationName")

package com.vkatz.missioncontrol.server.base.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command
import com.vkatz.missioncontrol.common.ValueCommand
import kotlin.math.absoluteValue
import kotlin.math.ceil

@Composable
fun <C, T> C.valueAsState(): State<T> where C : Command, C : ValueCommand<T> {
    val state = remember { mutableStateOf(value, neverEqualPolicy()) }
    SideEffect { state.value = value }
    return state
}

@JvmInline
value class HSLColor(private val data: FloatArray) {
    constructor (
        h: Float = 0f,
        s: Float = 0f,
        l: Float = 0f,
        a: Float = 1f
    ) : this(floatArrayOf(h, s, l, a))

    var h: Float
        get() = data[0]
        set(value) {
            data[0] = value
        }

    var s: Float
        get() = data[1]
        set(value) {
            data[1] = value
        }

    var l: Float
        get() = data[2]
        set(value) {
            data[2] = value
        }

    var a: Float
        get() = data[3]
        set(value) {
            data[3] = value
        }
}

@OptIn(ExperimentalStdlibApi::class)
fun Color.toHex() = toArgb().toHexString(
    format = HexFormat {
        upperCase = true
    }
)

internal fun Color.toHsl(): HSLColor {
    val r: Float = this.red
    val g: Float = this.green
    val b: Float = this.blue
    val min = minOf(r, g, b)
    val max = maxOf(r, g, b)
    val delta = max - min
    var h = 0f
    var s = 0f
    val l = (max + min) / 2f
    if (delta.absoluteValue > 0.0001f) {
        s = if (l < 0.5) delta / (max + min) else delta / (2f - max - min)
        val deltaR = (((max - r) / 6f) + (delta / 2f)) / delta
        val deltaG = (((max - g) / 6f) + (delta / 2f)) / delta
        val deltaB = (((max - b) / 6f) + (delta / 2f)) / delta
        when {
            (r - max).absoluteValue < 0.0001f -> h = deltaB - deltaG
            (g - max).absoluteValue < 0.0001f -> h = (1f / 3f) + deltaR - deltaB
            (b - max).absoluteValue < 0.0001f -> h = (2f / 3f) + deltaG - deltaR
        }
        if (h < 0f) h += 1f
        else if (h > 1f) h -= 1f
    }
    return HSLColor(
        h = h * 360f,
        s = s,
        l = l,
        a = alpha
    )
}

internal fun HSLColor.toRgba() = Color.hsl(hue = h, saturation = s, lightness = l, alpha = a)

internal fun Modifier.drawTransparencyGrid() = drawWithCache {
    onDrawBehind {
        val cellWidth = 8.dp.toPx()
        val cellHeight = 8.dp.toPx()
        val cellSize = Size(cellWidth, cellHeight)

        val horizontalSteps = ceil(size.width / cellWidth).toInt()
        val verticalSteps = ceil(size.height / cellHeight).toInt()

        for (y in 0..verticalSteps) {
            for (x in 0..horizontalSteps) {
                val isGrayTile = ((x + y) % 2 == 1)
                drawRect(
                    color = if (isGrayTile) Color.LightGray else Color.White,
                    topLeft = Offset(x * cellWidth, y * cellHeight),
                    size = cellSize
                )
            }
        }
    }
}