package feature.editor

import base.DesktopViewModel
import data.models.Klip
import feature.AppCoordinator
import feature.Output
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import repository.KlipRepoImpl

class EditorViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val repo: KlipRepoImpl,
    private val coordinator: AppCoordinator,
    private val initialState: EditorViewState
): DesktopViewModel<EditorViewState>(initialState){

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

    override fun sendOutput(output: Output) {
        coordinator.handleOutput(output)
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