package vegabobo.dsusideloader.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import vegabobo.dsusideloader.preferences.AccentColor
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.preferences.PaletteVariant
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
        DesignStyle.values().forEach { style ->
            AccentColor.values().forEach { accent ->
                val scheme = appearanceColorScheme(AppearanceSettings(ThemeMode.AMOLED, accent, designStyle = style), false)
                assertEquals(Color.Black, scheme.background)
                assertEquals(Color.Black, scheme.surface)
            }
        }
    }

    @Test
    fun everyPresetKeepsTextReadableAcrossAllModes() {
        DesignStyle.values().forEach { style ->
            ThemeMode.values().forEach { mode ->
                AccentColor.values().forEach { accent ->
                    listOf(false, true).forEach { systemDark ->
                        val scheme = appearanceColorScheme(AppearanceSettings(mode, accent, designStyle = style), systemDark)
                        val pairs = listOf(
                            scheme.primary to scheme.onPrimary,
                            scheme.primaryContainer to scheme.onPrimaryContainer,
                            scheme.secondaryContainer to scheme.onSecondaryContainer,
                            scheme.surface to scheme.onSurface,
                            scheme.background to scheme.onBackground,
                            scheme.surfaceVariant to scheme.onSurfaceVariant,
                        )
                        pairs.forEach { (background, foreground) ->
                            val bright = maxOf(background.luminance(), foreground.luminance())
                            val dark = minOf(background.luminance(), foreground.luminance())
                            val contrast = (bright + 0.05) / (dark + 0.05)
                            assertTrue("$style / $mode / $accent / systemDark=$systemDark has contrast $contrast", contrast >= 4.5)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun visualStylesHaveDistinctSurfacesWithoutChangingTheChosenAccent() {
        val schemes = DesignStyle.values().map { style ->
            appearanceColorScheme(AppearanceSettings(ThemeMode.LIGHT, AccentColor.TEAL, designStyle = style), false)
        }
        assertEquals(4, schemes.map { it.background }.distinct().size)
        assertEquals(1, schemes.map { it.primary }.distinct().size)
    }
    @Test
    fun monetUsesTheSelectedSeedAndPaletteAndCanBeTurnedOff() {
        val red = AppearanceSettings(themeMode = ThemeMode.LIGHT, keyColor = 0xFFF44336.toInt())
        val blue = red.copy(keyColor = 0xFF2196F3.toInt())
        assertNotEquals(appearanceColorScheme(red, false).primary, appearanceColorScheme(blue, false).primary)
        val generated = PaletteVariant.entries.map {
            appearanceColorScheme(red.copy(paletteVariant = it), false).primary
        }
        assertTrue(generated.distinct().size > 4)
        assertEquals(
            appearanceColorScheme(red.copy(monetEnabled = false), false).primary,
            appearanceColorScheme(blue.copy(monetEnabled = false), false).primary,
        )
    }

}
