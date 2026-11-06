package vegabobo.dsusideloader.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import vegabobo.dsusideloader.preferences.DesignStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import vegabobo.dsusideloader.ui.theme.metrics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    barTitle: String,
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    onClickIcon: () -> Unit = {},
    onClickBackButton: (() -> Unit)? = null,
) {
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        MiuixTopAppBar(
            title = barTitle,
            color = MaterialTheme.colorScheme.background,
            navigationIcon = {
                if (onClickBackButton != null) {
                    MiuixIconButton(onClick = onClickBackButton) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(vegabobo.dsusideloader.R.string.navigate_back))
                    }
                }
            },
            actions = {
                if (icon != null) MiuixIconButton(onClick = onClickIcon) { Icon(icon, contentDescription = iconContentDescription) }
            },
        )
        return
    }
    val title: @Composable () -> Unit = { Text(text = barTitle, style = MaterialTheme.typography.titleMedium) }
    val navigationIcon: @Composable () -> Unit = {
        if (onClickBackButton != null) {
            IconButton(onClickBackButton) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(vegabobo.dsusideloader.R.string.navigate_back))
            }
        }
    }
    val actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {
        if (icon != null) {
            IconButton(onClickIcon) { Icon(icon, contentDescription = iconContentDescription) }
        }
    }
    if (LocalDesignStyle.current.metrics().leftAlignedTitle) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            ),
        )
    } else {
        CenterAlignedTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            ),
        )
    }
}
