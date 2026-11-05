package vegabobo.dsusideloader.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AppearancePreferencesTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun savedChoicesSurviveReopeningTheStore() = runBlocking {
        val file = File(temporaryFolder.root, "appearance.preferences_pb")
        val firstScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val firstStore = PreferenceDataStoreFactory.create(scope = firstScope, produceFile = { file })
        val first = AppearancePreferences(firstStore)
        try {
            first.setThemeMode(ThemeMode.AMOLED)
            first.setAccentColor(AccentColor.TEAL)
            first.setWallpaperColors(true)
        } finally {
            firstScope.coroutineContext[Job]!!.cancelAndJoin()
        }

        val secondScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val secondStore = PreferenceDataStoreFactory.create(scope = secondScope, produceFile = { file })
        try {
            assertEquals(
                AppearanceSettings(ThemeMode.AMOLED, AccentColor.TEAL, true),
                AppearancePreferences(secondStore).settings.first(),
            )
        } finally {
            secondScope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun missingOrUnknownChoicesUseDefaults() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(temporaryFolder.root, "appearance.preferences_pb") },
        )
        try {
            val appearance = AppearancePreferences(store)
            assertEquals(AppearanceSettings(), appearance.settings.first())
            store.edit {
                it[stringPreferencesKey("appearance_theme")] = "future-theme"
                it[stringPreferencesKey("appearance_accent")] = "future-accent"
            }
            assertEquals(AppearanceSettings(), appearance.settings.first())
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun resetPreservesInstallationPreferences() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(temporaryFolder.root, "appearance.preferences_pb") },
        )
        try {
            val appearance = AppearancePreferences(store)
            val keepScreenOn = booleanPreferencesKey(AppPrefs.KEEP_SCREEN_ON)
            store.edit { it[keepScreenOn] = true }
            appearance.setThemeMode(ThemeMode.DARK)
            appearance.setAccentColor(AccentColor.ROSE)
            appearance.setWallpaperColors(true)
            appearance.reset()
            assertEquals(AppearanceSettings(), appearance.settings.first())
            assertTrue(store.data.first()[keepScreenOn] == true)
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun choosingAPresetStopsUsingWallpaperColors() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(temporaryFolder.root, "appearance.preferences_pb") },
        )
        try {
            val appearance = AppearancePreferences(store)
            appearance.setWallpaperColors(true)
            appearance.setAccentColor(AccentColor.BLUE)
            val saved = appearance.settings.first()
            assertEquals(AccentColor.BLUE, saved.accentColor)
            assertFalse(saved.useWallpaperColors)
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }
}
