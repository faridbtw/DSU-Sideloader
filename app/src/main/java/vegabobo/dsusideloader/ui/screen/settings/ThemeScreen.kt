package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.ColorSpecVersion
import vegabobo.dsusideloader.preferences.PaletteVariant
import vegabobo.dsusideloader.preferences.ThemeMode
import vegabobo.dsusideloader.preferences.effectiveColorSpec
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

private val seedValues = listOf(
    0, 0xFFF44336.toInt(), 0xFFE91E63.toInt(), 0xFF9C27B0.toInt(), 0xFF673AB7.toInt(),
    0xFF3F51B5.toInt(), 0xFF2196F3.toInt(), 0xFF00BCD4.toInt(), 0xFF009688.toInt(),
    0xFF4FAF50.toInt(), 0xFFFFEB3B.toInt(), 0xFFFFC107.toInt(), 0xFFFF9800.toInt(),
    0xFF795548.toInt(), 0xFF607D8F.toInt(), 0xFFFF9CA8.toInt(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(navigate: (String) -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.appearance
    val seedLabels = listOf(
        R.string.key_default, R.string.key_red, R.string.key_pink, R.string.key_purple, R.string.key_deep_purple,
        R.string.accent_indigo, R.string.accent_blue, R.string.key_cyan, R.string.accent_teal,
        R.string.accent_green, R.string.key_yellow, R.string.accent_amber, R.string.key_orange,
        R.string.key_brown, R.string.key_blue_grey, R.string.key_sakura,
    )
    SettingsPage(title = stringResource(R.string.theme_mode), onBack = { navigate(Destinations.Up) }) {
        CardBox(addPadding = false) {
            val modes = ThemeMode.entries
            val labels = listOf(R.string.theme_system, R.string.theme_light, R.string.theme_dark, R.string.theme_amoled).map { stringResource(it) }
            AppearanceChoice(stringResource(R.string.theme_mode), labels, modes.indexOf(settings.themeMode), onSelect = { viewModel.setThemeMode(modes[it]) })
            PreferenceItem(
                title = stringResource(R.string.enable_monet), description = stringResource(R.string.monet_description),
                showToggle = true, isChecked = settings.monetEnabled, onClick = { viewModel.setMonetEnabled(!it) },
            )
        }
        CardBox(cardTitle = stringResource(R.string.key_color)) {
            Column(modifier = Modifier.selectableGroup().alpha(if (settings.monetEnabled) 1f else 0.45f)) {
                seedValues.indices.toList().chunked(4).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { index ->
                            val seed = seedValues[index]
                            val selected = settings.keyColor == seed
                            Column(
                                modifier = Modifier.weight(1f).selectable(
                                    selected = selected, enabled = settings.monetEnabled, role = Role.RadioButton,
                                    onClick = { viewModel.setKeyColor(seed) },
                                ).padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp), shape = CircleShape,
                                    color = if (seed == 0) MaterialTheme.colorScheme.primaryContainer else Color(seed),
                                    border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null,
                                ) {
                                    if (seed == 0) Icon(Icons.Outlined.Wallpaper, null, modifier = Modifier.padding(6.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    else if (selected) Icon(Icons.Outlined.Check, null, modifier = Modifier.padding(6.dp), tint = if (Color(seed).luminance() > 0.179f) Color.Black else Color.White)
                                }
                                Text(stringResource(seedLabels[index]), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
        CardBox(addPadding = false) {
            val palettes = PaletteVariant.entries
            AppearanceChoice(
                stringResource(R.string.color_style), palettes.map { it.preferenceValue }, palettes.indexOf(settings.paletteVariant),
                enabled = settings.monetEnabled, onSelect = { viewModel.setPaletteVariant(palettes[it]) },
            )
            val specs = ColorSpecVersion.entries
            AppearanceChoice(
                stringResource(R.string.color_spec), listOf("2021", "2025"), specs.indexOf(settings.effectiveColorSpec()),
                enabled = settings.monetEnabled && settings.paletteVariant.supportsSpec2025,
                onSelect = { viewModel.setColorSpec(specs[it]) },
            )
            if (!settings.paletteVariant.supportsSpec2025) {
                Text(stringResource(R.string.color_spec_compatibility), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        TextButton(onClick = viewModel::resetAppearance) { Text(stringResource(R.string.reset_appearance)) }
    }
}
