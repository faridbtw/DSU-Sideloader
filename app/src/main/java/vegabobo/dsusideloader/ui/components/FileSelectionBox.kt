package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSelectionBox(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    isEnabled: Boolean,
    isError: Boolean,
    textFieldTitle: String,
    textFieldValue: String,
    textFieldInteraction: MutableInteractionSource = MutableInteractionSource(),
    onValueChange: (String) -> Unit = {},
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        MiuixTextField(
            value = textFieldValue, onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth().then(
                if (isError) Modifier.border(1.dp, MiuixTheme.colorScheme.error, RoundedCornerShape(TextFieldDefaults.CornerRadius)) else Modifier,
            ),
            colors = if (isError) TextFieldDefaults.textFieldColors(
                labelColor = MiuixTheme.colorScheme.error, borderColor = MiuixTheme.colorScheme.error,
            ) else TextFieldDefaults.textFieldColors(),
            label = textFieldTitle, enabled = isEnabled, readOnly = isReadOnly,
            singleLine = true, keyboardOptions = keyboardOptions,
            interactionSource = textFieldInteraction,
        )
        return
    }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = textFieldValue,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        placeholder = { Text(text = "") },
        onValueChange = onValueChange,
        enabled = isEnabled,
        isError = isError,
        singleLine = true,
        readOnly = isReadOnly,
        keyboardOptions = keyboardOptions,
        interactionSource = textFieldInteraction,
        label = { Text(text = textFieldTitle) },
    )
}
