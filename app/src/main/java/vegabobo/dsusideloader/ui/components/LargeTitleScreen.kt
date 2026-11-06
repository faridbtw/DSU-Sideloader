package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.theme.MiuixTheme
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTitleScreen(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val scroll = rememberScrollState()
    val isolation = remember { ScrollIsolationState() }
    val backLabel = stringResource(R.string.navigate_back)
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        val behavior = MiuixScrollBehavior()
        val connection = remember(behavior, isolation) { GuardedScrollConnection(behavior.nestedScrollConnection, isolation) }
        MiuixScaffold(
            modifier = Modifier.fillMaxSize().trackScrollOwner(isolation, child = false),
            topBar = {
                MiuixTopAppBar(
                    title = title,
                    scrollBehavior = behavior,
                    navigationIcon = {
                        if (onBack != null) MiuixIconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, backLabel, tint = MiuixTheme.colorScheme.onSurface)
                        }
                    },
                )
            },
        ) { insets ->
            Column(
                modifier = Modifier.fillMaxSize().nestedScroll(connection)
                    .verticalScroll(scroll).padding(insets).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = { CompositionLocalProvider(LocalScrollIsolation provides isolation) { content() } },
            )
        }
    } else {
        val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val connection = remember(behavior, isolation) { GuardedScrollConnection(behavior.nestedScrollConnection, isolation) }
        Scaffold(
            modifier = Modifier.fillMaxSize().trackScrollOwner(isolation, child = false),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                LargeTopAppBar(
                    title = { Text(title) }, scrollBehavior = behavior,
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    navigationIcon = {
                        if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, backLabel) }
                    },
                )
            },
        ) { insets ->
            Column(
                modifier = Modifier.fillMaxSize().nestedScroll(connection)
                    .verticalScroll(scroll).padding(insets).padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = { CompositionLocalProvider(LocalScrollIsolation provides isolation) { content() } },
            )
        }
    }
}

