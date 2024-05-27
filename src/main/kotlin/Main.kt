import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import data.models.HistoryKlip
import data.models.Klip
import data.persistence.DbFactory
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import repository.KlipRepoImpl
import ui.CreateWindow
import ui.MainScreen
import ui.theme.colorScheme
import ui.theme.sonosFamily
import utils.GlobalKeyListener

@Composable
@Preview
fun App() {
    val clipboardManager = LocalClipboardManager.current

    val repo = KlipRepoImpl(DbFactory.createDb())

    var showCreateDialog by remember { mutableStateOf(false) }

    val ioDispatcher = CoroutineScope(Dispatchers.IO)

    var klips by remember { mutableStateOf(listOf<Klip>()) }

    var historyKlips by remember { mutableStateOf(listOf<HistoryKlip>()) }

    suspend fun updateKlips() {
        repo.getKlips().fold(
            onSuccess = {
                klips = it
            },
            onFailure = {
                /* no-op */
            }
        )
    }

    suspend fun storeAndUpdateHistory(latest: AnnotatedString?){
        latest?.let {
            ioDispatcher.launch {
                repo.saveHistoryKlip(it.toString())
                repo.getHistoryKlips().fold(
                    onSuccess = { latestHistoryKlips ->
                        historyKlips = latestHistoryKlips
                    },
                    onFailure = {
                        /* no-op */
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        updateKlips()
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

    AnimatedVisibility(visible = true) {
        MainScreen(
            klips = klips,
            historyKlips = historyKlips,
            onCopyClick = { klip ->
                clipboardManager.setText(klip.klippedText)
            },
            onCreateKlip = {
                showCreateDialog = true
            },
            onPinKlip = { klip ->
                ioDispatcher.launch {
                    repo.pinKlip(klip)
                    updateKlips()
                }
            },
            onDeleteKlip = { klip ->
                ioDispatcher.launch {
                    repo.deleteKlip(klip)
                    updateKlips()
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateWindow(
            onDismissRequest = {
                showCreateDialog = false
            },
            onCreateClicked = { title, text ->
                showCreateDialog = false
                ioDispatcher.launch {
                    repo.addKlip(
                        title = title,
                        klip = text,
                        isPinned = false
                    )
                    repo.getKlips().fold(
                        onSuccess = {
                            klips = it
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
    var showDropdown by remember { mutableStateOf(false) }

    val appState = rememberAppState(
        windowState = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition.Aligned(Alignment.TopEnd)
        ),
        trayState = rememberTrayState(),
        onExit = { exitApplication() },
        onShow = {
            showDropdown = !showDropdown
        }
    )

    GlobalKeyListener(
        onShow = { showDropdown = it }
    )

    if (showDropdown) {
        Window(
            onCloseRequest = { showDropdown = false },
            state = appState.windowState,
            undecorated = true,
            transparent = true,
            alwaysOnTop = true,
        ) {
            MaterialTheme(
                colors = colorScheme,
                shapes = MaterialTheme.shapes.copy(large = RoundedCornerShape(12.dp)),
                typography = Typography(defaultFontFamily = sonosFamily)
            ) {
                App()
            }
        }
    }

    Tray(
        icon = painterResource("tray_icon.svg"),
        state = appState.trayState,
        menu = {
            AppMenu(appState)
        }
    )
}

@Composable
private fun MenuScope.AppMenu(state: AppState) {
    Item("Klips", onClick = { state.show() })
    Separator()
    Item("Exit", onClick = { state.exit() })
}