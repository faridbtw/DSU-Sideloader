package vegabobo.dsusideloader.ui.cards.installation.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton
import vegabobo.dsusideloader.ui.screen.home.InstallationCardState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotInstallingCardContent(
    onClickSelectFile: () -> Unit,
    uiState: InstallationCardState,
    onClickClear: () -> Unit,
    onClickInstall: () -> Unit,
    showFilePicker: Boolean = true,
) {
    if (showFilePicker) {
        Surface(
            onClick = onClickSelectFile,
            enabled = uiState.isTextFieldEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            border = BorderStroke(
                1.dp,
                if (uiState.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = if (uiState.isInstallable) Icons.Outlined.Description else Icons.Outlined.UploadFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = uiState.text.ifEmpty { stringResource(R.string.choose_system_image) },
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(if (uiState.isInstallable) R.string.tap_to_change_image else R.string.supported_image_formats),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
    if (uiState.isError) {
        Text(
            text = stringResource(R.string.file_unsupported),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    ) {
        if (uiState.isInstallable) {
            SecondaryButton(text = stringResource(R.string.clear), onClick = onClickClear)
        }
        PrimaryButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.install),
            onClick = onClickInstall,
            isEnabled = uiState.isInstallable,
        )
    }
}
