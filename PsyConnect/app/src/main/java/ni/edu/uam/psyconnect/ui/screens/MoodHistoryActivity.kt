package ni.edu.uam.psyconnect.ui.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalDatabase
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalRepository
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.MoodHistoryViewModel

class MoodHistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MoodJournalDatabase.getDatabase(this)
        val repository = MoodJournalRepository(database.moodJournalDao())

        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MoodHistoryViewModel(repository) as T
            }
        })[MoodHistoryViewModel::class.java]

        // Obtener el ID del usuario actual de SharedPreferences
        val sharedPreferences = getSharedPreferences("psyconnect", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", -1L)
        
        if (userId != -1L) {
            viewModel.setUserId(userId)
        }

        setContent {
            PsyConnectTheme {
                MoodHistoryScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}
