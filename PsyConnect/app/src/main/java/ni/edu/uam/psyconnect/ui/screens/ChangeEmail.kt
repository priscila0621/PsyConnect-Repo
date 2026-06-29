package ni.edu.uam.psyconnect.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.ChangeEmailViewModel

class ChangeEmail : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[ChangeEmailViewModel::class.java]
        
        val userId = getSharedPreferences("psyconnect", MODE_PRIVATE)
            .getLong("userId", -1L)

        setContent {
            PsyConnectTheme {
                val state by viewModel.uiState.collectAsState()

                // Manejo de éxito
                LaunchedEffect(state.isSuccess) {
                    if (state.isSuccess) {
                        Toast.makeText(this@ChangeEmail, "Correo actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                // Manejo de errores
                LaunchedEffect(state.error) {
                    state.error?.let {
                        Toast.makeText(this@ChangeEmail, it, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }

                ChangeEmailScreen(
                    state = state,
                    onEmailChange = viewModel::onEmailChange,
                    onCodeChange = viewModel::onCodeChange,
                    onSendCode = viewModel::sendCode,
                    onVerify = { viewModel.verifyAndChangeEmail(userId) },
                    onBack = { finish() }
                )
            }
        }
    }
}
