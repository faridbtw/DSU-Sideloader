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
            first.setDesignStyle(DesignStyle.MIUIX)
            first.setThemeMode(ThemeMode.AMOLED)
            first.setAccentColor(AccentColor.TEAL)
            first.setKeyColor(0xFFE91E63.toInt())
            first.setMonetEnabled(false)
            first.setPaletteVariant(PaletteVariant.FIDELITY)
            first.setColorSpec(ColorSpecVersion.SPEC_2021)
        } finally {
            firstScope.coroutineContext[Job]!!.cancelAndJoin()
        }

        val secondScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val secondStore = PreferenceDataStoreFactory.create(scope = secondScope, produceFile = { file })
        try {
            assertEquals(
                AppearanceSettings(ThemeMode.AMOLED, AccentColor.TEAL, false, DesignStyle.MIUIX, false, 0xFFE91E63.toInt(), PaletteVariant.FIDELITY, ColorSpecVersion.SPEC_2021),
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
                it[stringPreferencesKey("appearance_style")] = "future-style"
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
            appearance.setDesignStyle(DesignStyle.CUPERTINO)
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

    @Test
    fun changingStylePreservesColorModeAndAccentSettings() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(temporaryFolder.root, "appearance.preferences_pb") },
        )
        try {
            val appearance = AppearancePreferences(store)
            appearance.setThemeMode(ThemeMode.DARK)
            appearance.setAccentColor(AccentColor.ROSE)
            appearance.setWallpaperColors(true)
            appearance.setDesignStyle(DesignStyle.MINIMAL)
            assertEquals(
                AppearanceSettings(ThemeMode.DARK, AccentColor.ROSE, true, DesignStyle.MINIMAL),
                appearance.settings.first(),
            )
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }
    @Test
    fun olderAccentPreferencesMigrateWithoutLosingTheChosenColor() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(temporaryFolder.root, "legacy.preferences_pb") },
        )
        try {
            store.edit { it[stringPreferencesKey("appearance_accent")] = "teal" }
            val appearance = AppearancePreferences(store)
            assertEquals(0xFF006B60.toInt(), appearance.settings.first().keyColor)
            store.edit { it[booleanPreferencesKey("appearance_wallpaper_colors")] = true }
            assertEquals(0, appearance.settings.first().keyColor)
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun unsupportedPalettesFallBackWithoutOverwritingTheSavedSpec() {
        PaletteVariant.entries.forEach { palette ->
            val settings = AppearanceSettings(paletteVariant = palette, colorSpec = ColorSpecVersion.SPEC_2025)
            assertEquals(
                if (palette.supportsSpec2025) ColorSpecVersion.SPEC_2025 else ColorSpecVersion.SPEC_2021,
                settings.effectiveColorSpec(),
            )
            assertEquals(ColorSpecVersion.SPEC_2025, settings.colorSpec)
        }
    }

}
