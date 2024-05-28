import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import data.persistence.DbFactory
import feature.AppCoordinator
import feature.tray.KlipTray
import repository.KlipRepoImpl
import ui.theme.colorScheme
import ui.theme.sonosFamily
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

    val coordinator = remember {
        AppCoordinator(
            onExit = { exitApplication() },
            repo = KlipRepoImpl(DbFactory.createDb()),
            clipboardManager = KlipboardManager()
        )
    }

    GlobalKeyListener(
        onOutput = coordinator::handleOutput
    )

    KlipListener(
        clipboardManager = coordinator.clipboardManager,
        onOutput = coordinator::handleOutput
    )

    KlipTray(coordinator)

    MaterialTheme(
        colors = colorScheme,
        shapes = MaterialTheme.shapes.copy(large = RoundedCornerShape(12.dp)),
        typography = Typography(defaultFontFamily = sonosFamily)
    ) {
        App(coordinator)
    }
}