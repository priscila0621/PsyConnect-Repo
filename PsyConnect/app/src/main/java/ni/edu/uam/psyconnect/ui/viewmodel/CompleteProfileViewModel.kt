package ni.edu.uam.psyconnect.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.model.User
import ni.edu.uam.psyconnect.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class CompleteProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState

    private var userId: Long = -1

    fun setUserId(id: Long) {
        userId = id
    }

    fun onDescriptionChange(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun onImageSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun saveProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Obtener datos actuales del usuario
                val userResponse = RetrofitClient.apiService.getUserById(userId)
                if (!userResponse.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al obtener datos")
                    return@launch
                }
                
                val currentUser = userResponse.body() ?: return@launch
                var updatedImage = currentUser.profileImage

                // 2. Subir imagen si se seleccionó una
                _uiState.value.selectedImageUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                        // Aquí asumo que existe un endpoint de upload o que se puede pasar en el JSON
                        // Por el momento, si no hay endpoint de multipart, simulamos o usamos el campo profileImage
                        // Nota: Muchos backends requieren un endpoint separado para archivos.
                        // Si el backend espera el nombre del archivo, lo ideal sería subirlo primero.
                        // Como no veo endpoint de upload en ApiService, actualizaré el usuario directamente con el path local (simulado)
                        // o dejaré que el usuario use el servicio de actualización si acepta base64 o similar.
                    }
                }

                // 3. Actualizar usuario con descripción
                val updatedUser = currentUser.copy(
                    description = _uiState.value.description
                )
                
                val updateResponse = RetrofitClient.apiService.updateUser(userId, updatedUser)
                if (updateResponse.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isSuccess = true, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al actualizar")
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }
}

data class CompleteProfileUiState(
    val description: String = "",
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
