package vegabobo.dsusideloader.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cottage
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.FloatingNavigationBar
import top.yukonga.miuix.kmp.basic.FloatingNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationBarDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.DesignStyle
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle

@Composable
fun MainBottomBar(selectedRoute: String, onSelect: (String) -> Unit) {
    val tabs = listOf(
        Triple(Destinations.Homepage, stringResource(R.string.home_tab), Icons.Rounded.Cottage),
        Triple(Destinations.Preferences, stringResource(R.string.settings), Icons.Rounded.Settings),
    )
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        FloatingNavigationBar(shadowElevation = 8.dp, showDivider = true) {
            tabs.forEach { (route, label, icon) ->
                val selected = selectedRoute == route
                val indicator by animateColorAsState(if (selected) MiuixTheme.colorScheme.primaryContainer else Color.Transparent)
                FloatingNavigationBarItem(
                    modifier = Modifier.clip(RoundedCornerShape(22.dp)).background(indicator),
                    selected = selected, onClick = { onSelect(route) }, icon = icon, label = label,
                    colors = NavigationBarDefaults.navigationBarItemColors(
                        selectedContentColor = MiuixTheme.colorScheme.onPrimaryContainer,
                    ),
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(top = 8.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shadowElevation = 8.dp,
            ) {
                Row(modifier = Modifier.padding(8.dp).selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tabs.forEach { (route, label, icon) ->
                        val selected = selectedRoute == route
                        val indicator by animateColorAsState(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        val foreground = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        Column(
                            modifier = Modifier.width(96.dp).height(56.dp).clip(RoundedCornerShape(24.dp))
                                .background(indicator).selectable(selected = selected, role = Role.Tab, onClick = { onSelect(route) }),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(24.dp))
                            Text(label, color = foreground, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
