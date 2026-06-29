package ni.edu.uam.psyconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalRepository

class MoodJournalViewModel(
    private val repository: MoodJournalRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<Long?>(null)
    private val _entries = MutableStateFlow<List<MoodJournalEntry>>(emptyList())
    val entries: StateFlow<List<MoodJournalEntry>> = _entries

    fun setUserId(id: Long) {
        _userId.value = id
        loadEntries()
    }

    private fun loadEntries() {
        val id = _userId.value ?: return
        viewModelScope.launch {
            try {
                val response = ni.edu.uam.psyconnect.network.RetrofitClient.apiService.getMoodHistory(id)
                if (response.isSuccessful) {
                    _entries.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insertEntry(entry: MoodJournalEntry) {
        viewModelScope.launch {
            try {
                val response = ni.edu.uam.psyconnect.network.RetrofitClient.apiService.saveMood(entry)
                if (response.isSuccessful) {
                    loadEntries()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateEntry(entry: MoodJournalEntry) {
        // Actualización optimista: Actualizamos la lista local antes de la llamada de red
        val currentList = _entries.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            currentList[index] = entry
            _entries.value = currentList
        }

        viewModelScope.launch {
            try {
                // Si el backend soporta el campo isFavorite, se guardará. 
                // Usamos saveMood (POST) que suele manejar updates si el ID existe en Spring Boot/JPA
                ni.edu.uam.psyconnect.network.RetrofitClient.apiService.saveMood(entry)
                // No llamamos a loadEntries inmediatamente para evitar parpadeos si la red es lenta
            } catch (e: Exception) {
                // Si falla, revertimos (opcional, por ahora reintentamos cargar)
                loadEntries()
                e.printStackTrace()
            }
        }
    }

    fun deleteEntry(entry: MoodJournalEntry) {
        viewModelScope.launch {
            try {
                val response = ni.edu.uam.psyconnect.network.RetrofitClient.apiService.deleteMood(entry.id.toLong())
                if (response.isSuccessful) {
                    loadEntries()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
