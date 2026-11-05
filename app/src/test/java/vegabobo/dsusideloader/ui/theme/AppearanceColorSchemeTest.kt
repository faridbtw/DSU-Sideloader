package vegabobo.dsusideloader.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import vegabobo.dsusideloader.preferences.AccentColor
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.ThemeMode

class AppearanceColorSchemeTest {
    @Test
    fun explicitModesOverrideTheSystemAndSystemModeFollowsIt() {
        assertFalse(ThemeMode.SYSTEM.isDark(false))
        assertTrue(ThemeMode.SYSTEM.isDark(true))
        assertFalse(ThemeMode.LIGHT.isDark(true))
        assertTrue(ThemeMode.DARK.isDark(false))
        assertTrue(ThemeMode.AMOLED.isDark(false))
    }

    @Test
    fun amoledUsesBlackSurfacesForEveryAccent() {
        AccentColor.values().forEach { accent ->
            val scheme = appearanceColorScheme(AppearanceSettings(ThemeMode.AMOLED, accent), false)
            assertEquals(Color.Black, scheme.background)
            assertEquals(Color.Black, scheme.surface)
        }
    }

    @Test
    fun everyPresetKeepsTextReadableAcrossAllModes() {
        ThemeMode.values().forEach { mode ->
            AccentColor.values().forEach { accent ->
                listOf(false, true).forEach { systemDark ->
                    val scheme = appearanceColorScheme(AppearanceSettings(mode, accent), systemDark)
                    val pairs = listOf(
                        scheme.primary to scheme.onPrimary,
                        scheme.primaryContainer to scheme.onPrimaryContainer,
                        scheme.secondaryContainer to scheme.onSecondaryContainer,
                        scheme.surface to scheme.onSurface,
                    )
                    pairs.forEach { (background, foreground) ->
                        val bright = maxOf(background.luminance(), foreground.luminance())
                        val dark = minOf(background.luminance(), foreground.luminance())
                        val contrast = (bright + 0.05) / (dark + 0.05)
                        assertTrue("$mode / $accent / systemDark=$systemDark has contrast $contrast", contrast >= 4.5)
                    }
                }
            }
        }
    }
}
