package repository.klips

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jjswigut.klippaklip.Database
import com.jjswigut.klippaklip.database.HistoryEntity
import com.jjswigut.klippaklip.database.KlipEntity
import data.models.Klip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class KlipRepoImpl(private val db: Database): KlipRepo {

    override val historyKlips: Flow<List<HistoryEntity>> = db.historyQueries.selectAllHistory().asFlow().mapToList(Dispatchers.IO)
    override val klips: Flow<List<KlipEntity>> = db.klippedQueries.selectAllKlipEntities().asFlow().mapToList(Dispatchers.IO)

    override suspend fun addKlip(title: String?, klip: String, isPinned: Boolean) {
        runCatching {
            db.klippedQueries.insertKlipEntity(
                title = title,
                itemText = klip,
                isPinned = if(isPinned) 1 else 0
            )
        }
    }

    override suspend fun deleteKlip(klip: Klip) {
        runCatching {
            db.klippedQueries.deleteKlipEntity(klip.id)
        }
    }

    override suspend fun pinKlip(klip: Klip) {
        runCatching {
            db.klippedQueries.updateKlipEntityIsPinned(
                id = klip.id,
                isPinned = if (klip.isPinned) 0 else 1
            )
        }
    }

    override suspend fun saveHistoryKlip(string: String) {
        runCatching {
            val historyKlips = db.historyQueries.selectAllHistory().executeAsList()
            val nonExistentKlip = historyKlips.none { it.text == string }
            if (nonExistentKlip) {
                db.historyQueries.transaction {

                    db.historyQueries.insertHistoryEntity(string, System.currentTimeMillis())

                    if (historyKlips.count() == 50) {
                        val oldestKlip = historyKlips.minBy { it.timestamp }
                        db.historyQueries.deleteHistoryEntity(oldestKlip.id)
                    }
                }
            }
        }
    }

    override suspend fun deleteAllHistory() {
        runCatching {
            db.historyQueries.deleteAllHistory()
        }
    }
}