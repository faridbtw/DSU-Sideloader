package vegabobo.dsusideloader.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.util.SelinuxMode
import vegabobo.dsusideloader.util.SelinuxState

@Composable
fun SelinuxStatusRow(state: SelinuxState, onCheck: () -> Unit) {
    val status = when {
        state.checking -> R.string.selinux_checking
        else -> when (state.mode) {
            SelinuxMode.NOT_CHECKED -> R.string.selinux_not_checked
            SelinuxMode.ENFORCING -> R.string.selinux_enforcing
            SelinuxMode.PERMISSIVE -> R.string.selinux_permissive
            SelinuxMode.DISABLED -> R.string.selinux_disabled
            SelinuxMode.ROOT_UNAVAILABLE -> R.string.selinux_no_root
            SelinuxMode.UNAVAILABLE -> R.string.selinux_unavailable
        }
    }
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.Security, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                Text(stringResource(R.string.selinux_title), style = MaterialTheme.typography.labelLarge)
                Text(stringResource(status), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (state.checking) {
                CircularProgressIndicator(modifier = Modifier.padding(14.dp).size(20.dp), strokeWidth = 2.dp)
            } else IconButton(onClick = onCheck) {
                Icon(Icons.Outlined.Refresh, contentDescription = stringResource(R.string.selinux_check_root))
            }
        }
    }
}
