package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.components.PreferenceItem

@Composable
fun AppearanceSettingsCard(settings: AppearanceSettings, onDesignStyleChanged: (DesignStyle) -> Unit, onOpenTheme: () -> Unit) {
    SettingsGroup {
        val styles = DesignStyle.entries
        val labels = styles.map {
            stringResource(when (it) {
                DesignStyle.MATERIAL -> R.string.style_material
                DesignStyle.MIUIX -> R.string.style_miuix
                DesignStyle.CUPERTINO -> R.string.style_cupertino
                DesignStyle.MINIMAL -> R.string.style_minimal
            })
        }
        AppearanceChoice(
            icon = Icons.Rounded.DisplaySettings, title = stringResource(R.string.ui_style), items = labels, selected = styles.indexOf(settings.designStyle),
            onSelect = { onDesignStyleChanged(styles[it]) },
        )
        PreferenceItem(icon = Icons.Rounded.Palette, showArrow = true, title = stringResource(R.string.theme_mode), description = stringResource(R.string.open_theme_description), onClick = { onOpenTheme() })
    }
}
