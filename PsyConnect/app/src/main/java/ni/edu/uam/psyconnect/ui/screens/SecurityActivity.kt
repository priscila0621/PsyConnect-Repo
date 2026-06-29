package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme

class SecurityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PsyConnectTheme {
                SecurityScreen(
                    onChangePasswordClick = {
                        startActivity(Intent(this, ChangePassword::class.java))
                    },
                    onChangeEmailClick = {
                        startActivity(Intent(this, ChangeEmail::class.java))
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
