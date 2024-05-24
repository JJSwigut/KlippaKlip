package data.models

import androidx.compose.ui.text.AnnotatedString
import com.jjswigut.klippaklip.database.HistoryEntity

data class HistoryKlippable(
    val text: String,
    val timestamp: String,
): Klippable {
    override val klippedText: AnnotatedString
        get() = AnnotatedString(text)
}

private fun HistoryEntity.toKlip(): HistoryKlippable {
    return HistoryKlippable(
        text = this.text,
        timestamp = this.timestamp
    )
}

fun List<HistoryEntity>.toKlips():List<HistoryKlippable> = map { it.toKlip() }