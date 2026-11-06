package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.Title

@Composable
fun AppearanceSettingsCard(settings: AppearanceSettings, onDesignStyleChanged: (DesignStyle) -> Unit, onOpenTheme: () -> Unit) {
    Title(title = stringResource(R.string.appearance))
    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
        CardBox(addPadding = false) {
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
                title = stringResource(R.string.ui_style), items = labels, selected = styles.indexOf(settings.designStyle),
                onSelect = { onDesignStyleChanged(styles[it]) },
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            PreferenceItem(title = stringResource(R.string.theme_mode), description = stringResource(R.string.open_theme_description), onClick = { onOpenTheme() })
        }
    }
}
