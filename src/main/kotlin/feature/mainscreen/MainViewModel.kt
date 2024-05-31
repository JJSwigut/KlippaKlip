package feature.mainscreen

import base.DesktopViewModel
import com.sun.tools.javac.Main
import data.models.HistoryKlip
import data.models.Klip
import data.models.Klippable
import data.models.toKlips
import feature.AppCoordinator
import feature.Output
import feature.mainscreen.MainAction.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.launch
import repository.KlipRepo
import repository.KlipRepoImpl

class MainViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val repo: KlipRepo,
    private val output: (Output) -> Unit,
) : DesktopViewModel<MainViewState>(MainViewState()) {

    init {
        handleAction(Initialize)
    }

        fun handleAction(action: MainAction) {
            viewModelScope.launch(dispatcher){
                when(action){
                    is Initialize -> intialize()
                    is HandleCopy -> sendOutput(MainOutput.CopyKlip(action.klip))
                    is HandleCreateClicked -> sendOutput(MainOutput.CreateKlip)
                    is HandleDelete -> repo.deleteKlip(action.klip)
                    is HandlePin -> repo.pinKlip(action.klip)
                    is HandleDeleteHistory -> repo.deleteAllHistory()
                    is HandleEdit -> sendOutput(MainOutput.EditKlip(action.klip))
                }
            }
        }

    private suspend fun intialize() {
        combine(repo.klips,repo.historyKlips){
            klips, historyKlips ->
            Pair(
                klips.toKlips(),
                historyKlips.toKlips()
            )
        }.collect { (klips, historyKlips) ->
            val (pinnedKlips, regularKlips) = klips.partition { it.isPinned }
            updateState {
                copy(
                    pinnedKlips = pinnedKlips,
                    klips = regularKlips,
                    historyKlips = historyKlips
                )
            }
        }
    }

    override fun sendOutput(output: Output) {
        output(output)
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
    data class HandleEdit(val klip: Klip): MainAction
    data object HandleDeleteHistory : MainAction

}

sealed interface MainOutput : Output {
    data object CreateKlip: MainOutput
    data class EditKlip(val klip: Klip): MainOutput
    data class CopyKlip(val klip: Klippable): MainOutput
}