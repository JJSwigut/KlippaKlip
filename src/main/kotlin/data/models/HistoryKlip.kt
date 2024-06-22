package data.models

import androidx.compose.ui.text.AnnotatedString
import com.jjswigut.klippaklip.database.HistoryEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HistoryKlip(
    val id: Long,
    val text: String,
    val timeCopied: String,
): Klippable {
    override val klippedText: AnnotatedString
        get() = AnnotatedString(text)
}

private val historyTimeFormatter = DateTimeFormatter.ofPattern("MMM d, h:mma")

private fun HistoryEntity.toKlip(): HistoryKlip {
    val timeCopied =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this.timestamp), ZoneId.systemDefault())
            .format(historyTimeFormatter)

    return HistoryKlip(
        id = id,
        text = this.text,
        timeCopied = timeCopied
    )
}

fun List<HistoryEntity>.toKlips():List<HistoryKlip> = map { it.toKlip() }