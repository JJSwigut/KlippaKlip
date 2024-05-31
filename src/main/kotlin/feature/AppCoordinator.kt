package feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import feature.editor.EditorOutput
import feature.editor.EditorScreen
import feature.editor.EditorViewModel
import feature.editor.EditorViewState
import feature.mainscreen.MainOutput
import feature.mainscreen.MainScreen
import feature.mainscreen.MainViewModel
import feature.tray.MenuOutput
import feature.tray.MenuOutput.Exit
import feature.tray.MenuOutput.ShowCreate
import feature.tray.MenuOutput.ShowKlips
import feature.tray.MenuOutput.ShowSettings
import java.awt.GraphicsEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.KlipRepoImpl
import utils.KeyListenerOutput
import utils.KlipListenerOutput

class AppCoordinator(
    val onExit: () -> Unit,
    val repo: KlipRepoImpl,
    val clipboardManager: ClipboardManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var currentWindow by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var showKlips by mutableStateOf(false)

    fun handleOutput(output: Output) {
        when (output) {
            is EditorOutput -> handleEditorOutput(output)
            is MenuOutput -> handleMenuOutput(output)
            is MainOutput -> handleMainOutput(output)
            is KlipListenerOutput -> handleKlipListenerOutput(output)
            is KeyListenerOutput -> handleKeyListenerOutput(output)
        }
    }

    private fun handleKeyListenerOutput(output: KeyListenerOutput) {
        when (output) {
            is KeyListenerOutput.ShowKlips -> showKlips = output.shouldShow
        }
    }

    private fun handleKlipListenerOutput(output: KlipListenerOutput) {
        when (output) {
            is KlipListenerOutput.SaveHistoryKlip -> saveHistoryKlip(output.text)
        }
    }

    private fun handleEditorOutput(output: EditorOutput) {
        when (output) {
            is EditorOutput.Finished -> currentWindow = null
        }
    }

    private fun handleMainOutput(output: MainOutput) {
        when (output) {
            is MainOutput.CreateKlip -> {
                val viewModel = EditorViewModel(
                    ioDispatcher = Dispatchers.IO,
                    repo = repo,
                    coordinator = this,
                    initialState = EditorViewState()
                )
                currentWindow = { EditorScreen(viewModel) }
            }
            is MainOutput.EditKlip -> {
                val viewModel = EditorViewModel(
                    ioDispatcher = Dispatchers.IO,
                    repo = repo,
                    coordinator = this,
                    initialState = EditorViewState(
                        windowTitle = "Edit Klip",
                        klip = output.klip
                    )
                )
                currentWindow = { EditorScreen(viewModel) }
            }
            is MainOutput.CopyKlip -> clipboardManager.setText(output.klip.klippedText)
        }
    }

    private fun handleMenuOutput(output: MenuOutput) {
        when (output) {
            Exit -> onExit()
            ShowCreate -> {
                //todo
            }
            ShowKlips -> showKlips = true
            ShowSettings -> {
                //todo
            }
        }
    }

    private fun saveHistoryKlip(latest: AnnotatedString) {
        scope.launch {
            repo.saveHistoryKlip(latest.toString())
        }
    }

    @Composable
    fun WindowContent() {
        currentWindow?.invoke()
    }

    @Composable
    fun KlipsContent() {
        if (showKlips) {
            MainScreen(
                viewModel = MainViewModel(
                    dispatcher = Dispatchers.IO,
                    repo = repo,
                    output = ::handleOutput
                )
            )
        }
    }
}

interface Output