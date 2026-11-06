package vegabobo.dsusideloader.ui.components

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity

class ScrollIsolationState {
    // Retain ownership after pointer-up so the child's fling stays isolated too.
    // The next pointer-down resets ownership at the page before reaching a child.
    var childOwnsGesture = false
}

val LocalScrollIsolation = staticCompositionLocalOf<ScrollIsolationState?> { null }

fun Modifier.trackScrollOwner(state: ScrollIsolationState?, child: Boolean): Modifier =
    if (state == null) this else pointerInput(state, child) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.type == PointerEventType.Scroll || event.changes.any { it.pressed && !it.previousPressed }) {
                    state.childOwnsGesture = child
                }
            }
        }
    }

class GuardedScrollConnection(
    private val delegate: NestedScrollConnection,
    private val state: ScrollIsolationState,
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
        if (state.childOwnsGesture) Offset.Zero else delegate.onPreScroll(available, source)

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
        if (state.childOwnsGesture) Offset.Zero else delegate.onPostScroll(consumed, available, source)

    override suspend fun onPreFling(available: Velocity): Velocity =
        if (state.childOwnsGesture) Velocity.Zero else delegate.onPreFling(available)

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        if (state.childOwnsGesture) Velocity.Zero else delegate.onPostFling(consumed, available)
}

object IndependentListScrollConnection : NestedScrollConnection {
    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource) = Offset(0f, available.y)
    override suspend fun onPostFling(consumed: Velocity, available: Velocity) = Velocity(0f, available.y)
}
