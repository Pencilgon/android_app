package com.example.lma.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ArchiveAccent,
    onPrimary = ArchiveBackground,
    secondary = Color(0xFF80CBC4),
    onSecondary = ArchiveBackground,
    tertiary = Color(0xFFC7F36B),
    background = ArchiveBackground,
    onBackground = ArchiveText,
    surface = ArchiveSurface,
    onSurface = ArchiveText,
    surfaceVariant = ArchiveSurfaceVariant,
    onSurfaceVariant = ArchiveMutedText
)

@Composable
fun LostMediaArchiveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}