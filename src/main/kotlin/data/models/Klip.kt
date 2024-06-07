package data.models

import androidx.compose.ui.text.AnnotatedString
import com.jjswigut.klippaklip.database.KlipEntity

data class Klip(
    val id: Long,
    val title: String? = null,
    val itemText: String,
    val isPinned: Boolean,
    val timeCreated: Long,
): Klippable {
    override val klippedText: AnnotatedString
        get() = AnnotatedString(itemText)
}

private fun KlipEntity.toKlip(): Klip {
    return Klip(
        id = id,
        title = title,
        itemText = itemText,
        isPinned = isPinned == 1L,
        timeCreated = timestamp
    )
}

fun List<KlipEntity>.toKlips():List<Klip> = map { it.toKlip() }