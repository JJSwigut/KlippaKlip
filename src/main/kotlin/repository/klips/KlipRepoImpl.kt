package repository.klips

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jjswigut.klippaklip.Database
import com.jjswigut.klippaklip.database.HistoryEntity
import com.jjswigut.klippaklip.database.KlipEntity
import data.models.HistoryKlip
import data.models.Klip
import data.models.Klippable
import data.models.toKlips
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import repository.klips.KlipRepo

class KlipRepoImpl(private val db: Database) : KlipRepo {

    override suspend fun getAllKlips(): Flow<List<KlipEntity>> =
        db.klipEntityQueries.selectAllKlipEntities().asFlow().mapToList(Dispatchers.IO)

    override suspend fun searchKlips(query: String): Flow<List<KlipEntity>> =
        db.klipEntityQueries.searchKlipEntities(query, query).asFlow().mapToList(Dispatchers.IO)

    override val historyKlips: Flow<List<HistoryEntity>> =
        db.historyQueries.selectAllHistory().asFlow().mapToList(Dispatchers.IO)

    override suspend fun upsertKlip(
        id: Long?,
        title: String?,
        itemText: String,
        isPinned: Boolean,
        timeCreated: Long?
    ) {
        runCatching {
            db.klipEntityQueries.upsertKlipEntity(
                id = id,
                title = title,
                itemText = itemText,
                isPinned = if(isPinned) 1 else 0,
                timestamp = timeCreated ?: System.currentTimeMillis()
            )
        }
    }

    override suspend fun deleteKlip(klip: Klippable) {
        runCatching {
            when (klip) {
                is HistoryKlip -> db.historyQueries.deleteHistoryEntity(klip.id)
                is Klip -> db.klipEntityQueries.deleteKlipEntity(klip.id)
            }
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