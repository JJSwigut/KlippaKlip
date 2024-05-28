import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.models.HistoryKlip
import data.models.Klip
import data.persistence.DbFactory
import feature.AppCoordinator
import feature.mainscreen.MainScreen
import feature.tray.KlipTray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import repository.KlipRepoImpl
import ui.CreateWindow
import ui.MainView
import ui.theme.colorScheme
import ui.theme.sonosFamily
import utils.GlobalKeyListener
import utils.KeyOutput

@Composable
@Preview
fun App(
    coordinator: AppCoordinator
) {
    val dispatcher = Dispatchers.IO

    Navigator(MainScreen(
        coordinator,
        dispatcher,
        shouldShow = false
    ))

    val clipboardManager = LocalClipboardManager.current
    val navigator = LocalNavigator.currentOrThrow

    LaunchedEffect(Unit){
        coordinator.setNavigator(navigator)
    }

    var showCreateDialog by remember { mutableStateOf(false) }

    val ioDispatcher = CoroutineScope(Dispatchers.IO)


    suspend fun storeAndUpdateHistory(latest: AnnotatedString?){
        latest?.let {
            ioDispatcher.launch {
                coordinator.repo.saveHistoryKlip(it.toString())
                coordinator.repo.getHistoryKlips().fold(
                    onSuccess = { latestHistoryKlips ->
//                        historyKlips = latestHistoryKlips
                    },
                    onFailure = {
                        /* no-op */
                    }
                )
            }
        }
    }

    LaunchedEffect(clipboardManager) {
        while (true) {
            val clipboardText = snapshotFlow { clipboardManager.getText() }
                .filterNotNull()
                .distinctUntilChanged()
                .firstOrNull()
            clipboardText?.let {
                storeAndUpdateHistory(it)
            }
            delay(2000) // Poll every 2 seconds
        }
    }

    if (showCreateDialog) {
        CreateWindow(
            onDismissRequest = {
                showCreateDialog = false
            },
            onCreateClicked = { title, text ->
                showCreateDialog = false
                ioDispatcher.launch {
                    coordinator.repo.addKlip(
                        title = title,
                        klip = text,
                        isPinned = false
                    )
                    coordinator.repo.getKlips().fold(
                        onSuccess = {

                        },
                        onFailure = {
                            /* no-op */
                        }
                    )
                }
            }
        )
    }
}

fun main() = application {

    val coordinator = remember {
        AppCoordinator(
            onExit = { exitApplication() },
            repo = KlipRepoImpl(DbFactory.createDb())
        )
    }

    MaterialTheme(
        colors = colorScheme,
        shapes = MaterialTheme.shapes.copy(large = RoundedCornerShape(12.dp)),
        typography = Typography(defaultFontFamily = sonosFamily)
    ) {
        App(coordinator)
    }

    GlobalKeyListener(
        onShow = {
            coordinator.handleOutput(KeyOutput(it))
        }
    )

    KlipTray(coordinator)
}