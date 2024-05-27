package data.models

import androidx.compose.ui.text.AnnotatedString
import com.jjswigut.klippaklip.database.HistoryEntity

data class HistoryKlip(
    val text: String,
    val timestamp: String,
): Klippable {
    override val klippedText: AnnotatedString
        get() = AnnotatedString(text)
}

private fun HistoryEntity.toKlip(): HistoryKlip {
    return HistoryKlip(
        text = this.text,
        timestamp = this.timestamp.toString()
    )
}

fun List<HistoryEntity>.toKlips():List<HistoryKlip> = map { it.toKlip() }