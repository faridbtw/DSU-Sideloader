package vegabobo.dsusideloader.ui.screen.home

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Refresh
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
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.util.launcherAcResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderImagesCard(
    state: FolderImagesState,
    selectedUri: String,
    enabled: Boolean,
    onRefresh: () -> Unit,
    onChangeFolder: (Uri) -> Unit,
    onSelectImage: (FolderImage) -> Unit,
) {
    var showAll by rememberSaveable(state.folderUri) { mutableStateOf(false) }
    val folderLauncher = launcherAcResult(onChangeFolder)
    val context = LocalContext.current

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
                    if (state.folderUri.isNotBlank()) {
                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, state.folderUri.toUri())
                    }
                    folderLauncher.launch(intent)
                },
            ) {
                Icon(Icons.Outlined.FolderOpen, contentDescription = stringResource(R.string.change_image_folder))
            }
            IconButton(enabled = enabled && !state.isLoading, onClick = onRefresh) {
                Icon(Icons.Outlined.Refresh, contentDescription = stringResource(R.string.refresh_image_folder))
            }
        }
        Text(
            stringResource(R.string.folder_image_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )
        when {
            state.isLoading -> Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(stringResource(R.string.scanning_image_folder), style = MaterialTheme.typography.bodyMedium)
            }
            state.hasError -> Text(
                stringResource(R.string.image_folder_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
            state.images.isEmpty() -> Text(
                stringResource(R.string.image_folder_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val visible = if (showAll) state.images else state.images.take(5)
                visible.forEach { image ->
                    val isSelected = image.uriString == selectedUri
                    Surface(
                        onClick = { onSelectImage(image) },
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth().semantics { selected = isSelected },
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(image.filename, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
                                if (image.relativePath != image.filename) {
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
                if (state.images.size > 5) {
                    TextButton(onClick = { showAll = !showAll }) {
                        Text(
                            if (showAll) {
                                stringResource(R.string.show_fewer_images)
                            } else {
                                stringResource(R.string.show_all_images, state.images.size)
                            },
                        )
                    }
                }
            }
        }
        if (state.limited && !state.isLoading && !state.hasError) {
            Text(
                stringResource(R.string.image_scan_limited),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
