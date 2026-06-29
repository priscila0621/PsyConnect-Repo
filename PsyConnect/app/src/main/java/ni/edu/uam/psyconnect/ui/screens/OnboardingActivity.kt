package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme

class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs: SharedPreferences = getSharedPreferences("psyconnect", MODE_PRIVATE)

        // Si ya completó el onboarding, saltar al Login
        if (prefs.getBoolean("onboarding_completed", false)) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        setContent {
            PsyConnectTheme {
                OnboardingScreen(
                    onFinished = {
                        prefs.edit().putBoolean("onboarding_completed", true).apply()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
