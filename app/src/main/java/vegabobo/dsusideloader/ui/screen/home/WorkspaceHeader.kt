package vegabobo.dsusideloader.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.util.OperationMode

@Composable
fun WorkspaceHeader(operationMode: OperationMode, uiState: HomeUiState) {
    val mode = when (operationMode) {
        OperationMode.ADB -> "ADB"
        OperationMode.SHIZUKU -> "Shizuku"
        OperationMode.ROOT -> "Root"
        OperationMode.SYSTEM -> "System"
        OperationMode.SYSTEM_AND_ROOT -> "System + Root"
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.your_workspace),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = stringResource(R.string.operation_mode_badge, mode),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if (uiState.passedInitialChecks && uiState.additionalCard == AdditionalCardState.NONE) {
            InstallationJourney(uiState.installationCard)
        }
    }
}

@Composable
private fun InstallationJourney(state: InstallationCardState) {
    val currentStep = when {
        state.installationStep != InstallationStep.NOT_INSTALLING -> 2
        state.isInstallable -> 1
        else -> 0
    }
    val labels = listOf(R.string.journey_choose, R.string.journey_configure, R.string.journey_install)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val active = index == currentStep
            val reached = index <= currentStep
            val color = if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(3.dp)
                        .background(if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (index < currentStep) {
                        Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
                    } else {
                        Text(text = "0${index + 1}", color = color, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(
                        text = stringResource(label),
                        color = color,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
