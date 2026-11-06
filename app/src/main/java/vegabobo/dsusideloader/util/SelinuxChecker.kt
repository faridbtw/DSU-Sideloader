package vegabobo.dsusideloader.util

import com.topjohnwu.superuser.Shell
import java.util.Locale

// Only observes SELinux; this checker never changes the enforcement policy.
enum class SelinuxMode { NOT_CHECKED, ENFORCING, PERMISSIVE, DISABLED, ROOT_UNAVAILABLE, UNAVAILABLE }

data class SelinuxState(val mode: SelinuxMode = SelinuxMode.NOT_CHECKED, val checking: Boolean = false)

fun parseGetenforce(output: String, exitCode: Int): SelinuxMode {
    if (exitCode != 0) return SelinuxMode.UNAVAILABLE
    return when (output.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.lastOrNull()?.lowercase(Locale.ROOT)) {
        "enforcing" -> SelinuxMode.ENFORCING
        "permissive" -> SelinuxMode.PERMISSIVE
        "disabled" -> SelinuxMode.DISABLED
        else -> SelinuxMode.UNAVAILABLE
    }
}

object SelinuxChecker {
    fun checkWithRoot(): SelinuxMode {
        val shell = Shell.getShell()
        if (!shell.isRoot) return SelinuxMode.ROOT_UNAVAILABLE
        // libsu has already opened su. Execute getenforce in that verified root shell.
        val result = shell.newJob().add("getenforce").exec()
        return parseGetenforce(result.out.joinToString("\n"), result.code)
    }
}
