package vegabobo.dsusideloader.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// Stable values keep saved choices independent of enum ordering.
enum class ThemeMode(val preferenceValue: String) {
    SYSTEM("system"), LIGHT("light"), DARK("dark"), AMOLED("amoled");

    fun isDark(systemDark: Boolean): Boolean = when (this) {
        SYSTEM -> systemDark
        LIGHT -> false
        DARK, AMOLED -> true
    }
}

enum class AccentColor(val preferenceValue: String) {
    INDIGO("indigo"), BLUE("blue"), TEAL("teal"), GREEN("green"), ROSE("rose"), AMBER("amber")
}

enum class DesignStyle(val preferenceValue: String) {
    MATERIAL("material"), MIUIX("miuix")
}

enum class PaletteVariant(val preferenceValue: String) {
    TONAL_SPOT("TonalSpot"), NEUTRAL("Neutral"), VIBRANT("Vibrant"), EXPRESSIVE("Expressive"),
    RAINBOW("Rainbow"), FRUIT_SALAD("FruitSalad"), MONOCHROME("Monochrome"), FIDELITY("Fidelity"), CONTENT("Content");

    val supportsSpec2025: Boolean get() = this in setOf(TONAL_SPOT, NEUTRAL, VIBRANT, EXPRESSIVE)
}

enum class ColorSpecVersion { SPEC_2021, SPEC_2025 }

data class AppearanceSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.INDIGO,
    val useWallpaperColors: Boolean = false,
    val designStyle: DesignStyle = DesignStyle.MATERIAL,
    val monetEnabled: Boolean = false,
    val keyColor: Int = 0,
    val paletteVariant: PaletteVariant = PaletteVariant.TONAL_SPOT,
    val colorSpec: ColorSpecVersion = ColorSpecVersion.SPEC_2025,
    val scrollAnimation: Boolean = true,
)

@Singleton
class AppearancePreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
    private val scrollAnimationKey = booleanPreferencesKey("appearance_scroll_animation")
    private val monetKey = booleanPreferencesKey("appearance_monet")
    private val keyColorKey = intPreferencesKey("appearance_key_color")
    private val paletteKey = stringPreferencesKey("appearance_palette")
    private val specKey = stringPreferencesKey("appearance_color_spec")
    private val styleKey = stringPreferencesKey("appearance_style")
    private val themeKey = stringPreferencesKey("appearance_theme")
    private val accentKey = stringPreferencesKey("appearance_accent")
    private val wallpaperKey = booleanPreferencesKey("appearance_wallpaper_colors")

    val settings = dataStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map { preferences ->
        AppearanceSettings(
            themeMode = ThemeMode.values().firstOrNull { it.preferenceValue == preferences[themeKey] } ?: ThemeMode.SYSTEM,
            accentColor = AccentColor.values().firstOrNull { it.preferenceValue == preferences[accentKey] } ?: AccentColor.INDIGO,
            useWallpaperColors = preferences[wallpaperKey] ?: false,
            designStyle = DesignStyle.values().firstOrNull { it.preferenceValue == preferences[styleKey] } ?: DesignStyle.MATERIAL,
            monetEnabled = preferences[monetKey] ?: false,
            scrollAnimation = preferences[scrollAnimationKey] ?: true,
            keyColor = preferences[keyColorKey] ?: legacyKeyColor(preferences[accentKey], preferences[wallpaperKey] == true),
            paletteVariant = PaletteVariant.values().firstOrNull { it.preferenceValue == preferences[paletteKey] } ?: PaletteVariant.TONAL_SPOT,
            colorSpec = ColorSpecVersion.values().firstOrNull { it.name == preferences[specKey] } ?: ColorSpecVersion.SPEC_2025,
        )
    }.distinctUntilChanged()

    suspend fun setScrollAnimation(enabled: Boolean) {
        dataStore.edit { it[scrollAnimationKey] = enabled }
    }

    suspend fun setMonetEnabled(enabled: Boolean) {
        dataStore.edit { it[monetKey] = enabled }
    }

    suspend fun setKeyColor(color: Int) {
        require(color == 0 || color ushr 24 == 255)
        dataStore.edit {
            it[keyColorKey] = color
            it[wallpaperKey] = color == 0
        }
    }

    suspend fun setPaletteVariant(palette: PaletteVariant) {
        dataStore.edit { it[paletteKey] = palette.preferenceValue }
    }

    suspend fun setColorSpec(spec: ColorSpecVersion) {
        dataStore.edit { it[specKey] = spec.name }
    }

    suspend fun setDesignStyle(style: DesignStyle) {
        dataStore.edit { it[styleKey] = style.preferenceValue }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[themeKey] = mode.preferenceValue }
    }

    suspend fun setAccentColor(accent: AccentColor) {
        dataStore.edit {
            it[accentKey] = accent.preferenceValue
            it[wallpaperKey] = false
            it[keyColorKey] = legacyKeyColor(accent.preferenceValue, false)
        }
    }

    suspend fun setWallpaperColors(enabled: Boolean) {
        dataStore.edit {
            it[wallpaperKey] = enabled
            it[keyColorKey] = if (enabled) 0 else legacyKeyColor(it[accentKey], false)
        }
    }

    suspend fun reset() {
        dataStore.edit {
            it.remove(scrollAnimationKey)
            it.remove(monetKey)
            it.remove(keyColorKey)
            it.remove(paletteKey)
            it.remove(specKey)
            it.remove(styleKey)
            it.remove(themeKey)
            it.remove(accentKey)
            it.remove(wallpaperKey)
        }
    }
}

fun AppearanceSettings.effectiveColorSpec(): ColorSpecVersion =
    if (colorSpec == ColorSpecVersion.SPEC_2025 && !paletteVariant.supportsSpec2025) ColorSpecVersion.SPEC_2021 else colorSpec

private fun legacyKeyColor(accent: String?, wallpaper: Boolean): Int {
    if (wallpaper || accent == null) return 0
    return when (accent) {
        "indigo" -> 0xFF5754D6.toInt()
        "blue" -> 0xFF245DB5.toInt()
        "teal" -> 0xFF006B60.toInt()
        "green" -> 0xFF376B30.toInt()
        "rose" -> 0xFFA23662.toInt()
        "amber" -> 0xFF825500.toInt()
        else -> 0
    }
}
