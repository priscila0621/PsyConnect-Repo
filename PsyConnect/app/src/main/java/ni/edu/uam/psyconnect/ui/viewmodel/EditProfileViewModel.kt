package ni.edu.uam.psyconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.model.User
import ni.edu.uam.psyconnect.network.RetrofitClient

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var usernameOriginal = ""
    private var checkUsernameJob: Job? = null

    fun loadUserData(userId: Long) {
        if (userId == -1L) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = RetrofitClient.apiService.getUserById(userId)
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        usernameOriginal = user.username
                        _uiState.value = _uiState.value.copy(
                            name = user.name,
                            username = user.username,
                            description = user.description ?: "",
                            birthdate = user.birthdate,
                            profileImage = user.profileImage,
                            email = user.email,
                            isLoading = false,
                            initialUser = user
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
        validateForm()
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
        if (newUsername == usernameOriginal) {
            _uiState.value = _uiState.value.copy(isUsernameAvailable = true)
            validateForm()
            return
        }
        
        // Debounce para no saturar la API
        checkUsernameJob?.cancel()
        checkUsernameJob = viewModelScope.launch {
            delay(500)
            try {
                val response = RetrofitClient.apiService.existsUsername(newUsername)
                _uiState.value = _uiState.value.copy(isUsernameAvailable = response.body() == false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isUsernameAvailable = null)
            }
            validateForm()
        }
    }

    fun onDescriptionChange(newDesc: String) {
        if (newDesc.length <= 100) {
            _uiState.value = _uiState.value.copy(description = newDesc)
            validateForm()
        }
    }

    fun onBirthdateChange(newDate: String) {
        _uiState.value = _uiState.value.copy(birthdate = newDate)
        validateForm()
    }

    fun onImageSelected(uri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                // Compress image before Base64 encoding to prevent huge payloads
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (bitmap != null) {
                    val outputStream = java.io.ByteArrayOutputStream()
                    // Compress to JPEG 70%
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                    val bytes = outputStream.toByteArray()
                    
                    val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                    _uiState.value = _uiState.value.copy(profileImage = base64)
                    validateForm()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validateForm() {
        val state = _uiState.value
        val hasChanges = state.name != state.initialUser?.name ||
                state.username != state.initialUser?.username ||
                state.description != (state.initialUser?.description ?: "") ||
                state.birthdate != state.initialUser?.birthdate ||
                state.profileImage != state.initialUser?.profileImage

        val isValid = state.name.length >= 3 && (state.isUsernameAvailable == true)
        
        _uiState.value = _uiState.value.copy(
            isSaveEnabled = hasChanges && isValid
        )
    }

    fun saveChanges(userId: Long) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val updatedUser = User(
                    id = userId,
                    name = state.name,
                    username = state.username,
                    email = state.email,
                    password = "", // No se cambia aquí
                    birthdate = state.birthdate,
                    description = state.description,
                    profileImage = state.profileImage
                )
                val response = RetrofitClient.apiService.updateUser(userId, updatedUser)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isUpdateSuccess = true, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        error = response.errorBody()?.string() ?: "Error al actualizar"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class EditProfileUiState(
    val name: String = "",
    val username: String = "",
    val description: String = "",
    val birthdate: String = "",
    val profileImage: String? = null,
    val email: String = "",
    val isLoading: Boolean = false,
    val isUsernameAvailable: Boolean? = true,
    val isSaveEnabled: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val error: String? = null,
    val initialUser: User? = null
)
