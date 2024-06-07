package repository.klips

import com.jjswigut.klippaklip.database.HistoryEntity
import com.jjswigut.klippaklip.database.KlipEntity
import data.models.Klip
import kotlinx.coroutines.flow.Flow

interface KlipRepo {

    val klips: Flow<List<KlipEntity>>
    val historyKlips: Flow<List<HistoryEntity>>

    suspend fun upsertKlip(id: Long?, title: String?, itemText: String, isPinned: Boolean, timeCreated: Long?)

    suspend fun deleteKlip(klip: Klip)

    suspend fun saveHistoryKlip(string: String)

    suspend fun deleteAllHistory()
}