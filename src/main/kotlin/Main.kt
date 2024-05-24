import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.models.HistoryKlip
import data.models.Klip
import data.persistence.DbFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.KlipRepoImpl
import ui.CreateDialog
import ui.MainScreen

@Composable
@Preview
fun App() {
    val clipboardManager  = LocalClipboardManager.current

    val repo = KlipRepoImpl(DbFactory.createDb())

    var showCreateDialog by remember { mutableStateOf(false) }

    val ioDispatcher = CoroutineScope(Dispatchers.IO)

    var klips by remember { mutableStateOf(listOf<Klip>())}

    var historyKlips by remember { mutableStateOf(listOf<HistoryKlip>())}

    LaunchedEffect(Unit){
        repo.getKlips().fold(
            onSuccess = {
                klips =it
            },
            onFailure = {

            }
        )
    }

    MainScreen(
        pinnedKlips = klips,
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
            }
        }
    )

    if(showCreateDialog){
        CreateDialog(
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
    Window(
        state = rememberWindowState(size = DpSize(600.dp, 300.dp)),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

fun generatePinnedKlips(size: Int): List<Klip> {
    return List(size) { index ->
        Klip(
            id = index.toLong(),
            title = "Pinned Title $index",
            itemText = "Pinned Text $index",
            isPinned = false
        )
    }
}

fun generateHistoryKlips(size: Int): List<HistoryKlip> {
    return List(size) { index ->
        HistoryKlip(
            text = "History Text $index",
            timestamp = "2024-05-24T12:${34 + index}:56"
        )
    }
}