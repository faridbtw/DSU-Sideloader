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
import vegabobo.dsusideloader.util.OperationMode

// An unavailable explicit mode falls back to ADB instead of silently using another privilege source.
enum class OperationModePreference(val preferenceValue: String, val mode: OperationMode?) {
    AUTOMATIC("auto", null),
    ADB("adb", OperationMode.ADB),
    ROOT("root", OperationMode.ROOT),
    SHIZUKU("shizuku", OperationMode.SHIZUKU),
    SYSTEM("system", OperationMode.SYSTEM),
    SYSTEM_AND_ROOT("system_root", OperationMode.SYSTEM_AND_ROOT);

    val requiresRootProbe: Boolean
        get() = this == AUTOMATIC || this == ROOT || this == SYSTEM_AND_ROOT

    fun resolve(available: Set<OperationMode>): OperationMode =
        if (mode == null) OperationMode.entries.firstOrNull { it in available } ?: OperationMode.ADB
        else mode.takeIf { it in available } ?: OperationMode.ADB
}

@Singleton
class OperationPreferences @Inject constructor(private val store: DataStore<Preferences>) {
    private val key = stringPreferencesKey("preferred_operation_mode")
    val preferredMode = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map { preferences ->
        OperationModePreference.entries.firstOrNull { it.preferenceValue == preferences[key] }
            ?: OperationModePreference.AUTOMATIC
    }.distinctUntilChanged()

    suspend fun setPreferredMode(mode: OperationModePreference) {
        store.edit { it[key] = mode.preferenceValue }
    }
}
