package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.components.AppAlertDialog
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle

@Composable
fun AppearanceChoice(title: String, items: List<String>, selected: Int, enabled: Boolean = true, onSelect: (Int) -> Unit) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        WindowDropdownPreference(title = title, items = items, selectedIndex = selected, enabled = enabled, onSelectedIndexChange = onSelect)
    } else {
        var open by rememberSaveable { mutableStateOf(false) }
        PreferenceItem(title = title, description = items.getOrElse(selected) { "" }, isEnabled = enabled, onClick = { open = true })
        if (open) {
            AppAlertDialog(
                onDismissRequest = { open = false },
                title = { Text(title) },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp).selectableGroup()) {
                        itemsIndexed(items) { index, label ->
                            Row(
                                modifier = Modifier.fillMaxWidth().selectable(
                                    selected = index == selected, role = Role.RadioButton,
                                    onClick = { onSelect(index); open = false },
                                ).padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                RadioButton(selected = index == selected, onClick = null)
                                Text(label)
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { open = false }) { Text(stringResource(R.string.close)) } },
            )
        }
    }
}
