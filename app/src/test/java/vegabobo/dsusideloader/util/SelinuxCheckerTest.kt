package vegabobo.dsusideloader.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SelinuxCheckerTest {
    @Test
    fun recognizesEveryGetenforceMode() {
        assertEquals(SelinuxMode.ENFORCING, parseGetenforce("Enforcing", 0))
        assertEquals(SelinuxMode.PERMISSIVE, parseGetenforce("  Permissive\n", 0))
        assertEquals(SelinuxMode.DISABLED, parseGetenforce("disabled", 0))
    }

    @Test
    fun commandFailuresCannotBeMistakenForAValidStatus() {
        assertEquals(SelinuxMode.UNAVAILABLE, parseGetenforce("Enforcing", 1))
        assertEquals(SelinuxMode.UNAVAILABLE, parseGetenforce("su: permission denied", 1))
        assertEquals(SelinuxMode.UNAVAILABLE, parseGetenforce("getenforce: not found", 127))
    }

    @Test
    fun ignoresBlankLinesButDoesNotGuessFromUnrecognizedOutput() {
        assertEquals(SelinuxMode.PERMISSIVE, parseGetenforce("shell notice\nPermissive\n\n", 0))
        assertEquals(SelinuxMode.UNAVAILABLE, parseGetenforce("\n  \n", 0))
        assertEquals(SelinuxMode.UNAVAILABLE, parseGetenforce("SELinux: Enforcing", 0))
    }
}
