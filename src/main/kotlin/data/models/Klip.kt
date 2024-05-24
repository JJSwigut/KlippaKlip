package data.models

import androidx.compose.ui.text.AnnotatedString
import com.jjswigut.klippaklip.database.PinnedEntity

data class PinnedKlip(
    val id: Long,
    val title: String? = null,
    val itemText: String,
): Klip {
    override val klippedText: AnnotatedString
        get() = AnnotatedString(itemText)
}

private fun PinnedEntity.toKlip(): PinnedKlip {
    return PinnedKlip(
        id = id,
        title = title,
        itemText = itemText
    )
}

fun List<PinnedEntity>.toKlips():List<PinnedKlip> = map { it.toKlip() }