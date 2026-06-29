package ni.edu.uam.psyconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.model.Mood
import ni.edu.uam.psyconnect.data.model.Psychologist
import ni.edu.uam.psyconnect.network.RetrofitClient

class HomeViewModel : ViewModel() {

    private val _userName = MutableStateFlow("Usuario")
    val userName: StateFlow<String> = _userName

    private val _psychologists = MutableStateFlow<List<Psychologist>>(emptyList())
    val psychologists: StateFlow<List<Psychologist>> = _psychologists

    private val _showMoodDialog = MutableStateFlow(false)
    val showMoodDialog: StateFlow<Boolean> = _showMoodDialog

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadUserData(userId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getUserById(userId)
                if (response.isSuccessful) {
                    _userName.value = response.body()?.name ?: "Usuario"
                }
                
                // Desactivado el diálogo automático para no molestar al usuario
                /*
                val moodResponse = RetrofitClient.apiService.hasMoodToday(userId)
                if (moodResponse.isSuccessful && moodResponse.body() == false) {
                    _showMoodDialog.value = true
                }
                */
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadPsychologists() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getPsychologists()
                if (response.isSuccessful) {
                    _psychologists.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveMood(userId: Long, moodName: String) {
        viewModelScope.launch {
            try {
                val dateStr = java.text.SimpleDateFormat("EEEE, d MMMM", java.util.Locale("es")).format(java.util.Date()).replaceFirstChar { it.uppercase() }
                RetrofitClient.apiService.saveMood(
                    ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry(
                        userId = userId,
                        mood = moodName,
                        reflection = "",
                        date = dateStr,
                        timestamp = System.currentTimeMillis(),
                        activities = ""
                    )
                )
                _showMoodDialog.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun dismissMoodDialog() {
        _showMoodDialog.value = false
    }
}
