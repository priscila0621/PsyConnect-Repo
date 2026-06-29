package ni.edu.uam.psyconnect.data.moodjournal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ni.edu.uam.psyconnect.data.model.AchievementEntity
import ni.edu.uam.psyconnect.data.model.TestResultEntity

@Database(
    entities = [MoodJournalEntry::class, TestResultEntity::class, AchievementEntity::class],
    version = 5,
    exportSchema = false
)
abstract class MoodJournalDatabase : RoomDatabase() {

    abstract fun moodJournalDao(): MoodJournalDao
    abstract fun testResultDao(): TestResultDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: MoodJournalDatabase? = null

        fun getDatabase(context: Context): MoodJournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodJournalDatabase::class.java,
                    "mood_journal_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
