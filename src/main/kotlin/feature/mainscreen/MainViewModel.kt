package feature.mainscreen

import base.DesktopViewModel
import data.models.HistoryKlip
import data.models.Klip
import data.models.Klippable
import data.models.SortOrder
import data.models.toKlips
import feature.Output
import feature.mainscreen.MainAction.HandleCopy
import feature.mainscreen.MainAction.HandleCreateClicked
import feature.mainscreen.MainAction.HandleDelete
import feature.mainscreen.MainAction.HandleDeleteHistory
import feature.mainscreen.MainAction.HandleEdit
import feature.mainscreen.MainAction.HandlePin
import feature.mainscreen.MainAction.HandleSearch
import feature.mainscreen.MainAction.Initialize
import feature.mainscreen.MainOutput.CreateKlip
import feature.mainscreen.MainOutput.EditKlip
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import repository.klips.KlipRepo
import repository.settings.Prefs

class MainViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val repo: KlipRepo,
    private val prefs: Prefs,
    output: (Output) -> Unit,
) : DesktopViewModel<MainViewState>(MainViewState(), output) {

    private var currentQuery: String = ""
    private var currentKlipsJob: Job? = null

    init {
        handleAction(Initialize)
    }

    fun handleAction(action: MainAction) {
        viewModelScope.launch(dispatcher) {
            when (action) {
                is Initialize -> initialize()
                is HandleCopy -> handleCopy(action.klip)
                is HandleCreateClicked -> sendOutput(CreateKlip)
                is HandleDelete -> handleDelete(action.klip)
                is HandlePin -> handlePin(action.klip)
                is HandleDeleteHistory -> repo.deleteAllHistory()
                is HandleEdit -> sendOutput(EditKlip(action.klip))
                is HandleSearch -> handleSearch(action.query)
            }
        }
    }

    private fun initialize() {
        observeKlips()
        observeHistoryKlips()
    }

    private fun observeKlips() {
        currentKlipsJob?.cancel()
        currentKlipsJob = viewModelScope.launch(dispatcher) {
            if (currentQuery.isBlank()) {
                repo.getAllKlips()
            } else {
                repo.searchKlips(currentQuery)
            }.collect { klips ->
                val sortedKlips = klips.toKlips().sortKlipsBasedOnPrefs()
                updateState {
                    copy(
                        pinnedKlips = sortedKlips.pinnedKlips,
                        klips = sortedKlips.klips
                    )
                }
            }
        }
    }

    private fun observeHistoryKlips() {
        viewModelScope.launch(dispatcher) {
            repo.historyKlips.collect { historyKlips ->
                updateState {
                    copy(historyKlips = historyKlips.toKlips())
                }
            }
        }
    }

    private suspend fun handleDelete(klip: Klippable) {
        repo.deleteKlip(klip)
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

    private fun handleSearch(query: String) {
        currentQuery = query
        observeKlips()
    }

    private fun handleCopy(klip: Klippable) {
        sendOutput(MainOutput.CopyKlip(klip))
        viewModelScope.launch(dispatcher) {
            updateState { copy(showCopiedMessage = true) }
            delay(2000)
            updateState { copy(showCopiedMessage = false) }
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
    data class HandleDelete(val klip: Klippable): MainAction
    data class HandleCopy(val klip: Klippable): MainAction
    data class HandlePin(val klip: Klip): MainAction
    data class HandleEdit(val klip: Klip): MainAction
    data class HandleSearch(val query: String): MainAction
    data object HandleDeleteHistory : MainAction

}

sealed interface MainOutput : Output {
    data object CreateKlip: MainOutput
    data class EditKlip(val klip: Klip): MainOutput
    data class CopyKlip(val klip: Klippable): MainOutput
}