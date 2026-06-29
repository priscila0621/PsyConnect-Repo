package ni.edu.uam.psyconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalRepository

class MoodHistoryViewModel(
    private val repository: MoodJournalRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<Long?>(null)
    
    private val _moodEntries = MutableStateFlow<List<MoodJournalEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodJournalEntry>> = _moodEntries

    fun setUserId(id: Long) {
        _userId.value = id
        loadHistory()
    }

    private fun loadHistory() {
        val id = _userId.value ?: return
        viewModelScope.launch {
            try {
                val response = ni.edu.uam.psyconnect.network.RetrofitClient.apiService.getMoodHistory(id)
                if (response.isSuccessful) {
                    _moodEntries.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
