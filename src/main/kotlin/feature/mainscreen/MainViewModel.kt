package feature.mainscreen

import base.DesktopViewModel
import data.models.HistoryKlip
import data.models.Klip
import data.models.KlipSettings
import data.models.Klippable
import data.models.SortOrder
import data.models.toKlips
import feature.Output
import feature.mainscreen.MainAction.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import repository.klips.KlipRepo
import repository.settings.Prefs

class MainViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val repo: KlipRepo,
    private val prefs: Prefs,
    output: (Output) -> Unit,
) : DesktopViewModel<MainViewState>(MainViewState(), output) {

    init {
        handleAction(Initialize)
    }

    fun handleAction(action: MainAction) {
        viewModelScope.launch(dispatcher) {
            when (action) {
                is Initialize -> intialize()
                is HandleCopy -> handleCopy(action.klip)
                is HandleCreateClicked -> sendOutput(MainOutput.CreateKlip)
                is HandleDelete -> repo.deleteKlip(action.klip)
                is HandlePin -> handlePin(action.klip)
                is HandleDeleteHistory -> repo.deleteAllHistory()
                is HandleEdit -> sendOutput(MainOutput.EditKlip(action.klip))
            }
        }
    }

    private fun handleCopy(klip: Klippable) {
        sendOutput(MainOutput.CopyKlip(klip))
        viewModelScope.launch(dispatcher) {
            updateState {
                copy(showCopiedMessage = true)
            }
            delay(2000)
            updateState {
                copy(showCopiedMessage = false)
            }
        }
    }

    private suspend fun handlePin(klip: Klip) {
        repo.upsertKlip(
            klip.id,
            klip.title,
            klip.itemText,
            !klip.isPinned,
            klip.timeCreated
        )
    }

    private suspend fun intialize() {
        combine(repo.klips,repo.historyKlips){
            klips, historyKlips ->
            Pair(
                klips.toKlips(),
                historyKlips.toKlips()
            )
        }.collect { (klips, historyKlips) ->
            val (pinnedKlips, regularKlips) = klips.sortKlipsBasedOnPrefs()
            updateState {
                copy(
                    pinnedKlips = pinnedKlips,
                    klips = regularKlips,
                    historyKlips = historyKlips
                )
            }
        }
    }

    private fun List<Klip>.sortKlipsBasedOnPrefs(): SortedKlips {
        val (pinnedKlips, regularKlips) = partition { it.isPinned }

        fun List<Klip>.sortKlips(order: SortOrder): List<Klip> {
            return when (order) {
                SortOrder.Oldest -> sortedBy { it.timeCreated }
                SortOrder.Recent -> sortedByDescending { it.timeCreated }
                SortOrder.Alphabetical -> sortedBy { it.title ?: it.itemText }
            }
        }
        with(prefs.settings.value) {
            return SortedKlips(
                pinnedKlips.sortKlips(pinnedSortOrder),
                regularKlips.sortKlips(klipSortOrder)
            )
        }
    }
}

private data class SortedKlips(
    val pinnedKlips: List<Klip>,
    val klips: List<Klip>
)

data class MainViewState(
    val historyKlips: List<HistoryKlip> = listOf(),
    val pinnedKlips: List<Klip> = listOf(),
    val klips: List<Klip> = listOf(),
    val showCopiedMessage: Boolean = false,
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