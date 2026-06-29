package ni.edu.uam.psyconnect.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalDatabase
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalRepository
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.MoodJournalViewModel

class MoodJournalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MoodJournalDatabase.getDatabase(this)
        val repository = MoodJournalRepository(database.moodJournalDao())

        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MoodJournalViewModel(repository) as T
            }
        })[MoodJournalViewModel::class.java]

        // Obtener el ID del usuario actual de SharedPreferences
        val sharedPreferences = getSharedPreferences("psyconnect", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", -1L)
        
        if (userId != -1L) {
            viewModel.setUserId(userId)
        }

        setContent {
            PsyConnectTheme {
                MoodJournalScreen(
                    viewModel = viewModel,
                    userId = userId,
                    onBack = { finish() },
                    onNavigateToHome = {
                        val intent = Intent(this, Home::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToHistory = {
                        val intent = Intent(this, History::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToProfile = {
                        val intent = Intent(this, Profile::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}
