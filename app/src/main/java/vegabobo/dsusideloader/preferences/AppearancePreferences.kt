package vegabobo.dsusideloader.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
    MATERIAL("material"), MIUIX("miuix"), CUPERTINO("cupertino"), MINIMAL("minimal")
}

data class AppearanceSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.INDIGO,
    val useWallpaperColors: Boolean = false,
    val designStyle: DesignStyle = DesignStyle.MATERIAL,
)

@Singleton
class AppearancePreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
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
        )
    }.distinctUntilChanged()

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
        }
    }

    suspend fun setWallpaperColors(enabled: Boolean) {
        dataStore.edit { it[wallpaperKey] = enabled }
    }

    suspend fun reset() {
        dataStore.edit {
            it.remove(styleKey)
            it.remove(themeKey)
            it.remove(accentKey)
            it.remove(wallpaperKey)
        }
    }
}
