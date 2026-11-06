package vegabobo.dsusideloader.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cottage
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar

@Composable
fun MainBottomBar(selectedRoute: String, onSelect: (String) -> Unit) {
    val tabs = listOf(
        Triple(Destinations.Homepage, stringResource(R.string.home_tab), Icons.Rounded.Cottage),
        Triple(Destinations.Preferences, stringResource(R.string.settings), Icons.Rounded.Settings),
    )
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        MiuixNavigationBar {
            tabs.forEach { (route, label, icon) ->
                NavigationBarItem(
                    modifier = Modifier.weight(1f),
                    selected = selectedRoute == route,
                    onClick = { onSelect(route) }, icon = icon, label = label,
                )
            }
        }
    } else {
        NavigationBar {
            tabs.forEach { (route, label, icon) ->
                NavigationBarItem(
                    selected = selectedRoute == route, onClick = { onSelect(route) },
                    icon = { Icon(icon, contentDescription = null) }, label = { Text(label) },
                )
            }
        }
    }
}
