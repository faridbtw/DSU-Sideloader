package vegabobo.dsusideloader.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

enum class LauncherIcon(val preferenceValue: String, val componentSuffix: String) {
    STOCK("stock", "LauncherStock"),
    MATERIAL("material", "LauncherMaterial"),
    MINT("mint", "LauncherMint"),
    OCEAN("ocean", "LauncherOcean"),
    SUNSET("sunset", "LauncherSunset"),
    ROSE("rose", "LauncherRose"),
    GRAPHITE("graphite", "LauncherGraphite"),
    ORBIT("orbit", "LauncherOrbit"),
    CIRCUIT("circuit", "LauncherCircuit"),
    PRISM("prism", "LauncherPrism"),
    PIXEL("pixel", "LauncherPixel"),
    TERMINAL("terminal", "LauncherTerminal"),
    SOLAR("solar", "LauncherSolar");

    fun componentClass(packageName: String) = "$packageName.$componentSuffix"
}

@Singleton
class LauncherIconPreferences @Inject constructor(private val store: DataStore<Preferences>) {
    private val key = stringPreferencesKey("launcher_icon")
    val selectedIcon = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map { preferences ->
        LauncherIcon.entries.firstOrNull { it.preferenceValue == preferences[key] } ?: LauncherIcon.MATERIAL
    }.distinctUntilChanged()

    suspend fun setIcon(icon: LauncherIcon) {
        store.edit { it[key] = icon.preferenceValue }
    }
}
