package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.launcher.labelResource
import vegabobo.dsusideloader.launcher.previewResource
import vegabobo.dsusideloader.preferences.LauncherIcon
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle

@Composable
fun AppIconsScreen(navigate: (String) -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val enabled = !state.isInstalling && !state.changingIcon
    SettingsPage(title = stringResource(R.string.app_icon), onBack = { navigate(Destinations.Up) }) {
        SettingsGroup {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp).selectableGroup()) {
                LauncherIcon.entries.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { icon ->
                            val selected = icon == state.appIcon
                            Column(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                                    .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = { viewModel.setAppIcon(icon) })
                                    .padding(horizontal = 4.dp, vertical = 12.dp).alpha(if (enabled) 1f else 0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier.size(80.dp).border(
                                        2.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(24.dp),
                                    ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painterResource(icon.previewResource), contentDescription = null,
                                        modifier = Modifier.size(68.dp).clip(RoundedCornerShape(18.dp)),
                                    )
                                    if (selected) Icon(
                                        Icons.Rounded.Check, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.align(Alignment.BottomEnd).size(23.dp)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape).padding(4.dp),
                                    )
                                }
                                Text(stringResource(icon.labelResource), style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 2)
                            }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        Text(
            stringResource(if (state.isInstalling) R.string.operation_busy else R.string.app_icon_notice),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        if (state.iconChangeFailed) Text(stringResource(R.string.app_icon_error), color = MaterialTheme.colorScheme.error)
    }
}
