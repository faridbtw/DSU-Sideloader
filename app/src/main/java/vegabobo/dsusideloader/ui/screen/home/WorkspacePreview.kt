package vegabobo.dsusideloader.ui.screen.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.cards.DsuInfoCard
import vegabobo.dsusideloader.ui.cards.installation.content.NotInstallingCardContent
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.OperationMode

@Preview(name = "Workspace · Light", widthDp = 393, heightDp = 852, showBackground = true)
@Preview(name = "Workspace · Dark", widthDp = 393, heightDp = 852, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Workspace · Large text", widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Composable
private fun WorkspacePreview() {
    DSUHelperTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WorkspaceHeader(OperationMode.SHIZUKU, HomeUiState(passedInitialChecks = true))
                CardBox(cardTitle = stringResource(R.string.system_image)) {
                    NotInstallingCardContent(
                        onClickSelectFile = {},
                        uiState = InstallationCardState(),
                        onClickClear = {},
                        onClickInstall = {},
                    )
                }
                AdvancedOptions(hasCustomValues = false) {}
                DsuInfoCard(onClickViewDocs = {}, onClickLearnMore = {})
            }
        }
    }
}
