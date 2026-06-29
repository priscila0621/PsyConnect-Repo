package ni.edu.uam.psyconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.model.LoginRequest
import ni.edu.uam.psyconnect.network.RetrofitClient

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(identifier, password))
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null && authResponse.success) {
                        val userId = authResponse.userId ?: -1
                        
                        // Validar si el usuario ya tiene perfil completo (foto y descripción)
                        val userResponse = RetrofitClient.apiService.getUserById(userId)
                        val isProfileComplete = if (userResponse.isSuccessful) {
                            val user = userResponse.body()
                            !user?.profileImage.isNullOrEmpty() && !user?.description.isNullOrEmpty()
                        } else {
                            false
                        }

                        _loginState.value = LoginState.Success(
                            userId = userId,
                            message = authResponse.message ?: "Inicio de sesión exitoso",
                            identifier = identifier,
                            isProfileComplete = isProfileComplete
                        )
                    } else {
                        _loginState.value = LoginState.Error(authResponse?.message ?: "Credenciales incorrectas")
                    }
                } else {
                    _loginState.value = LoginState.Error("Error en el servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(
        val userId: Long, 
        val message: String, 
        val identifier: String,
        val isProfileComplete: Boolean = false
    ) : LoginState()
    data class Error(val message: String) : LoginState()
}
