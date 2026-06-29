package ni.edu.uam.psyconnect.data.moodjournal

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodJournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodJournalEntry)

    @Update
    suspend fun update(entry: MoodJournalEntry)

    @Delete
    suspend fun delete(entry: MoodJournalEntry)

    @Query("SELECT * FROM mood_journal WHERE userId = :userId ORDER BY timestamp DESC")
    fun getEntriesByUser(userId: Long): Flow<List<MoodJournalEntry>>

    @Query("SELECT * FROM mood_journal ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<MoodJournalEntry>>
}
