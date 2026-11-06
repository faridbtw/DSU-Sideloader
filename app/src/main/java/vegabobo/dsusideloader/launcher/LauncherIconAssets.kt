package vegabobo.dsusideloader.launcher

import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.LauncherIcon

val LauncherIcon.previewResource: Int get() = when (this) {
    LauncherIcon.STOCK -> R.drawable.icon_stock
    LauncherIcon.MATERIAL -> R.drawable.dsu_icon
    LauncherIcon.MINT -> R.drawable.icon_mint
    LauncherIcon.OCEAN -> R.drawable.icon_ocean
    LauncherIcon.SUNSET -> R.drawable.icon_sunset
    LauncherIcon.ROSE -> R.drawable.icon_rose
    LauncherIcon.GRAPHITE -> R.drawable.icon_graphite
    LauncherIcon.ORBIT -> R.drawable.icon_orbit
    LauncherIcon.CIRCUIT -> R.drawable.icon_circuit
    LauncherIcon.PRISM -> R.drawable.icon_prism
    LauncherIcon.PIXEL -> R.drawable.icon_pixel
    LauncherIcon.TERMINAL -> R.drawable.icon_terminal
    LauncherIcon.SOLAR -> R.drawable.icon_solar
}

val LauncherIcon.labelResource: Int get() = when (this) {
    LauncherIcon.STOCK -> R.string.app_icon_stock
    LauncherIcon.MATERIAL -> R.string.app_icon_material
    LauncherIcon.MINT -> R.string.app_icon_mint
    LauncherIcon.OCEAN -> R.string.app_icon_ocean
    LauncherIcon.SUNSET -> R.string.app_icon_sunset
    LauncherIcon.ROSE -> R.string.app_icon_rose
    LauncherIcon.GRAPHITE -> R.string.app_icon_graphite
    LauncherIcon.ORBIT -> R.string.app_icon_orbit
    LauncherIcon.CIRCUIT -> R.string.app_icon_circuit
    LauncherIcon.PRISM -> R.string.app_icon_prism
    LauncherIcon.PIXEL -> R.string.app_icon_pixel
    LauncherIcon.TERMINAL -> R.string.app_icon_terminal
    LauncherIcon.SOLAR -> R.string.app_icon_solar
}
