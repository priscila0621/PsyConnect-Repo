package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.HomeViewModel

class Home : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val sharedPreferences = getSharedPreferences("psyconnect", MODE_PRIVATE)
        userId = sharedPreferences.getLong("userId", -1L)

        setContent {
            PsyConnectTheme {
                val userName by viewModel.userName.collectAsState()
                val psychologists by viewModel.psychologists.collectAsState()
                val showMoodDialog by viewModel.showMoodDialog.collectAsState()

                HomeScreen(
                    userName = userName,
                    psychologists = psychologists,
                    showMoodDialog = showMoodDialog,
                    onMoodSelected = { moodName ->
                        if (userId != -1L) {
                            viewModel.saveMood(userId, moodName)
                        }
                    },
                    onDismissMoodDialog = { viewModel.dismissMoodDialog() },
                    onTestClick = { category ->
                        val intent = Intent(this, DynamicTestActivity::class.java)
                        intent.putExtra("category", category)
                        startActivity(intent)
                    },
                    onPsychologistClick = { psychologist ->
                        val intent = Intent(this, DetailPsychologist::class.java).apply {
                            putExtra("name", psychologist.name)
                            putExtra("specialty", psychologist.specialty)
                            putExtra("city", psychologist.city)
                            putExtra("email", psychologist.email)
                            putExtra("description", psychologist.description)
                            putExtra("phone", psychologist.phone)
                            putExtra("photo", psychologist.photo)
                        }
                        startActivity(intent)
                    },
                    onNavigateToHistory = {
                        startActivity(Intent(this, History::class.java))
                    },
                    onNavigateToProfile = {
                        startActivity(Intent(this, Profile::class.java))
                    },
                    onNavigateToMoodJournal = {
                        startActivity(Intent(this, MoodJournalActivity::class.java))
                    },
                    onNavigateToBreathing = {
                        startActivity(Intent(this, BreathingActivity::class.java))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refrescar datos del usuario cada vez que la pantalla se vuelve visible
        if (userId != -1L) {
            viewModel.loadUserData(userId)
            viewModel.loadPsychologists() // Opcional, pero asegura consistencia
        }
    }
}
