package vegabobo.dsusideloader.launcher

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import vegabobo.dsusideloader.preferences.LauncherIcon

@Singleton
class LauncherIconManager @Inject constructor(@ApplicationContext private val context: Context) {
    fun apply(icon: LauncherIcon) {
        val pm = context.packageManager
        val changes = LauncherIcon.entries.mapNotNull { candidate ->
            val component = ComponentName(context.packageName, candidate.componentClass(context.packageName))
            val current = pm.getComponentEnabledSetting(component)
            val enabled = current == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                (current == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && candidate == LauncherIcon.MATERIAL)
            val wanted = candidate == icon
            if (enabled == wanted) null else component to if (wanted) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (changes.isNotEmpty()) pm.setComponentEnabledSettings(changes.map { (component, state) ->
                PackageManager.ComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP)
            })
        } else {
            // Older Android versions cannot batch changes. Enable the new entry before hiding the old one.
            changes.sortedByDescending { it.second == PackageManager.COMPONENT_ENABLED_STATE_ENABLED }
                .forEach { (component, state) -> pm.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP) }
        }
    }
}
