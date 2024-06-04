package repository.klips

import com.jjswigut.klippaklip.database.HistoryEntity
import com.jjswigut.klippaklip.database.KlipEntity
import data.models.HistoryKlip
import data.models.Klip
import kotlinx.coroutines.flow.Flow

interface KlipRepo {

    val klips: Flow<List<KlipEntity>>
    val historyKlips: Flow<List<HistoryEntity>>

    suspend fun addKlip(title: String?, klip: String, isPinned: Boolean)

    suspend fun deleteKlip(klip: Klip)

    suspend fun pinKlip(klip: Klip)

    suspend fun saveHistoryKlip(string: String)

    suspend fun deleteAllHistory()
}