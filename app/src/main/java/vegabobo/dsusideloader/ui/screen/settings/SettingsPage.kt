package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.components.LargeTitleScreen

@Composable
fun SettingsPage(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    LargeTitleScreen(title = title, onBack = onBack, content = content)
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    CardBox(addPadding = false) { Column(content = content) }
}
