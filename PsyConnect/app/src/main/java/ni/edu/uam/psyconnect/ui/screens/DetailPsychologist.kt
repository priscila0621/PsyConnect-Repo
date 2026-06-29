package ni.edu.uam.psyconnect.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ni.edu.uam.psyconnect.ui.theme.PsyConnectTheme

class DetailPsychologist : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar datos del especialista
        val name = intent.getStringExtra("name") ?: ""
        val specialty = intent.getStringExtra("specialty") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val photo = intent.getStringExtra("photo") ?: ""

        setContent {
            PsyConnectTheme {
                DetailPsychologistScreen(
                    name = name,
                    specialty = specialty,
                    city = city,
                    email = email,
                    phone = phone,
                    description = description,
                    photo = photo,
                    onWhatsappClick = {
                        val url = "https://wa.me/505$phone?text=Hola,%20vi%20tu%20perfil%20en%20PsyConnect%20y%20me%20gustaría%20más%20información."
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
