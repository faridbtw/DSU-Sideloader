package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import vegabobo.dsusideloader.launcher.labelResource
import androidx.compose.material.icons.rounded.Brightness1
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import top.yukonga.miuix.kmp.basic.TabRow
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.ColorSpecVersion
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.preferences.PaletteVariant
import vegabobo.dsusideloader.preferences.ThemeMode
import vegabobo.dsusideloader.preferences.effectiveColorSpec
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

internal val themeSeeds = listOf(
    0, 0xFFF44336.toInt(), 0xFFE91E63.toInt(), 0xFF9C27B0.toInt(), 0xFF673AB7.toInt(),
    0xFF3F51B5.toInt(), 0xFF2196F3.toInt(), 0xFF00BCD4.toInt(), 0xFF009688.toInt(),
    0xFF4FAF50.toInt(), 0xFFFFEB3B.toInt(), 0xFFFFC107.toInt(), 0xFFFF9800.toInt(),
    0xFF795548.toInt(), 0xFF607D8F.toInt(), 0xFFFF9CA8.toInt(),
)

@Composable
fun ThemeScreen(navigate: (String) -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.appearance
    val miuix = LocalDesignStyle.current == DesignStyle.MIUIX
    val seedLabels = listOf(
        R.string.key_default, R.string.key_red, R.string.key_pink, R.string.key_purple, R.string.key_deep_purple,
        R.string.accent_indigo, R.string.accent_blue, R.string.key_cyan, R.string.accent_teal,
        R.string.accent_green, R.string.key_yellow, R.string.accent_amber, R.string.key_orange,
        R.string.key_brown, R.string.key_blue_grey, R.string.key_sakura,
    ).map { stringResource(it) }
    // Keep migrated accent colors selectable even when they are outside the new presets.
    val colors = if (settings.keyColor in themeSeeds) themeSeeds else themeSeeds + settings.keyColor
    val labels = if (colors.size == seedLabels.size) seedLabels else {
        seedLabels + ("#" + settings.keyColor.toUInt().toString(16).takeLast(6).uppercase(java.util.Locale.ROOT))
    }
    SettingsPage(title = stringResource(R.string.theme_mode), onBack = { navigate(Destinations.Up) }) {
        ThemePhonePreview()
        if (!miuix) {
            AnimatedVisibility(visible = settings.monetEnabled) {
                ThemeColorStrip(settings, colors, labels, viewModel::setKeyColor)
            }
        }
        ThemeModeTabs(settings.themeMode, miuix, viewModel::setThemeMode)
        SettingsGroup {
            PreferenceItem(
                icon = Icons.Rounded.Wallpaper,
                title = stringResource(R.string.enable_monet),
                showToggle = true, isChecked = settings.monetEnabled,
                onClick = { viewModel.setMonetEnabled(!it) },
            )
            AnimatedVisibility(visible = settings.monetEnabled) {
                Column {
                    if (miuix) {
                        AppearanceChoice(
                            icon = Icons.Rounded.Colorize,
                            title = stringResource(R.string.key_color), items = labels,
                            selected = colors.indexOf(settings.keyColor),
                            onSelect = { viewModel.setKeyColor(colors[it]) },
                        )
                    }
                    AnimatedVisibility(visible = !miuix || settings.keyColor != 0) {
                        Column { PaletteChoices(settings, viewModel) }
                    }
                }
            }
        }
        if (miuix) {
            AnimatedVisibility(visible = settings.themeMode == ThemeMode.DARK || settings.themeMode == ThemeMode.AMOLED) {
                SettingsGroup {
                    PreferenceItem(
                        icon = Icons.Rounded.DarkMode, title = stringResource(R.string.theme_amoled),
                        showToggle = true, isChecked = settings.themeMode == ThemeMode.AMOLED,
                        onClick = { viewModel.setThemeMode(if (it) ThemeMode.DARK else ThemeMode.AMOLED) },
                    )
                }
            }
        }
        SettingsGroup {
            PreferenceItem(
                icon = Icons.Rounded.Apps, title = stringResource(R.string.app_icon),
                description = stringResource(state.appIcon.labelResource), showArrow = true,
                onClick = { navigate(Destinations.AppIcons) },
            )
            PreferenceItem(
                icon = Icons.Rounded.ViewCarousel, title = stringResource(R.string.scroll_animation),
                description = stringResource(R.string.scroll_animation_description),
                showToggle = true, isChecked = settings.scrollAnimation,
                onClick = { viewModel.setScrollAnimation(!it) },
            )
            PreferenceItem(
                icon = Icons.Rounded.RestartAlt, title = stringResource(R.string.reset_appearance),
                onClick = { viewModel.resetAppearance() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeTabs(mode: ThemeMode, miuix: Boolean, onSelect: (ThemeMode) -> Unit) {
    val modes = ThemeMode.entries
    val labels = listOf(R.string.theme_system, R.string.theme_light, R.string.theme_dark, R.string.theme_amoled)
        .map { stringResource(it) }
    if (miuix) {
        TabRow(
            tabs = labels.take(3), selectedTabIndex = modes.indexOf(mode).coerceAtMost(2),
            onTabSelected = { onSelect(modes[it]) },
        )
    } else {
        val icons = listOf(Icons.Rounded.Brightness4, Icons.Rounded.LightMode, Icons.Rounded.DarkMode, Icons.Rounded.Brightness1)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            modes.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = mode == item, onClick = { onSelect(item) },
                    shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                    modifier = Modifier.weight(1f), icon = {},
                    label = { Icon(icons[index], contentDescription = labels[index]) },
                )
            }
        }
    }
}

@Composable
private fun PaletteChoices(settings: AppearanceSettings, viewModel: SettingsViewModel) {
    val palettes = PaletteVariant.entries
    AppearanceChoice(
        icon = Icons.Rounded.Style, title = stringResource(R.string.color_style),
        items = palettes.map { it.preferenceValue }, selected = palettes.indexOf(settings.paletteVariant),
        onSelect = { viewModel.setPaletteVariant(palettes[it]) },
    )
    val specs = ColorSpecVersion.entries
    AppearanceChoice(
        icon = Icons.Rounded.DesignServices, title = stringResource(R.string.color_spec),
        items = listOf("2021", "2025"), selected = specs.indexOf(settings.effectiveColorSpec()),
        enabled = settings.paletteVariant.supportsSpec2025,
        onSelect = { viewModel.setColorSpec(specs[it]) },
    )
}
