package vegabobo.dsusideloader.ui.components.buttons

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import vegabobo.dsusideloader.preferences.DesignStyle
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import vegabobo.dsusideloader.ui.theme.metrics

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    colorButton: Color? = null,
    colorText: Color? = null,
    textButton: Boolean = false,
    plainText: Boolean = false,
    isEnabled: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        if (textButton) {
            MiuixTextButton(
                text = text, onClick = onClick, enabled = isEnabled, modifier = modifier.heightIn(min = 48.dp),
                colors = if (plainText) MiuixButtonDefaults.textButtonColors(
                    color = Color.Transparent, disabledColor = Color.Transparent,
                    textColor = MiuixTheme.colorScheme.primary,
                ) else MiuixButtonDefaults.textButtonColors(),
            )
        } else {
            MiuixButton(
                onClick = onClick,
                enabled = isEnabled,
                modifier = modifier.heightIn(min = 48.dp),
                colors = MiuixButtonDefaults.buttonColorsPrimary(
                    color = colorButton ?: MiuixTheme.colorScheme.primary,
                    contentColor = colorText ?: MiuixTheme.colorScheme.onPrimary,
                ),
            ) {
                MiuixText(text = text)
                content()
            }
        }
        return
    }
    if (textButton) {
        TextButton(modifier = modifier.heightIn(min = 48.dp), onClick = onClick, enabled = isEnabled) {
            Text(text = text)
        }
    } else {
        FilledTonalButton(
            modifier = modifier.heightIn(min = 48.dp),
            shape = RoundedCornerShape(LocalDesignStyle.current.metrics().buttonRadius.dp),
            onClick = onClick,
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorButton ?: MaterialTheme.colorScheme.primary,
                contentColor = colorText ?: MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
            content()
        }
    }
}
