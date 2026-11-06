package vegabobo.dsusideloader.ui.screen.home

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.model.FolderImage
import vegabobo.dsusideloader.model.FolderImageFormat
import vegabobo.dsusideloader.ui.cards.installation.content.NotInstallingCardContent
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.util.launcherAcResult
import vegabobo.dsusideloader.ui.util.systemImageSelectionIntent

@Composable
fun FolderImagesCard(
    state: FolderImagesState,
    installation: InstallationCardState,
    enabled: Boolean,
    onRefresh: () -> Unit,
    onChangeFolder: (Uri) -> Unit,
    onSelectImage: (FolderImage) -> Unit,
    onSelectFile: (Uri) -> Unit,
    onInstall: () -> Unit,
    onClear: () -> Unit,
) {
    var showAll by rememberSaveable(state.folderUri) { mutableStateOf(false) }
    val folderLauncher = launcherAcResult(onChangeFolder)
    val imageLauncher = launcherAcResult(onSelectFile)
    val browseImage = { imageLauncher.launch(systemImageSelectionIntent()) }
    val selectedImage = state.images.firstOrNull { it.uriString == installation.selectedFileUri }

    CardBox {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.folder_system_images), style = MaterialTheme.typography.titleMedium)
                if (state.folderName.isNotBlank()) {
                    Text(
                        state.folderName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(
                enabled = enabled,
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    if (state.folderUri.isNotBlank()) intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, state.folderUri.toUri())
                    folderLauncher.launch(intent)
                },
            ) {
                Icon(Icons.Outlined.FolderOpen, contentDescription = stringResource(R.string.change_image_folder))
            }
            IconButton(enabled = enabled && !state.isLoading, onClick = onRefresh) {
                Icon(Icons.Outlined.Refresh, contentDescription = stringResource(R.string.refresh_image_folder))
            }
        }
        when {
            state.isLoading -> Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(stringResource(R.string.scanning_image_folder), style = MaterialTheme.typography.bodySmall)
            }
            state.hasError -> Text(
                stringResource(R.string.image_folder_error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
            state.images.isEmpty() -> Text(
                stringResource(R.string.image_folder_empty),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Keep the current selection visible even when it comes from the full list.
                val preview = (listOfNotNull(selectedImage) + state.images.filter { it.uriString != installation.selectedFileUri }).take(2)
                preview.forEach { image ->
                    FolderImageRow(
                        image = image,
                        isSelected = image.uriString == installation.selectedFileUri,
                        enabled = enabled,
                        onClick = { onSelectImage(image) },
                    )
                }
            }
        }
        if (state.images.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = browseImage, enabled = enabled, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.browse_images))
                }
                if (state.images.size > 2) {
                    TextButton(onClick = { showAll = true }, enabled = enabled && !state.isLoading) {
                        Text(stringResource(R.string.show_all_images, state.images.size))
                    }
                }
            }
        }
        if (state.limited && !state.isLoading && !state.hasError) {
            Text(
                stringResource(R.string.image_scan_limited),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        NotInstallingCardContent(
            onClickSelectFile = browseImage,
            uiState = installation,
            onClickClear = onClear,
            onClickInstall = onInstall,
            showFilePicker = state.images.isEmpty() || (installation.isInstallable && selectedImage == null),
        )
    }

    if (showAll) {
        AlertDialog(
            onDismissRequest = { showAll = false },
            title = { Text(stringResource(R.string.folder_system_images)) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.55f).dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(state.images, key = { it.uriString }) { image ->
                        FolderImageRow(
                            image = image,
                            isSelected = image.uriString == installation.selectedFileUri,
                            enabled = enabled && !state.isLoading,
                            showPath = true,
                            onClick = {
                                onSelectImage(image)
                                showAll = false
                            },
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAll = false }) { Text(stringResource(R.string.close)) }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderImageRow(
    image: FolderImage,
    isSelected: Boolean,
    enabled: Boolean,
    showPath: Boolean = false,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().semantics { selected = isSelected },
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(image.filename, style = MaterialTheme.typography.titleSmall, maxLines = if (showPath) 2 else 1, overflow = TextOverflow.Ellipsis)
                val format = when (image.format) {
                    FolderImageFormat.IMAGE -> "IMG"
                    FolderImageFormat.GZIP -> "GZIP"
                    FolderImageFormat.XZ -> "XZ"
                    FolderImageFormat.DSU_ZIP -> stringResource(R.string.dsu_zip_format)
                }
                Text(
                    if (image.size > 0) "$format · ${Formatter.formatShortFileSize(context, image.size)}" else format,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (showPath && image.relativePath != image.filename) {
                    Text(
                        image.relativePath.substringBeforeLast('/'),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
