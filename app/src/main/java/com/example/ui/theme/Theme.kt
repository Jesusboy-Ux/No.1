package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val label: String, val description: String) {
    LIGHT("Light", "Ivory reading surfaces with deep teal typography"),
    DARK("Dark", "Deep teal workspace with luminous high-contrast text"),
    SYSTEM("System", "Automatically match device appearance settings")
}

@Immutable
data class GreenAiColors(
    val isDark: Boolean,
    // Base Surfaces
    val screenBackground: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val surfaceMuted: Color,
    val borderSubtle: Color,
    val borderStrong: Color,
    // Typography
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    // Brand & Accents
    val brandPrimary: Color,
    val brandAccent: Color,
    val brandAccentMuted: Color,
    val botanicalGreen: Color,
    // AI Response Workspace Tokens (Guaranteed high contrast & readability)
    val aiCardBg: Color,
    val aiCardBorder: Color,
    val aiTextParagraph: Color,
    val aiTextHeading: Color,
    val aiTextSubheading: Color,
    val aiBulletMarker: Color,
    val aiNumberMarker: Color,
    val aiBlockquoteBg: Color,
    val aiBlockquoteBorder: Color,
    val aiBlockquoteText: Color,
    val aiInlineCodeBg: Color,
    val aiInlineCodeText: Color,
    val aiCodeBlockBg: Color,
    val aiCodeBlockText: Color,
    val aiCodeBlockBorder: Color,
    val aiTableBorder: Color,
    val aiTableHeaderBg: Color,
    val aiTableHeaderText: Color,
    val aiTableRowBg: Color,
    val aiTableRowAltBg: Color,
    val aiTableCellText: Color,
    val aiCitationBg: Color,
    val aiCitationText: Color,
    val aiCitationBorder: Color,
    val aiLinkText: Color,
    // Navigation & Toolbars
    val topBarBg: Color,
    val topBarText: Color,
    val bottomNavBg: Color,
    val bottomNavBorder: Color,
    val bottomNavSelected: Color,
    val bottomNavUnselected: Color,
    // Badges & Alerts
    val alertBg: Color,
    val alertBorder: Color,
    val alertText: Color,
    val warningBannerBg: Color = alertBg,
    val warningBannerBorder: Color = alertBorder,
    val warningBannerText: Color = alertText
)

val LightGreenAiTokens = GreenAiColors(
    isDark = false,
    screenBackground = WarmBotanicalIvory,
    surfaceCard = PureWhite,
    surfaceElevated = PureWhite,
    surfaceMuted = WarmSurfaceMuted,
    borderSubtle = SubtleBorderNeutral,
    borderStrong = Color(0xFFCBC8BA),
    textPrimary = PrimaryDeepTeal,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    brandPrimary = PrimaryDeepTeal,
    brandAccent = AccentLime,
    brandAccentMuted = AccentLimeDark,
    botanicalGreen = SecondaryBotanicalGreen,
    // AI Specific Tokens - Light Theme (Dark text on light surfaces)
    aiCardBg = PureWhite,
    aiCardBorder = SubtleBorderNeutral,
    aiTextParagraph = Color(0xFF0B2E34),
    aiTextHeading = Color(0xFF061B1F),
    aiTextSubheading = Color(0xFF13424B),
    aiBulletMarker = Color(0xFF0E7490),
    aiNumberMarker = Color(0xFF0B2E34),
    aiBlockquoteBg = WarmBotanicalIvory,
    aiBlockquoteBorder = PrimaryDeepTeal,
    aiBlockquoteText = Color(0xFF1A4750),
    aiInlineCodeBg = WarmSurfaceMuted,
    aiInlineCodeText = Color(0xFF0B2E34),
    aiCodeBlockBg = Color(0xFF0B2E34),
    aiCodeBlockText = AccentLime,
    aiCodeBlockBorder = Color(0xFF1A4750),
    aiTableBorder = SubtleBorderNeutral,
    aiTableHeaderBg = WarmSurfaceMuted,
    aiTableHeaderText = PrimaryDeepTeal,
    aiTableRowBg = PureWhite,
    aiTableRowAltBg = WarmBotanicalIvory,
    aiTableCellText = Color(0xFF0B2E34),
    aiCitationBg = Color(0xFFEEF7F8),
    aiCitationText = PrimaryDeepTeal,
    aiCitationBorder = Color(0xFFB8D8DC),
    aiLinkText = Color(0xFF0284C7),
    topBarBg = PrimaryDeepTeal,
    topBarText = PureWhite,
    bottomNavBg = PureWhite,
    bottomNavBorder = SubtleBorderNeutral,
    bottomNavSelected = PrimaryDeepTeal,
    bottomNavUnselected = Color(0xFF6B868C),
    alertBg = SafetyAlertBg,
    alertBorder = SafetyAlertBorder,
    alertText = SafetyAlertText
)

