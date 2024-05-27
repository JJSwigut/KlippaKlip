package repository

import data.models.HistoryKlip
import data.models.Klip

interface KlipRepo {

    suspend fun getKlips(): Result<List<Klip>>

    suspend fun addKlip(title: String?, klip: String, isPinned: Boolean)

    suspend fun deleteKlip(klip: Klip)

    suspend fun pinKlip(klip: Klip)

    suspend fun saveHistoryKlip(string: String)

    suspend fun getHistoryKlips(): Result<List<HistoryKlip>>
}