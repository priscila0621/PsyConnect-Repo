package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme
import ni.edu.uam.psyconnect.ui.viewmodel.CompleteProfileViewModel

class CompleteProfileActivity : ComponentActivity() {

    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[CompleteProfileViewModel::class.java]
        val sharedPreferences = getSharedPreferences("psyconnect", MODE_PRIVATE)
        userId = sharedPreferences.getLong("userId", -1L)

        if (userId == -1L) {
            finish()
            return
        }

        viewModel.setUserId(userId)

        setContent {
            PsyConnectTheme {
                val state by viewModel.uiState.collectAsState()

                LaunchedEffect(state.isSuccess) {
                    if (state.isSuccess) {
                        Toast.makeText(this@CompleteProfileActivity, "¡Perfil actualizado!", Toast.LENGTH_SHORT).show()
                        markProfileAsCompleted()
                        navigateToHome()
                    }
                }

                LaunchedEffect(state.error) {
                    state.error?.let {
                        Toast.makeText(this@CompleteProfileActivity, it, Toast.LENGTH_LONG).show()
                    }
                }

                CompleteProfileScreen(
                    onImageSelected = { viewModel.onImageSelected(it) },
                    onDescriptionChange = { viewModel.onDescriptionChange(it) },
                    description = state.description,
                    selectedImageUri = state.selectedImageUri,
                    isLoading = state.isLoading,
                    onSave = { viewModel.saveProfile(this@CompleteProfileActivity) },
                    onSkip = { 
                        markProfileAsCompleted()
                        navigateToHome() 
                    }
                )
            }
        }
    }

    private fun markProfileAsCompleted() {
        getSharedPreferences("psyconnect", MODE_PRIVATE)
            .edit()
            .putBoolean("profile_setup_$userId", true)
            .apply()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}
