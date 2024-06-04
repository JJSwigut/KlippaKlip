package feature.settings

import base.DesktopViewModel
import data.models.KlipSettings
import feature.Output
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.settings.Prefs

class SettingsViewModel(
    private val prefs: Prefs,
    output: (Output) -> Unit,
): DesktopViewModel<SettingsViewState>(SettingsViewState(prefs.settings.value), output) {

    fun handleAction(action: SettingsAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is SettingsAction.HandleSave -> handleSave(action)
                is SettingsAction.HandleExit -> sendOutput(SettingsOutput.Finished)
            }
        }
    }

    private fun handleSave(action: SettingsAction.HandleSave) {
        prefs.saveSettings(action.settings)
        sendOutput(SettingsOutput.Finished)
    }
}

data class SettingsViewState(
    val settings: KlipSettings
)

sealed interface SettingsAction {
    data class HandleSave(
        val settings: KlipSettings,
    ): SettingsAction

    data object HandleExit: SettingsAction
}

sealed interface SettingsOutput: Output {
    data object Finished: SettingsOutput
}