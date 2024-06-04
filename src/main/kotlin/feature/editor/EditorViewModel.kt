package feature.editor

import base.DesktopViewModel
import data.models.Klip
import feature.Output
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import repository.klips.KlipRepo
import repository.klips.KlipRepoImpl

class EditorViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val repo: KlipRepo,
    private val initialState: EditorViewState,
    output: (Output) -> Unit,
): DesktopViewModel<EditorViewState>(initialState, output){

    fun handleAction(action: EditorAction){
        viewModelScope.launch(ioDispatcher) {
            when (action) {
                is EditorAction.HandleSave -> handleSave(action)
                is EditorAction.HandleExit -> sendOutput(EditorOutput.Finished)
            }
        }
    }

    private suspend fun handleSave(action: EditorAction.HandleSave) {
        // todo
    }
}

data class EditorViewState(
    val windowTitle: String = "Create",
    val klip: Klip? = null,
)

sealed interface EditorAction {
    data class HandleSave(
        val title: String?,
        val klip: String,
    ): EditorAction

    data object HandleExit: EditorAction
}

sealed interface EditorOutput : Output {
    data object Finished: EditorOutput
}