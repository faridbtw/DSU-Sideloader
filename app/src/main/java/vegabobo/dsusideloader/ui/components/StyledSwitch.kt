package vegabobo.dsusideloader.ui.components

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import top.yukonga.miuix.kmp.basic.Switch as MiuixSwitch

@Composable
fun StyledSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        MiuixSwitch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, modifier = modifier)
    } else {
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, modifier = modifier)
    }
}
