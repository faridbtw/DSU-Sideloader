package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import top.yukonga.miuix.kmp.basic.BasicComponent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceItem(
    title: String,
    description: String = "",
    icon: ImageVector? = null,
    onClick: (Boolean) -> Unit = {},
    isChecked: Boolean = false,
    showToggle: Boolean = false,
    isEnabled: Boolean = true,
    showArrow: Boolean = false,
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        val leading: @Composable (() -> Unit)? = if (icon != null) ({
            Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
        }) else null
        when {
            showToggle -> SwitchPreference(
                title = title, summary = description.takeIf { it.isNotEmpty() },
                enabled = isEnabled, startAction = leading, checked = isChecked,
                onCheckedChange = { onClick(isChecked) },
            )
            showArrow -> ArrowPreference(
                title = title, summary = description.takeIf { it.isNotEmpty() },
                enabled = isEnabled, startAction = leading, onClick = { onClick(isChecked) },
            )
            else -> BasicComponent(
                title = title, summary = description.takeIf { it.isNotEmpty() },
                enabled = isEnabled, startAction = leading, onClick = { onClick(isChecked) },
            )
        }
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.5f)
            .clickable(onClick = { onClick(isChecked) }, enabled = isEnabled)
            .padding(
                start = 17.dp,
                end = 17.dp,
                bottom = 12.dp,
                top = 12.dp,
            ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 22.dp),
            )
        }
        Row(modifier = Modifier.weight(0.5F)) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (description.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        if (showToggle) {
            StyledSwitch(
                checked = isChecked,
                enabled = isEnabled,
                onCheckedChange = { onClick(isChecked) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        if (showArrow && !showToggle) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

    }
}
