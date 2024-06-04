package repository.settings

import androidx.compose.runtime.mutableStateOf
import data.models.KlipSettings
import java.util.prefs.Preferences
import kotlinx.serialization.json.Json

object Prefs {
    private const val SETTINGS_KEY = "settings"
    private val prefs: Preferences = Preferences.userNodeForPackage(Prefs::class.java)
    private val json = Json

    val settings = mutableStateOf(decodeSettings())

    fun saveSettings(settings: KlipSettings) {
        val jsonString = json.encodeToString(KlipSettings.serializer(), settings)
        prefs.put(SETTINGS_KEY, jsonString)
        this.settings.value = settings
    }

    private fun decodeSettings(): KlipSettings {
        val jsonString = prefs.get(SETTINGS_KEY, null)
        return jsonString?.let {
            json.decodeFromString(KlipSettings.serializer(), it)
        } ?: KlipSettings()
    }
}