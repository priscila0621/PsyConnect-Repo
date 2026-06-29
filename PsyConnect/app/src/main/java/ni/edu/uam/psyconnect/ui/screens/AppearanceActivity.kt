package ni.edu.uam.psyconnect.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.theme.ThemeSettings

class AppearanceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("psyconnect", MODE_PRIVATE)

        setContent {
            PsyConnectTheme {
                AppearanceScreen(
                    isDarkMode = ThemeSettings.isDarkMode,
                    onDarkModeChange = { checked ->
                        // 1. Guardar preferencia
                        sharedPreferences.edit().putBoolean("darkMode", checked).apply()
                        
                        // 2. Actualizar estado global reactivo
                        ThemeSettings.isDarkMode = checked

                        // 3. Aplicar a nivel sistema para componentes legacy si existen
                        if (checked) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
