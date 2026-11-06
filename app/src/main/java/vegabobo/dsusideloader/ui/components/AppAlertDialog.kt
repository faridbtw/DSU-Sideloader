package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.window.WindowDialog
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable () -> Unit = {},
    icon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    text: @Composable () -> Unit = {},
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        WindowDialog(show = true, modifier = modifier, onDismissRequest = onDismissRequest) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                icon()
                title()
                text()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    dismissButton()
                    confirmButton()
                }
            }
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismissRequest, modifier = modifier,
            confirmButton = confirmButton, dismissButton = dismissButton,
            icon = icon, title = title, text = text,
        )
    }
}
