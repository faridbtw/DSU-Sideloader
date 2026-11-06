package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.ui.components.DialogLikeBottomSheet
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun Settings(
    navigate: (String) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.checkDevOpt()
    }

    SettingsPage(
        title = stringResource(R.string.settings),
    ) {
        AppearanceSettingsCard(
            settings = uiState.appearance,
            onDesignStyleChanged = settingsViewModel::setDesignStyle,
            onOpenTheme = { navigate(Destinations.Theme) },
        )
        SettingsGroup {
            PreferenceItem(
                icon = Icons.Rounded.SystemUpdate,
                title = stringResource(id = R.string.builtin_installer),
                description =
                if (settingsViewModel.isAndroidQ()) {
                    stringResource(id = R.string.unsupported)
                } else if (uiState.isRoot) {
                    stringResource(id = R.string.builtin_installer_description)
                } else {
                    stringResource(R.string.requires_root)
                },
                showToggle = true,
                isEnabled = uiState.isRoot && !settingsViewModel.isAndroidQ(),
                isChecked = uiState.preferences[AppPrefs.USE_BUILTIN_INSTALLER]!!,
                onClick = {
                    if (!it) {
                        settingsViewModel.updateSheetDisplay(DialogSheetState.BUILT_IN_INSTALLER)
                    }
                    settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, !it)
                },
            )
            PreferenceItem(
                icon = Icons.Rounded.SdStorage,
                title = stringResource(id = R.string.unmount_sd_title),
                description = stringResource(id = R.string.unmount_sd_description),
                showToggle = true,
                isChecked = uiState.preferences[AppPrefs.UMOUNT_SD]!!,
                onClick = { settingsViewModel.togglePreference(AppPrefs.UMOUNT_SD, !it) },
            )
            PreferenceItem(
                icon = Icons.Rounded.ScreenLockPortrait,
                title = stringResource(id = R.string.keep_screen_on),
                showToggle = true,
                isChecked = uiState.preferences[AppPrefs.KEEP_SCREEN_ON]!!,
                onClick = { settingsViewModel.togglePreference(AppPrefs.KEEP_SCREEN_ON, !it) },
            )
        }

        if (uiState.isDevOptEnabled) {
            SettingsGroup {
                PreferenceItem(
                    icon = Icons.Rounded.Storage,
                    title = stringResource(R.string.storage_check_title),
                    description = stringResource(R.string.storage_check_description),
                    showToggle = true,
                    isChecked = uiState.preferences[AppPrefs.DISABLE_STORAGE_CHECK]!!,
                    onClick = {
                        if (!it) {
                            settingsViewModel.updateSheetDisplay(DialogSheetState.DISABLE_STORAGE_CHECK)
                        }
                        settingsViewModel.togglePreference(AppPrefs.DISABLE_STORAGE_CHECK, !it)
                    },
                )
                if (settingsViewModel.getOperationMode() != OperationMode.ADB) {
                    PreferenceItem(
                        icon = Icons.Rounded.BugReport,
                        title = stringResource(R.string.full_logcat_logging_title),
                        description = stringResource(R.string.full_logcat_logging_description),
                        showToggle = true,
                        isChecked = uiState.preferences[AppPrefs.FULL_LOGCAT_LOGGING]!!,
                        onClick = { settingsViewModel.togglePreference(AppPrefs.FULL_LOGCAT_LOGGING, !it) },
                    )
                }
            }
        }

        SettingsGroup {
            PreferenceItem(
                icon = Icons.Rounded.Security,
                title = stringResource(R.string.operation_mode),
                description = settingsViewModel.checkOperationMode(),
            )
            PreferenceItem(
                icon = Icons.Rounded.Info,
                title = stringResource(R.string.about),
                description = stringResource(R.string.about_description),
                showArrow = true,
                onClick = { navigate(Destinations.About) },
            )
        }
    }

    when (uiState.dialogSheetDisplay) {
        DialogSheetState.BUILT_IN_INSTALLER ->
            DialogLikeBottomSheet(
                title = stringResource(id = R.string.experimental_feature),
                icon = Icons.Outlined.NewReleases,
                text = stringResource(id = R.string.experimental_feature_description),
                confirmText = stringResource(id = R.string.yes),
                cancelText = stringResource(id = R.string.cancel),
                onClickCancel = {
                    settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, false)
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
                onClickConfirm = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
            )

        DialogSheetState.DISABLE_STORAGE_CHECK ->
            DialogLikeBottomSheet(
                title = stringResource(id = R.string.warning_storage_check_title),
                icon = Icons.Outlined.WarningAmber,
                text = stringResource(id = R.string.warning_storage_check_description),
                confirmText = stringResource(id = R.string.continue_anyway),
                cancelText = stringResource(id = R.string.cancel),
                onClickCancel = {
                    settingsViewModel.togglePreference(AppPrefs.DISABLE_STORAGE_CHECK, false)
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
                onClickConfirm = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
            )

        else -> {}
    }
}
