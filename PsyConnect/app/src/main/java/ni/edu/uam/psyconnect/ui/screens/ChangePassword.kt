package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.ChangePasswordViewModel

class ChangePassword : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        
        val userId = getSharedPreferences("psyconnect", MODE_PRIVATE)
            .getLong("userId", -1L)

        setContent {
            PsyConnectTheme {
                val state by viewModel.uiState.collectAsState()

                // Manejo de éxito
                LaunchedEffect(state.isSuccess) {
                    if (state.isSuccess) {
                        Toast.makeText(this@ChangePassword, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                // Manejo de errores
                LaunchedEffect(state.error) {
                    state.error?.let {
                        Toast.makeText(this@ChangePassword, it, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }

                ChangePasswordScreen(
                    state = state,
                    onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
                    onNewPasswordChange = viewModel::onNewPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    onSave = { viewModel.changePassword(userId) },
                    onForgotPassword = {
                        val intent = Intent(this@ChangePassword, ForgotPassword::class.java).apply {
                            putExtra("userId", userId) // Pasamos el ID para recuperación directa
                        }
                        startActivity(intent)
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
