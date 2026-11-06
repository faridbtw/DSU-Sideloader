package vegabobo.dsusideloader.ui.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import vegabobo.dsusideloader.ui.theme.LocalScrollAnimation
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import vegabobo.dsusideloader.ui.screen.settings.AppIconsScreen
import vegabobo.dsusideloader.ui.screen.settings.Settings
import vegabobo.dsusideloader.ui.screen.settings.ThemeScreen

object Destinations {
    const val Homepage = "home"
    const val Preferences = "preferences"
    const val Theme = "theme"
    const val AppIcons = "app_icons"
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
    val animateTabs = LocalScrollAnimation.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val showTabs = route == Destinations.Homepage || route == Destinations.Preferences
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
            enterTransition = {
                val from = initialState.destination.route
                val to = targetState.destination.route
                if (isMainTabChange(from, to)) {
                    if (animateTabs) slideIntoContainer(
                        towards = if ((to == Destinations.Preferences) != rtl) AnimatedContentTransitionScope.SlideDirection.Left else AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(320, easing = FastOutSlowInEasing),
                    ) else EnterTransition.None
                } else fadeIn(tween(220))
            },
            exitTransition = {
                val from = initialState.destination.route
                val to = targetState.destination.route
                if (isMainTabChange(from, to)) {
                    if (animateTabs) slideOutOfContainer(
                        towards = if ((to == Destinations.Preferences) != rtl) AnimatedContentTransitionScope.SlideDirection.Left else AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(320, easing = FastOutSlowInEasing),
                    ) else ExitTransition.None
                } else fadeOut(tween(220))
            },
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
            composable(Destinations.AppIcons) { AppIconsScreen(navigate = { navigate(it) }) }
            composable(Destinations.Theme) { ThemeScreen(navigate = { navigate(it) }) }
            composable(Destinations.ADBInstallation) { AdbScreen(navigate = { navigate(it) }) }
            composable(Destinations.About) { AboutScreen(navigate = { navigate(it) }) }
            composable(Destinations.Libraries) { LibrariesScreen(navigate = { navigate(it) }) }
        }
    }
}

private fun isMainTabChange(from: String?, to: String?): Boolean =
    (from == Destinations.Homepage && to == Destinations.Preferences) ||
        (from == Destinations.Preferences && to == Destinations.Homepage)
