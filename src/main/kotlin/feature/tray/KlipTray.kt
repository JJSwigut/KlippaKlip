package feature.tray

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.rememberTrayState
import feature.AppCoordinator
import feature.Output
import feature.tray.MenuOutput.*

@Composable
fun ApplicationScope.KlipTray(coordinator: AppCoordinator) {
    Tray(
        icon = painterResource("tray_icon.svg"),
        state = rememberTrayState(),
        menu = {
            AppMenu(coordinator)
        }
    )
}

@Composable
private fun MenuScope.AppMenu(coordinator: AppCoordinator) {
    Item("Klips", onClick = { coordinator.handleOutput(ShowKlips) })
    Item("Create", onClick = { coordinator.handleOutput(ShowCreate) })
    Item("Settings", onClick = { coordinator.handleOutput(ShowSettings) })
    Separator()
    Item("Exit", onClick = { coordinator.handleOutput(Exit) })
}

sealed interface MenuOutput: Output {
    data object ShowKlips : MenuOutput
    data object ShowSettings : MenuOutput
    data object ShowCreate : MenuOutput
    data object Exit : MenuOutput
}