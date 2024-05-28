package feature.mainscreen

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.models.HistoryKlip
import data.models.Klip
import data.models.Klippable
import feature.AppCoordinator
import feature.mainscreen.MainAction.HandleCopy
import feature.mainscreen.MainAction.HandleCreateClicked
import feature.mainscreen.MainAction.HandleDelete
import feature.mainscreen.MainAction.HandlePin
import feature.mainscreen.MainAction.Initialize
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    val coordinator: AppCoordinator,
    val dispatcher: CoroutineDispatcher,
) : StateScreenModel<MainViewState>(MainViewState()) {

    init {
        handleAction(Initialize)
    }

        fun handleAction(action: MainAction) {
            screenModelScope.launch(dispatcher){
                when(action){
                    is Initialize -> updateKlips()
                    is HandleCopy -> TODO()
                    is HandleCreateClicked -> TODO()
                    is HandleDelete -> TODO()
                    is HandlePin -> TODO()
                }
            }
        }

    private suspend fun updateKlips() {
        coordinator.repo.getKlips().fold(
            onSuccess = { klips ->
                val (pinnedKlips, regularKlips) = klips.partition { it.isPinned }
                mutableState.update {
                    it.copy(
                        pinnedKlips = pinnedKlips,
                        klips = regularKlips
                    )
                }
            },
            onFailure = {
                /* no-op */
            }
        )
    }


}

data class MainViewState(
    val historyKlips: List<HistoryKlip> = listOf(),
    val pinnedKlips: List<Klip> = listOf(),
    val klips: List<Klip> = listOf(),
)

sealed interface MainAction {
    data object Initialize: MainAction
    data object HandleCreateClicked: MainAction
    data class HandleDelete(val klip: Klip): MainAction
    data class HandleCopy(val klip: Klippable): MainAction
    data class HandlePin(val klip: Klip): MainAction
}