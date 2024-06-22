package feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import data.models.Klip
import feature.editor.EditorOutput
import feature.editor.EditorScreen
import feature.editor.EditorViewModel
import feature.editor.EditorViewState
import feature.mainscreen.MainOutput
import feature.mainscreen.MainScreen
import feature.mainscreen.MainViewModel
import feature.settings.SettingsOutput
import feature.settings.SettingsScreen
import feature.settings.SettingsViewModel
import feature.tray.MenuOutput
import feature.tray.MenuOutput.Exit
import feature.tray.MenuOutput.ShowCreate
import feature.tray.MenuOutput.ShowKlips
import feature.tray.MenuOutput.ShowSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.klips.KlipRepo
import repository.settings.Prefs
import utils.KeyListenerOutput
import utils.KlipListenerOutput

class AppCoordinator(
    val onExit: () -> Unit,
    val repo: KlipRepo,
    val prefs: Prefs,
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
            is SettingsOutput -> handleSettingsOutput(output)
        }
    }

    private fun handleKeyListenerOutput(output: KeyListenerOutput) {
        when (output) {
            is KeyListenerOutput.ShowKlips -> showKlips = output.shouldShow
            is KeyListenerOutput.ToggleKlips -> showKlips = !showKlips
        }
    }

    private fun handleSettingsOutput(output: SettingsOutput) {
        when(output) {
            is SettingsOutput.Finished -> currentWindow = null
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
            is MainOutput.CreateKlip -> showEditorScreen()
            is MainOutput.EditKlip -> showEditorScreen(output.klip)
            is MainOutput.CopyKlip -> clipboardManager.setText(output.klip.klippedText)
        }
    }

    private fun showEditorScreen(klip: Klip? = null) {
        val initialState = klip?.let {
            EditorViewState(
                windowTitle = "Edit Klip",
                klip = it
            )
        } ?: EditorViewState()

        val viewModel = EditorViewModel(
            ioDispatcher = Dispatchers.IO,
            repo = repo,
            output = ::handleOutput,
            initialState = initialState
        )

        currentWindow = { EditorScreen(viewModel) }
    }

    private fun showSettingsScreen() {
        val viewModel = SettingsViewModel(
            prefs = prefs,
            output = ::handleOutput
        )

        currentWindow = { SettingsScreen(viewModel) }
    }

    private fun handleMenuOutput(output: MenuOutput) {
        when (output) {
            Exit -> onExit()
            ShowCreate -> showEditorScreen()
            ShowKlips -> showKlips = !showKlips
            ShowSettings -> showSettingsScreen()
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
        val viewModel = MainViewModel(
            dispatcher = Dispatchers.IO,
            repo = repo,
            prefs = prefs,
            output = ::handleOutput
        )
        if (showKlips) {
            MainScreen(
                viewModel = viewModel
            )
        }
    }
}

interface Output