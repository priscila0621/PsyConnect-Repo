package ni.edu.uam.psyconnect.ui.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.model.RecoveryCodeRequest
import ni.edu.uam.psyconnect.network.RetrofitClient
import java.util.Locale

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(code = code)
    }

    fun loadUserEmail(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = RetrofitClient.apiService.getUserById(userId)
                if (response.isSuccessful) {
                    val email = response.body()?.email ?: ""
                    _uiState.value = _uiState.value.copy(
                        email = email,
                        maskedEmail = maskEmail(email),
                        isDirectMode = true,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "No se pudo cargar el correo del usuario")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error de conexión")
            }
        }
    }

    private fun maskEmail(email: String): String {
        if (!email.contains("@")) return email
        val parts = email.split("@")
        val name = parts[0]
        val domain = parts[1]
        
        return if (name.length > 2) {
            name.substring(0, 2) + "***" + name.substring(name.length - 1) + "@" + domain
        } else {
            "***@" + domain
        }
    }

    fun sendRecoveryCode() {
        val email = _uiState.value.email
        if (email.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Verificar si el correo existe (solo si no es direct mode, por seguridad)
                if (!_uiState.value.isDirectMode) {
                    val existsResponse = RetrofitClient.apiService.existsEmail(email)
                    if (existsResponse.body() != true) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No existe ninguna cuenta asociada a este correo"
                        )
                        return@launch
                    }
                }

                // Enviar el código
                val response = RetrofitClient.apiService.sendVerificationCode(email)
                if (response.isSuccessful) {
                    startTimer()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isCodeSent = true,
                        message = "Se ha enviado un código de verificación."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "No se pudo enviar el código")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error de conexión")
            }
        }
    }

    fun verifyCode() {
        val email = _uiState.value.email
        val code = _uiState.value.code
        if (email.isBlank() || code.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = RetrofitClient.apiService.validateRecoveryCode(RecoveryCodeRequest(email, code))
                if (response.isSuccessful && response.body() == true) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isCodeVerified = true)
                    countDownTimer?.cancel()
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "El código es incorrecto o ha expirado.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al validar el código")
            }
        }
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                _uiState.value = _uiState.value.copy(
                    timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                    isTimerActive = true
                )
            }
            override fun onFinish() {
                _uiState.value = _uiState.value.copy(timerText = "00:00", isTimerActive = false)
            }
        }.start()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}

data class ForgotPasswordUiState(
    val email: String = "",
    val maskedEmail: String = "",
    val isDirectMode: Boolean = false, // True si ya sabemos qué correo es (desde configuración)
    val code: String = "",
    val isLoading: Boolean = false,
    val isCodeSent: Boolean = false,
    val isCodeVerified: Boolean = false,
    val isTimerActive: Boolean = false,
    val timerText: String = "02:00",
    val message: String? = null,
    val error: String? = null
)
