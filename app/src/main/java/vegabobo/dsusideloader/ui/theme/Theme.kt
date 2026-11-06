package vegabobo.dsusideloader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import com.materialkolor.dynamiccolor.ColorSpec
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.ColorSpecVersion
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.preferences.ThemeMode
import vegabobo.dsusideloader.preferences.effectiveColorSpec

fun appearanceColorScheme(
    settings: AppearanceSettings,
    systemDark: Boolean,
    seedColor: Color = if (settings.keyColor != 0) Color(settings.keyColor) else Color(0xFF3482FF),
): ColorScheme {
    val dark = settings.themeMode.isDark(systemDark)
    val scheme = if (settings.monetEnabled) {
        dynamicColorScheme(
            seedColor = seedColor,
            isDark = dark,
            isAmoled = settings.themeMode == ThemeMode.AMOLED,
            style = PaletteStyle.valueOf(settings.paletteVariant.preferenceValue),
            specVersion = ColorSpec.SpecVersion.valueOf(settings.effectiveColorSpec().name),
        )
    } else {
        val palette = settings.accentColor.palette()
        (if (dark) darkColorScheme() else lightColorScheme()).copy(
            primary = if (dark) palette.darkPrimary else palette.lightPrimary,
            onPrimary = if (dark) palette.darkOnPrimary else Color.White,
            primaryContainer = if (dark) palette.darkContainer else palette.lightContainer,
            onPrimaryContainer = if (dark) palette.darkOnContainer else palette.lightOnContainer,
        )
    }
    val styled = scheme.withDesignStyle(settings.designStyle, dark)
    return if (settings.themeMode == ThemeMode.AMOLED) styled.withBlackSurfaces() else styled
}

private fun ColorScheme.withBlackSurfaces() = copy(
    background = Color.Black,
    surface = Color.Black,
    inverseOnSurface = Color.Black,
    surfaceVariant = Color(0xFF171717),
    surfaceContainer = Color.Black,
    surfaceContainerLow = Color.Black,
    surfaceContainerHigh = Color.Black,
    outlineVariant = Color(0xFF343434),
)

private fun Colors.toMaterialScheme(dark: Boolean): ColorScheme =
    (if (dark) darkColorScheme() else lightColorScheme()).copy(
        primary = primary, onPrimary = onPrimary,
        primaryContainer = primaryContainer, onPrimaryContainer = onPrimaryContainer,
        secondary = secondary, onSecondary = onSecondary,
        secondaryContainer = secondaryContainer, onSecondaryContainer = onSecondaryContainer,
        error = error, onError = onError, errorContainer = errorContainer, onErrorContainer = onErrorContainer,
        background = surface, onBackground = onSurface,
        surface = surfaceContainer, onSurface = onSurfaceContainer,
        surfaceVariant = surfaceContainerHigh, onSurfaceVariant = onSurfaceVariantSummary,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        inverseOnSurface = surfaceContainer,
        outline = outline, outlineVariant = dividerLine,
    )

@Composable
fun DSUHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appearance: AppearanceSettings = AppearanceSettings(),
    content: @Composable () -> Unit,
) {
    val dark = appearance.themeMode.isDark(darkTheme)
    val context = LocalContext.current
    val seed = when {
        appearance.keyColor != 0 -> Color(appearance.keyColor)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context).primary else dynamicLightColorScheme(context).primary
        else -> Color(0xFF3482FF)
    }
    val materialColors = remember(appearance, darkTheme, seed) { appearanceColorScheme(appearance, darkTheme, seed) }
    val controller = remember(appearance, dark, seed) {
        ThemeController(
            colorSchemeMode = if (appearance.monetEnabled) {
                if (dark) ColorSchemeMode.MonetDark else ColorSchemeMode.MonetLight
            } else {
                if (dark) ColorSchemeMode.Dark else ColorSchemeMode.Light
            },
            keyColor = if (appearance.monetEnabled) seed else null,
            paletteStyle = ThemePaletteStyle.valueOf(appearance.paletteVariant.preferenceValue),
            colorSpec = if (appearance.effectiveColorSpec() == ColorSpecVersion.SPEC_2025) ThemeColorSpec.Spec2025 else ThemeColorSpec.Spec2021,
            isDark = dark,
        )
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }
    // Keep the provider hierarchy stable so switching frameworks preserves navigation and view models.
    MiuixTheme(controller = controller) {
        val nativeColors = MiuixTheme.colorScheme
        val resolvedNative = if (appearance.themeMode == ThemeMode.AMOLED) {
            nativeColors.copy(background = Color.Black, surface = Color.Black, surfaceContainer = Color.Black)
        } else nativeColors
        MiuixTheme(colors = resolvedNative) {
            val miuixIndication = LocalIndication.current
            val metrics = appearance.designStyle.metrics()
            MaterialTheme(
                colorScheme = if (appearance.designStyle == DesignStyle.MIUIX) resolvedNative.toMaterialScheme(dark) else materialColors,
                typography = appearance.designStyle.typography(),
                shapes = Shapes(
                    small = RoundedCornerShape(metrics.controlRadius.dp),
                    medium = RoundedCornerShape(metrics.controlRadius.dp),
                    large = RoundedCornerShape(metrics.cardRadius.dp),
                    extraLarge = RoundedCornerShape(metrics.cardRadius.dp),
                ),
            ) {
                CompositionLocalProvider(
                    LocalDesignStyle provides appearance.designStyle,
                    LocalScrollAnimation provides appearance.scrollAnimation,
                    LocalIndication provides if (appearance.designStyle == DesignStyle.MIUIX) miuixIndication else LocalIndication.current,
                ) { content() }
            }
        }
    }
}
