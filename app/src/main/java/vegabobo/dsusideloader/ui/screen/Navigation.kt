package vegabobo.dsusideloader.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import vegabobo.dsusideloader.ui.components.MainBottomBar
import vegabobo.dsusideloader.ui.screen.about.AboutScreen
import vegabobo.dsusideloader.ui.screen.adb.AdbScreen
import vegabobo.dsusideloader.ui.screen.home.Home
import vegabobo.dsusideloader.ui.screen.libraries.LibrariesScreen
import vegabobo.dsusideloader.ui.screen.settings.Settings
import vegabobo.dsusideloader.ui.screen.settings.ThemeScreen

object Destinations {
    const val Homepage = "home"
    const val Preferences = "preferences"
    const val Theme = "theme"
    const val ADBInstallation = "adb_installation"
    const val About = "about"
    const val Libraries = "libraries"
    const val Up = "up"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: Destinations.Homepage
    val showTabs = route == Destinations.Homepage || route == Destinations.Preferences
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showTabs) MainBottomBar(selectedRoute = route) { destination ->
                if (destination != route) {
                    navController.navigate(destination) {
                        popUpTo(Destinations.Homepage) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Homepage,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        ) {
            fun navigate(destination: String) {
                if (destination == Destinations.Up) {
                    navController.navigateUp()
                } else {
                    navController.navigate(destination)
                }
            }

            composable(Destinations.Homepage) { Home(navigate = { navigate(it) }) }
            composable(Destinations.Preferences) { Settings(navigate = { navigate(it) }) }
            composable(Destinations.Theme) { ThemeScreen(navigate = { navigate(it) }) }
            composable(Destinations.ADBInstallation) { AdbScreen(navigate = { navigate(it) }) }
            composable(Destinations.About) { AboutScreen(navigate = { navigate(it) }) }
            composable(Destinations.Libraries) { LibrariesScreen(navigate = { navigate(it) }) }
        }
    }
}
