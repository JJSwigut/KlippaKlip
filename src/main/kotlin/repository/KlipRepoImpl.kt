package repository

import com.jjswigut.klippaklip.Database
import data.models.HistoryKlip
import data.models.Klip
import data.models.toKlips

class KlipRepoImpl(private val db: Database): KlipRepo {

    override suspend fun getKlips(): Result<List<Klip>> {
        return runCatching {
            db.klippedQueries.selectAllKlipEntities().executeAsList()
        }.fold(
            onSuccess = {
                Result.success(it.toKlips())
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

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
                isPinned = if(klip.isPinned) 1 else 0
            )
        }
    }

    override suspend fun getHistoryKlips(): Result<List<HistoryKlip>> {
        return runCatching {
            db.historyQueries.selectAllHistoryEntitys().executeAsList()
        }.fold(
            onSuccess = {
                Result.success(it.toKlips())
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}