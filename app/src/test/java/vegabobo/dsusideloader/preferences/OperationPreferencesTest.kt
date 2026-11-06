package vegabobo.dsusideloader.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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
import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.preparation.isActiveInstallationStep
import vegabobo.dsusideloader.util.DataStoreUtils
import vegabobo.dsusideloader.util.OperationMode

class OperationPreferencesTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun automaticKeepsCapabilityPriority() {
        assertEquals(OperationMode.SYSTEM_AND_ROOT, OperationModePreference.AUTOMATIC.resolve(OperationMode.entries.toSet()))
        assertEquals(OperationMode.SYSTEM, OperationModePreference.AUTOMATIC.resolve(setOf(OperationMode.SYSTEM, OperationMode.ROOT, OperationMode.ADB)))
        assertEquals(OperationMode.ROOT, OperationModePreference.AUTOMATIC.resolve(setOf(OperationMode.ROOT, OperationMode.SHIZUKU, OperationMode.ADB)))
        assertEquals(OperationMode.SHIZUKU, OperationModePreference.AUTOMATIC.resolve(setOf(OperationMode.SHIZUKU, OperationMode.ADB)))
        assertEquals(OperationMode.ADB, OperationModePreference.AUTOMATIC.resolve(emptySet()))
    }

    @Test
    fun explicitModesNeverSilentlyUseAnotherPrivilegeSource() {
        OperationModePreference.entries.filter { it.mode != null }.forEach { preference ->
            assertEquals(preference.mode, preference.resolve(OperationMode.entries.toSet()))
            assertEquals(OperationMode.ADB, preference.resolve(OperationMode.entries.toSet() - preference.mode!!))
        }
        assertEquals(OperationMode.ADB, OperationModePreference.ADB.resolve(OperationMode.entries.toSet()))
        assertFalse(OperationModePreference.ADB.requiresRootProbe)
        assertFalse(OperationModePreference.SHIZUKU.requiresRootProbe)
        assertFalse(OperationModePreference.SYSTEM.requiresRootProbe)
        assertTrue(OperationModePreference.ROOT.requiresRootProbe)
    }

    @Test
    fun freshStoreUsesAutomaticWithBuiltinInstallerOff() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { File(temporaryFolder.root, "fresh.preferences_pb") })
        try {
            assertEquals(OperationModePreference.AUTOMATIC, OperationPreferences(store).preferredMode.first())
            assertFalse(DataStoreUtils.readBoolPref(store, AppPrefs.USE_BUILTIN_INSTALLER, AppPrefs.DEFAULT_USE_BUILTIN_INSTALLER))
            DataStoreUtils.updateBoolPref(store, AppPrefs.USE_BUILTIN_INSTALLER, true)
            assertTrue(DataStoreUtils.readBoolPref(store, AppPrefs.USE_BUILTIN_INSTALLER, AppPrefs.DEFAULT_USE_BUILTIN_INSTALLER))
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun selectionSurvivesReopening() = runBlocking {
        val file = File(temporaryFolder.root, "saved.preferences_pb")
        val firstScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val firstStore = PreferenceDataStoreFactory.create(scope = firstScope, produceFile = { file })
        try {
            OperationPreferences(firstStore).setPreferredMode(OperationModePreference.SHIZUKU)
        } finally {
            firstScope.coroutineContext[Job]!!.cancelAndJoin()
        }
        val secondScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val secondStore = PreferenceDataStoreFactory.create(scope = secondScope, produceFile = { file })
        try {
            assertEquals(OperationModePreference.SHIZUKU, OperationPreferences(secondStore).preferredMode.first())
        } finally {
            secondScope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun unknownStoredModeUsesAutomatic() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { File(temporaryFolder.root, "unknown.preferences_pb") })
        try {
            store.edit { it[stringPreferencesKey("preferred_operation_mode")] = "future-mode" }
            assertEquals(OperationModePreference.AUTOMATIC, OperationPreferences(store).preferredMode.first())
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
        }
    }

    @Test
    fun installationGuardCoversPreparationAndInstallationButReleasesTerminalStates() {
        listOf(InstallationStep.PROCESSING, InstallationStep.EXTRACTING_FILE, InstallationStep.WAITING_USER_CONFIRMATION,
            InstallationStep.INSTALLING, InstallationStep.CREATING_PARTITION).forEach { assertTrue(it.isActiveInstallationStep()) }
        listOf(InstallationStep.NOT_INSTALLING, InstallationStep.ERROR, InstallationStep.ERROR_CANCELED,
            InstallationStep.INSTALL_SUCCESS, InstallationStep.REQUIRES_ADB_CMD_TO_CONTINUE,
            InstallationStep.DSU_ALREADY_INSTALLED).forEach { assertFalse(it.isActiveInstallationStep()) }
    }
}
