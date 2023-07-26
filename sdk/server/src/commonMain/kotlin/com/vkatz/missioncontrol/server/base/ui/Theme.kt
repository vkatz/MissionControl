package com.vkatz.missioncontrol.server.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NOT_SPECIFIED = Color(0xFFFF0000)

@Composable
internal fun MissionControlTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF4A88C7),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF3D4B5C),
            onPrimaryContainer = Color(0xFFBBBBBB),
            inversePrimary = NOT_SPECIFIED,
            secondary = NOT_SPECIFIED,
            onSecondary = NOT_SPECIFIED,
            secondaryContainer = NOT_SPECIFIED,
            onSecondaryContainer = NOT_SPECIFIED,
            tertiary = NOT_SPECIFIED,
            onTertiary = NOT_SPECIFIED,
            tertiaryContainer = NOT_SPECIFIED,
            onTertiaryContainer = NOT_SPECIFIED,
            background = NOT_SPECIFIED,
            onBackground = NOT_SPECIFIED,
            surface = Color(0xFF2B2D30),
            onSurface = Color(0xFFBBBBBB),
            surfaceVariant = Color(0xFF555555),
            onSurfaceVariant = Color(0xFFBBBBBB),
            surfaceTint = Color(0xFFBBBBBB),
            inverseSurface = NOT_SPECIFIED,
            inverseOnSurface = NOT_SPECIFIED,
            error = Color(0xFFFF5261),
            onError = Color(0xFFBBBBBB),
            errorContainer = Color(0xFF593D41),
            onErrorContainer = Color(0xFFBBBBBB),
            outline = Color(0xFF5E6060),
            outlineVariant = Color(0xFF5E6060),
            scrim = NOT_SPECIFIED,
        ),
        content = content
    )
}