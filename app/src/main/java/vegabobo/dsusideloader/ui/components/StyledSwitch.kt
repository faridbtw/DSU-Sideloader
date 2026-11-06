package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import vegabobo.dsusideloader.preferences.DesignStyle
import top.yukonga.miuix.kmp.basic.Switch as MiuixSwitch
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import vegabobo.dsusideloader.ui.theme.metrics

@Composable
fun StyledSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        MiuixSwitch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, modifier = modifier)
        return
    }
    if (!LocalDesignStyle.current.metrics().roundedSwitches) {
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, modifier = modifier)
        return
    }
    val progress by animateFloatAsState(if (checked) 1f else 0f, animationSpec = tween(150))
    val track by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(150),
    )
    val thumb by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(150),
    )
    Box(
        modifier = modifier.sizeIn(minWidth = 52.dp, minHeight = 48.dp)
            .alpha(if (enabled) 1f else 0.45f)
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.size(48.dp, 28.dp).clip(CircleShape).background(track)) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart)
                    .offset(x = (3 + 20 * progress).dp)
                    .size(22.dp).clip(CircleShape).background(thumb),
            )
        }
    }
}
