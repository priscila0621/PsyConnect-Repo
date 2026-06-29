package ni.edu.uam.psyconnect.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme

class BreathingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PsyConnectTheme {
                BreathingScreen(onBack = { finish() })
            }
        }
    }
}
