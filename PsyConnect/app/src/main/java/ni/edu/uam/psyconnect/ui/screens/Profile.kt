package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.ProfileViewModel

class Profile : ComponentActivity() {

    private lateinit var viewModel: ProfileViewModel
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        
        val sharedPreferences = getSharedPreferences("psyconnect", MODE_PRIVATE)
        userId = sharedPreferences.getLong("userId", -1L)

        setContent {
            PsyConnectTheme {
                val user by viewModel.user.collectAsState()
                val age by viewModel.age.collectAsState()

                ProfileScreen(
                    user = user,
                    age = age,
                    onEditProfile = {
                        startActivity(Intent(this, EditProfile::class.java))
                    },
                    onMoodHistory = {
                        startActivity(Intent(this, MoodHistoryActivity::class.java))
                    },
                    onAchievements = {
                        startActivity(Intent(this, AchievementsActivity::class.java))
                    },
                    onSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    onLogout = {
                        sharedPreferences.edit { remove("userId") }
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToHome = {
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    },
                    onNavigateToHistory = {
                        startActivity(Intent(this, History::class.java))
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refrescar datos instantáneamente al volver a la pantalla
        if (userId != -1L) {
            viewModel.loadProfile(userId)
        }
    }
}
