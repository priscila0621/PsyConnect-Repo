package ni.edu.uam.psyconnect.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.EditProfileViewModel

class EditProfile : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        
        val sharedPreferences = getSharedPreferences("psyconnect", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", -1L)

        viewModel.loadUserData(userId)

        setContent {
            PsyConnectTheme {
                val state by viewModel.uiState.collectAsState()

                // Manejo de efectos secundarios
                LaunchedEffect(state.isUpdateSuccess) {
                    if (state.isUpdateSuccess) {
                        Toast.makeText(this@EditProfile, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                        delay(1500)
                        finish()
                    }
                }

                LaunchedEffect(state.error) {
                    state.error?.let {
                        Toast.makeText(this@EditProfile, it, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }

                EditProfileScreen(
                    state = state,
                    onNameChange = viewModel::onNameChange,
                    onUsernameChange = viewModel::onUsernameChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onBirthdateChange = viewModel::onBirthdateChange,
                    onImageSelected = viewModel::onImageSelected,
                    onSave = { viewModel.saveChanges(userId) },
                    onBack = { finish() }
                )
            }
        }
    }
}
