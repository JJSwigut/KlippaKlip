import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.models.listenerCloseKeys
import data.models.listenerOpenKeys
import data.persistence.DbFactory
import feature.AppCoordinator
import feature.settings.SettingsStrings
import feature.tray.KlipTray
import repository.klips.KlipRepoImpl
import repository.settings.Prefs
import ui.theme.colorScheme
import ui.theme.jetbrainsFamily
import utils.GlobalKeyListener
import utils.KlipListener
import utils.KlipboardManager

@Composable
fun App(
    coordinator: AppCoordinator
) {
    coordinator.WindowContent()
    coordinator.KlipsContent()
}

fun main() = application {
    val prefs = remember { Prefs }

    val coordinator = remember {
        AppCoordinator(
            onExit = { exitApplication() },
            repo = KlipRepoImpl(DbFactory.createDb()),
            prefs = prefs,
            clipboardManager = KlipboardManager()
        )
    }

    with(prefs.settings.value){
        GlobalKeyListener(
            openKeys = listenerOpenKeys,
            closeKeys = listenerCloseKeys,
            onOutput = coordinator::handleOutput
        )

        if(shouldTrackHistory) {
            KlipListener(
                clipboardManager = coordinator.clipboardManager,
                onOutput = coordinator::handleOutput
            )
        }
    }

    KlipTray(coordinator)

    MaterialTheme(
        colors = colorScheme(),
        shapes = MaterialTheme.shapes.copy(large = RoundedCornerShape(12.dp)),
        typography = Typography(defaultFontFamily = jetbrainsFamily)
    ) {
        App(coordinator)
    }
}