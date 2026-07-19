package com.quangthe.nhatky.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.quangthe.nhatky.commons.utils.ColorUtils
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.helper.SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP

// Custom extra colors not present in standard Material3 ColorScheme
data class AppColors(
    val screenBackground: Color = Color.Unspecified,
    val toolbarBackground: Color = Color.Unspecified,
    val toolbarContent: Color = Color.White
)

val LocalAppColors = staticCompositionLocalOf { AppColors() }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val config = context.config
    
    // Dynamic colors based on user settings
    val primaryColor = Color(ColorUtils.adjustAlpha(config.primaryColor, 1f))
    val backgroundColor = Color(config.backgroundColor)
    val textColor = Color(config.textColor)
    val screenBackgroundColor = Color(config.screenBackgroundColor)

    val colorScheme = lightColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        background = backgroundColor,
        onBackground = textColor,
        surface = backgroundColor,
        onSurface = textColor,
        surfaceVariant = screenBackgroundColor,
        onSurfaceVariant = textColor.copy(alpha = 0.7f),
        outline = primaryColor.copy(alpha = 0.5f)
    )

    val customColors = AppColors(
        screenBackground = screenBackgroundColor,
        toolbarBackground = primaryColor,
        toolbarContent = Color.White
    )

    val density = LocalDensity.current
    val fontSizePx = config.settingFontSize
    val fontSizeSp = with(density) { fontSizePx.toSp() }
    val lineSpacing = config.lineSpacingScaleFactor
    
    val baseStyle = TextStyle(
        fontSize = fontSizeSp,
        lineHeight = fontSizeSp * 1.3f * lineSpacing,
        fontWeight = if (config.boldStyleEnable) FontWeight.Bold else FontWeight.Normal,
        color = textColor
    )

    val titleStyle = TextStyle(
        fontSize = fontSizeSp * 1.2f, // 20% larger than content
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = textColor
    )

    val typography = Typography(
        bodyLarge = baseStyle,
        bodyMedium = baseStyle,
        bodySmall = baseStyle.copy(fontSize = fontSizeSp * 0.8f),
        headlineLarge = titleStyle,
        headlineMedium = titleStyle,
        headlineSmall = titleStyle,
        titleLarge = baseStyle.copy(fontSize = fontSizeSp * 1.1f, fontWeight = FontWeight.Bold),
        titleMedium = baseStyle.copy(fontSize = fontSizeSp * 1.05f, fontWeight = FontWeight.Bold),
        titleSmall = baseStyle.copy(fontWeight = FontWeight.Bold)
    )

    CompositionLocalProvider(LocalAppColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}
