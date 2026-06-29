package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PsyConnectTheme {
                SettingsScreen(
                    onAppearanceClick = {
                        startActivity(Intent(this, AppearanceActivity::class.java))
                    },
                    onSecurityClick = {
                        startActivity(Intent(this, SecurityActivity::class.java))
                    },
                    onAboutClick = {
                        startActivity(Intent(this, AboutActivity::class.java))
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
