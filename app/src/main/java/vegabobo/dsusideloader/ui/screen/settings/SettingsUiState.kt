package vegabobo.dsusideloader.ui.screen.settings

import vegabobo.dsusideloader.preferences.OperationModePreference
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.preferences.LauncherIcon
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.preferences.AppearanceSettings

enum class DialogSheetState {
    NONE,
    BUILT_IN_INSTALLER,
    DISABLE_STORAGE_CHECK,
}

data class SettingsUiState(
    val appearance: AppearanceSettings = AppearanceSettings(),
    val preferences: HashMap<String, Boolean> = hashMapOf(
        AppPrefs.USE_BUILTIN_INSTALLER to AppPrefs.DEFAULT_USE_BUILTIN_INSTALLER,
        AppPrefs.KEEP_SCREEN_ON to false,
        AppPrefs.UMOUNT_SD to false,
        AppPrefs.DISABLE_STORAGE_CHECK to false,
        AppPrefs.FULL_LOGCAT_LOGGING to false,
    ),
    val dialogSheetDisplay: DialogSheetState = DialogSheetState.NONE,
    val appIcon: LauncherIcon = LauncherIcon.MATERIAL,
    val changingIcon: Boolean = false,
    val iconChangeFailed: Boolean = false,
    val preferredMode: OperationModePreference = OperationModePreference.AUTOMATIC,
    val activeMode: OperationMode = OperationMode.ADB,
    val isInstalling: Boolean = false,
    val changingMode: Boolean = false,
    val isRoot: Boolean = false,
    val isDevOptEnabled: Boolean = false,
)
