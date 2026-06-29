package ni.edu.uam.psyconnect.data.moodjournal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_journal")
data class MoodJournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Cambiado a Long para consistencia con la API
    val userId: Long,
    val mood: String,
    val reflection: String? = "",
    val date: String,
    val timestamp: Long,
    val activities: String? = "",
    val isFavorite: Boolean = false
)
