import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.WindowState

class AppState(
    val windowState: WindowState,
    val trayState: TrayState,
    private val onExit: () -> Unit,
    private val onShow: () -> Unit,
) {
    fun exit() {
        onExit()
    }

    fun show() {
        onShow()
    }
}

@Composable
fun rememberAppState(
    windowState: WindowState,
    trayState: TrayState,
    onExit: () -> Unit,
    onShow: () -> Unit,
): AppState {
    return remember { AppState(windowState, trayState, onExit, onShow) }
}