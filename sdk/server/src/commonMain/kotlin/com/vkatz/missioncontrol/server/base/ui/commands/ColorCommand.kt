package com.vkatz.missioncontrol.server.base.ui.commands

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vkatz.missioncontrol.common.Command.ColorPropertyUpdate
import com.vkatz.missioncontrol.server.base.ui.components.CompactTextField
import com.vkatz.missioncontrol.server.base.ui.components.HSLPanel
import com.vkatz.missioncontrol.server.base.ui.components.RGBPanel
import com.vkatz.missioncontrol.server.base.ui.drawTransparencyGrid
import com.vkatz.missioncontrol.server.base.ui.toHex
import com.vkatz.missioncontrol.server.base.ui.valueAsState
import okhttp3.internal.toHexString

private enum class ColorSpace {
    RGB,
    HSL
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalStdlibApi::class)
internal fun ColorCommand(
    command: ColorPropertyUpdate,
    modifier: Modifier = Modifier,
    onChanged: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        val value by command.valueAsState()
        var state by remember(value) { mutableStateOf(Color(command.value)) }
        var colorSpace by remember { mutableStateOf(ColorSpace.RGB) }

        Text(command.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var input by remember(state) { mutableStateOf(state.toHex()) }
            val filter = remember { Regex("[^0-9A-F]") }
            val focusManager = LocalFocusManager.current

            fun submit() {
                val newValue = input.hexToInt()
                state = Color(newValue)
                command.value = newValue
                onChanged()
            }

            CompactTextField(
                value = input,
                modifier = Modifier
                    .weight(0.8f)
                    .onPreviewKeyEvent {
                        if (it.key == Key.Enter || it.key == Key.NumPadEnter) {
                            submit()
                            true
                        } else false
                    },
                maxLines = 1,
                onValueChange = {
                    input = it.take(8).uppercase().replace(filter, "")
                },
                contentPaddings = PaddingValues(8.dp),
                visualTransformation = HexTransformation("#"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onAny = {
                        focusManager.clearFocus()
                        runCatching { submit() }
                    }
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .drawTransparencyGrid()
                    .border(1.dp, Color.White, MaterialTheme.shapes.extraSmall)
                    .background(state)
            )
        }
        Spacer(Modifier.size(8.dp))
        BoxWithConstraints {
            val colorSpaceValues = remember { ColorSpace.values() }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                items(colorSpaceValues) {
                    val color by animateColorAsState(
                        if (it == colorSpace) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .widthIn(min = maxWidth / colorSpaceValues.size)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { colorSpace = it }
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = color
                    )
                }
            }
        }
        val onColorChanged: (Color) -> Unit = {
            state = it
            command.value = state.toArgb()
            onChanged()
        }
        when (colorSpace) {
            ColorSpace.RGB -> RGBPanel(state, onColorChanged)
            ColorSpace.HSL -> HSLPanel(state, onColorChanged)
        }
    }
}


private class HexTransformation(private val prefix: String) : VisualTransformation, OffsetMapping {

    companion object {
        private const val MASK = "AARRGGBB"
    }

    private var textSize = 0

    override fun originalToTransformed(offset: Int) = offset + prefix.length

    override fun transformedToOriginal(offset: Int): Int {
        return when {
            offset < prefix.length -> 0
            offset - prefix.length > textSize -> textSize
            else -> offset - prefix.length
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        textSize = text.length
        return TransformedText(
            text = buildAnnotatedString {
                append(prefix + text.text)
                pushStyle(SpanStyle(color = Color.LightGray.copy(alpha = 0.3f)))
                append(MASK.takeLast(MASK.length - text.length))
            },
            offsetMapping = this
        )
    }
}