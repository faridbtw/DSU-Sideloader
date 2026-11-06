package vegabobo.dsusideloader.ui.screen.settings

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AccentColor
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.preferences.ThemeMode
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.Title
import vegabobo.dsusideloader.ui.theme.palette

@Composable
fun AppearanceSettingsCard(
    settings: AppearanceSettings,
    onDesignStyleChanged: (DesignStyle) -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onAccentChanged: (AccentColor) -> Unit,
    onWallpaperColorsChanged: (Boolean) -> Unit,
    onReset: () -> Unit,
) {
    var showStylePicker by rememberSaveable { mutableStateOf(false) }
    var showThemePicker by rememberSaveable { mutableStateOf(false) }
    val supportsWallpaperColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val useWallpaperColors = supportsWallpaperColors && settings.useWallpaperColors

    Title(title = stringResource(R.string.appearance))
    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
        CardBox(addPadding = false) {
            PreferenceItem(
                title = stringResource(R.string.interface_style),
                description = stringResource(settings.designStyle.labelRes()),
                icon = Icons.Outlined.Palette,
                onClick = { showStylePicker = true },
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            PreferenceItem(
                title = stringResource(R.string.theme_mode),
                description = stringResource(settings.themeMode.labelRes()),
                icon = Icons.Outlined.Contrast,
                onClick = { showThemePicker = true },
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.accent_color), style = MaterialTheme.typography.titleMedium)
                Column(modifier = Modifier.selectableGroup().alpha(if (useWallpaperColors) 0.45f else 1f)) {
                    AccentColor.values().toList().chunked(3).forEach { colors ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            colors.forEach { accent ->
                                val selected = settings.accentColor == accent && !useWallpaperColors
                                Column(
                                    modifier = Modifier.weight(1f)
                                        .selectable(
                                            selected = selected,
                                            enabled = !useWallpaperColors,
                                            role = Role.RadioButton,
                                            onClick = { onAccentChanged(accent) },
                                        )
                                        .padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        color = accent.palette().lightPrimary,
                                        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null,
                                    ) {
                                        if (selected) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(9.dp),
                                            )
                                        }
                                    }
                                    Text(stringResource(accent.labelRes()), style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
            if (supportsWallpaperColors) {
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                PreferenceItem(
                    title = stringResource(R.string.wallpaper_colors),
                    description = stringResource(R.string.wallpaper_colors_description),
                    showToggle = true,
                    isChecked = settings.useWallpaperColors,
                    onClick = { onWallpaperColorsChanged(!it) },
                )
            }
            TextButton(onClick = onReset, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(stringResource(R.string.reset_appearance))
            }
        }
    }

    if (showStylePicker) {
        AlertDialog(
            onDismissRequest = { showStylePicker = false },
            title = { Text(stringResource(R.string.interface_style)) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().selectableGroup()
                        .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.55f).dp),
                ) {
                    items(DesignStyle.values().toList(), key = { it.preferenceValue }) { style ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .selectable(
                                    selected = settings.designStyle == style,
                                    role = Role.RadioButton,
                                    onClick = {
                                        onDesignStyleChanged(style)
                                        showStylePicker = false
                                    },
                                ).padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            RadioButton(selected = settings.designStyle == style, onClick = null)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(style.labelRes()), style = MaterialTheme.typography.titleSmall)
                                Text(
                                    stringResource(style.descriptionRes()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStylePicker = false }) { Text(stringResource(R.string.close)) }
            },
        )
    }

    if (showThemePicker) {
        AlertDialog(
            onDismissRequest = { showThemePicker = false },
            title = { Text(stringResource(R.string.theme_mode)) },
            text = {
                Column(modifier = Modifier.selectableGroup()) {
                    ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .selectable(
                                    selected = settings.themeMode == mode,
                                    role = Role.RadioButton,
                                    onClick = {
                                        onThemeModeChanged(mode)
                                        showThemePicker = false
                                    },
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            RadioButton(selected = settings.themeMode == mode, onClick = null)
                            Text(stringResource(mode.labelRes()), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemePicker = false }) { Text(stringResource(R.string.close)) }
            },
        )
    }
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.theme_system
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
    ThemeMode.AMOLED -> R.string.theme_amoled
}

@StringRes
private fun AccentColor.labelRes(): Int = when (this) {
    AccentColor.INDIGO -> R.string.accent_indigo
    AccentColor.BLUE -> R.string.accent_blue
    AccentColor.TEAL -> R.string.accent_teal
    AccentColor.GREEN -> R.string.accent_green
    AccentColor.ROSE -> R.string.accent_rose
    AccentColor.AMBER -> R.string.accent_amber
}

@StringRes
private fun DesignStyle.labelRes(): Int = when (this) {
    DesignStyle.MATERIAL -> R.string.style_material
    DesignStyle.MIUIX -> R.string.style_miuix
    DesignStyle.CUPERTINO -> R.string.style_cupertino
    DesignStyle.MINIMAL -> R.string.style_minimal
}

@StringRes
private fun DesignStyle.descriptionRes(): Int = when (this) {
    DesignStyle.MATERIAL -> R.string.style_material_description
    DesignStyle.MIUIX -> R.string.style_miuix_description
    DesignStyle.CUPERTINO -> R.string.style_cupertino_description
    DesignStyle.MINIMAL -> R.string.style_minimal_description
}
