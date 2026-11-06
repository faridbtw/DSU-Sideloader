package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.preference.SwitchPreference
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle

@Composable
fun AllocationCard(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val miuix = LocalDesignStyle.current == DesignStyle.MIUIX
    CardBox(
        modifier = modifier,
        cardTitle = if (miuix) "" else title,
        addToggle = !miuix,
        addPadding = !miuix,
        isToggleChecked = checked,
        isToggleEnabled = enabled,
        onCheckedChange = onCheckedChange,
    ) {
        if (miuix) {
            SwitchPreference(title = title, checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
        }
        AnimatedVisibility(visible = checked, enter = expandVertically(), exit = shrinkVertically()) {
            // The native switch row owns its padding; the expanded field needs a separate inset.
            Column(modifier = if (miuix) Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp) else Modifier) {
                content()
            }
        }
    }
}
