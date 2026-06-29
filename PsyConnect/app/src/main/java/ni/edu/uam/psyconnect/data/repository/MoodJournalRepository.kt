package ni.edu.uam.psyconnect.data.moodjournal

import kotlinx.coroutines.flow.Flow

class MoodJournalRepository(
    private val dao: MoodJournalDao
) {
    // Obtenemos las entradas filtradas por el usuario actual
    fun getEntriesByUser(userId: Long): Flow<List<MoodJournalEntry>> = dao.getEntriesByUser(userId)

    // Mantenemos allEntries por si se usa en otros lugares, pero idealmente debería filtrarse
    val allEntries: Flow<List<MoodJournalEntry>> = dao.getAllEntries()

    suspend fun insert(entry: MoodJournalEntry) {
        dao.insert(entry)
    }

    suspend fun update(entry: MoodJournalEntry) {
        dao.update(entry)
    }

    suspend fun delete(entry: MoodJournalEntry) {
        dao.delete(entry)
    }
}
