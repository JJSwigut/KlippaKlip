package data.models

import androidx.compose.ui.Alignment
import java.awt.event.KeyEvent
import kotlinx.serialization.Serializable
import utils.KeyEventMapper

@Serializable
data class KlipSettings(
    val shouldTrackHistory: Boolean = true,
    val klipSortOrder: SortOrder = SortOrder.Recent,
    val pinnedSortOrder: SortOrder = SortOrder.Recent,
    val openHotKeys: List<Int> = listOf(KeyEvent.VK_CONTROL, KeyEvent.VK_A),
    val closeHotKeys: List<Int> = listOf(KeyEvent.VK_ESCAPE),
    val position: Alignment = Alignment.TopEnd,
)

val KlipSettings.listenerOpenKeys
    get() = openHotKeys.mapNotNull { KeyEventMapper.awtToNative(it) }

val KlipSettings.listenerCloseKeys
    get() = closeHotKeys.mapNotNull { KeyEventMapper.awtToNative(it) }


enum class SortOrder {
    Recent,
    Alphabetical,
    Oldest,
}
