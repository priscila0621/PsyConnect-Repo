package ni.edu.uam.psyconnect.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme

class ResultDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar datos del resultado
        val category = intent.getStringExtra("category") ?: "WELLNESS"
        val percentage = intent.getIntExtra("percentage", 0)
        val trend = intent.getIntExtra("trend", 0)
        val date = intent.getStringExtra("date") ?: "-"

        setContent {
            PsyConnectTheme {
                ResultDetailScreen(
                    category = category,
                    percentage = percentage,
                    trend = trend,
                    date = date,
                    onBack = { finish() }
                )
            }
        }
    }
}