val DarkGreenAiTokens = GreenAiColors(
    isDark = true,
    screenBackground = DeepTealDark,
    surfaceCard = DeepTealSurface,
    surfaceElevated = DeepTealContainer,
    surfaceMuted = Color(0xFF072126),
    borderSubtle = BorderTealMuted,
    borderStrong = Color(0xFF235A66),
    textPrimary = TextOnDark,
    textSecondary = TextOnDarkMuted,
    textMuted = Color(0xFF7A9BA3),
    brandPrimary = AccentLime,
    brandAccent = AccentLime,
    brandAccentMuted = AccentLimeDark,
    botanicalGreen = SecondaryBotanicalGreen,
    // AI Specific Tokens - Dark Theme (Light high-contrast text on deep teal surfaces)
    aiCardBg = DeepTealSurface,
    aiCardBorder = BorderTealMuted,
    aiTextParagraph = TextOnDark,
    aiTextHeading = AccentLime,
    aiTextSubheading = Color(0xFF6EE7B7),
    aiBulletMarker = AccentLime,
    aiNumberMarker = AccentLime,
    aiBlockquoteBg = DeepTealContainer,
    aiBlockquoteBorder = AccentLime,
    aiBlockquoteText = Color(0xFFE2EFF2),
    aiInlineCodeBg = DeepTealContainer,
    aiInlineCodeText = AccentLime,
    aiCodeBlockBg = Color(0xFF041215),
    aiCodeBlockText = AccentLime,
    aiCodeBlockBorder = BorderTealMuted,
    aiTableBorder = BorderTealMuted,
    aiTableHeaderBg = DeepTealContainer,
    aiTableHeaderText = AccentLime,
    aiTableRowBg = DeepTealSurface,
    aiTableRowAltBg = Color(0xFF0B292F),
    aiTableCellText = TextOnDark,
    aiCitationBg = DeepTealContainer,
    aiCitationText = AccentLime,
    aiCitationBorder = BorderTealMuted,
    aiLinkText = Color(0xFF38BDF8),
    topBarBg = DeepTealDark,
    topBarText = PureWhite,
    bottomNavBg = DeepTealDark,
    bottomNavBorder = BorderTealMuted,
    bottomNavSelected = AccentLime,
    bottomNavUnselected = TextOnDarkMuted,
    alertBg = SafetyAlertDarkBg,
    alertBorder = SafetyAlertDarkBorder,
    alertText = SafetyAlertDarkText
)

val LocalGreenAiColors = staticCompositionLocalOf { LightGreenAiTokens }

object GreenAiTheme {
    val colors: GreenAiColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGreenAiColors.current
}

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDeepTeal,
    onPrimary = PureWhite,
    primaryContainer = DeepTealContainer,
    onPrimaryContainer = AccentLime,
    secondary = SecondaryBotanicalGreen,
    onSecondary = PrimaryDeepTeal,
    secondaryContainer = WarmSurfaceMuted,
    onSecondaryContainer = PrimaryDeepTeal,
    tertiary = AccentLime,
    onTertiary = PrimaryDeepTeal,
    background = WarmBotanicalIvory,
    onBackground = TextPrimaryDark,
    surface = PureWhite,
    onSurface = TextPrimaryDark,
    surfaceVariant = WarmSurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = SubtleBorderNeutral,
    outlineVariant = SubtleBorderNeutral
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentLime,
    onPrimary = DeepTealDark,
    primaryContainer = DeepTealContainer,
    onPrimaryContainer = PureWhite,
    secondary = SecondaryBotanicalGreen,
    onSecondary = PrimaryDeepTeal,
    secondaryContainer = DeepTealSurface,
    onSecondaryContainer = AccentLime,
    tertiary = AccentLime,
    onTertiary = PrimaryDeepTeal,
    background = DeepTealDark,
    onBackground = TextOnDark,
    surface = DeepTealSurface,
    onSurface = TextOnDark,
    surfaceVariant = DeepTealContainer,
    onSurfaceVariant = TextOnDarkMuted,
    outline = BorderTealMuted,
    outlineVariant = BorderTealMuted
)

@Composable
fun GreenAiTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemDark
    }

    val tokens = if (isDark) DarkGreenAiTokens else LightGreenAiTokens
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalGreenAiColors provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
