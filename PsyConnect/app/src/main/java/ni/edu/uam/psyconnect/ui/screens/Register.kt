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
import ni.edu.uam.psyconnect.ui.viewmodel.RegisterViewModel

class Register : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setContent {
            PsyConnectTheme {
                val state by viewModel.uiState.collectAsState()

                // Manejo de eventos (Navegación y Errores)
                LaunchedEffect(state.isRegistered) {
                    if (state.isRegistered) {
                        Toast.makeText(this@Register, "¡Bienvenido! Usuario registrado.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }

                LaunchedEffect(state.error) {
                    state.error?.let {
                        Toast.makeText(this@Register, it, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }

                RegisterScreen(
                    state = state,
                    onNameChange = viewModel::onNameChange,
                    onUsernameChange = viewModel::onUsernameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onBirthdateChange = viewModel::onBirthdateChange,
                    onTermsChange = viewModel::onTermsChange,
                    onVerificationCodeChange = viewModel::onVerificationCodeChange,
                    onSendCode = viewModel::sendCode,
                    onVerifyCode = viewModel::verifyCode,
                    onRegister = viewModel::register,
                    onBack = { finish() }
                )
            }
        }
    }
}
