package vegabobo.dsusideloader.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IndependentScrollTest {
    private class Parent : NestedScrollConnection {
        var calls = 0
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset { calls++; return available }
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset { calls++; return available }
        override suspend fun onPreFling(available: Velocity): Velocity { calls++; return available }
        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity { calls++; return available }
    }

    @Test
    fun listGestureAndFlingCannotMoveTheParentHeader() = runBlocking {
        val state = ScrollIsolationState().apply { childOwnsGesture = true }
        val parent = Parent()
        val connection = GuardedScrollConnection(parent, state)
        assertEquals(Offset.Zero, connection.onPreScroll(Offset(0f, -70f), NestedScrollSource.UserInput))
        assertEquals(Offset.Zero, connection.onPostScroll(Offset(0f, -70f), Offset(0f, -20f), NestedScrollSource.UserInput))
        assertEquals(Velocity.Zero, connection.onPreFling(Velocity(0f, -1000f)))
        assertEquals(Velocity.Zero, connection.onPostFling(Velocity(0f, -800f), Velocity(0f, -200f)))
        assertEquals(0, parent.calls)
        assertTrue(state.childOwnsGesture)
    }

    @Test
    fun listEdgesAbsorbLeftoverVerticalMotionInBothDirections() = runBlocking {
        val boundary = IndependentListScrollConnection
        listOf(-60f, 60f).forEach { delta ->
            assertEquals(Offset(0f, delta), boundary.onPostScroll(Offset.Zero, Offset(12f, delta), NestedScrollSource.UserInput))
            assertEquals(Velocity(0f, delta), boundary.onPostFling(Velocity.Zero, Velocity(12f, delta)))
        }
    }

    @Test
    fun nextGestureOutsideTheListRestoresPageScrolling() = runBlocking {
        val state = ScrollIsolationState().apply { childOwnsGesture = true }
        val parent = Parent()
        val connection = GuardedScrollConnection(parent, state)
        state.childOwnsGesture = false
        assertEquals(Offset(0f, -50f), connection.onPreScroll(Offset(0f, -50f), NestedScrollSource.UserInput))
        assertEquals(Offset(0f, 25f), connection.onPostScroll(Offset.Zero, Offset(0f, 25f), NestedScrollSource.UserInput))
        assertEquals(Velocity(0f, -100f), connection.onPreFling(Velocity(0f, -100f)))
        assertEquals(Velocity(0f, 100f), connection.onPostFling(Velocity.Zero, Velocity(0f, 100f)))
        assertEquals(4, parent.calls)
    }
}
