package vegabobo.dsusideloader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = Color(0xFF282375),
    primaryContainer = Color(0xFF302D66),
    onPrimaryContainer = Color(0xFFE5E2FF),
    secondary = Color(0xFFBCC5E8),
    onSecondary = Ink,
    secondaryContainer = Color(0xFF2B3046),
    onSecondaryContainer = Color(0xFFDCE2FA),
    tertiary = Color(0xFFA0DCCB),
    background = Night,
    onBackground = Color(0xFFE8E9F4),
    surface = Color(0xFF1C1E2D),
    onSurface = Color(0xFFE8E9F4),
    surfaceVariant = Color(0xFF2A2D40),
    onSurfaceVariant = Color(0xFFB5B8CC),
    inverseOnSurface = Color(0xFF1C1E2D),
    outline = Color(0xFF85889F),
    outlineVariant = Color(0xFF35384D),
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBE9FF),
    onPrimaryContainer = Color(0xFF302B78),
    secondary = Slate,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEBEDF6),
    onSecondaryContainer = Ink,
    tertiary = Color(0xFF267D69),
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF0F1F8),
    onSurfaceVariant = Slate,
    inverseOnSurface = Color.White,
    outline = Color(0xFF777B91),
    outlineVariant = Color(0xFFE2E4F0),
)

fun appearanceColorScheme(settings: AppearanceSettings, systemDark: Boolean): androidx.compose.material3.ColorScheme {
    val dark = settings.themeMode.isDark(systemDark)
    val palette = settings.accentColor.palette()
    val base = if (dark) DarkColorScheme else LightColorScheme
    val accented = base.copy(
        primary = if (dark) palette.darkPrimary else palette.lightPrimary,
        onPrimary = if (dark) palette.darkOnPrimary else Color.White,
        primaryContainer = if (dark) palette.darkContainer else palette.lightContainer,
        onPrimaryContainer = if (dark) palette.darkOnContainer else palette.lightOnContainer,
        secondary = if (dark) palette.darkPrimary else palette.lightPrimary,
        secondaryContainer = if (dark) palette.darkContainer else palette.lightContainer,
        onSecondaryContainer = if (dark) palette.darkOnContainer else palette.lightOnContainer,
        tertiary = if (dark) palette.darkPrimary else palette.lightPrimary,
        surfaceTint = if (dark) palette.darkPrimary else palette.lightPrimary,
    )
    return if (settings.themeMode == ThemeMode.AMOLED) accented.withBlackSurfaces() else accented
}

private fun androidx.compose.material3.ColorScheme.withBlackSurfaces() = copy(
    background = Color.Black,
    surface = Color.Black,
    inverseOnSurface = Color.Black,
    surfaceVariant = Color(0xFF171717),
    outlineVariant = Color(0xFF343434),
)

@Composable
fun DSUHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appearance: AppearanceSettings = AppearanceSettings(),
    content: @Composable () -> Unit,
) {
    val effectiveDark = appearance.themeMode.isDark(darkTheme)
    val context = LocalContext.current
    val colorScheme = if (appearance.useWallpaperColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamic = if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        if (appearance.themeMode == ThemeMode.AMOLED) dynamic.withBlackSurfaces() else dynamic
    } else {
        appearanceColorScheme(appearance, darkTheme)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !effectiveDark
                isAppearanceLightNavigationBars = !effectiveDark
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp),
        ),
        content = content,
    )
}
