package ni.edu.uam.psyconnect_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY:}")
    private String brevoApiKey;

    @Value("${BREVO_SENDER_EMAIL:psyconnecta@gmail.com}")
    private String senderEmail;

    public void sendVerificationCode(
            String email,
            String code
    ) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            System.err.println("ADVERTENCIA: BREVO_API_KEY no configurada. Código generado para " + email + ": " + code);
            return; // Evita que explote si no se ha configurado la variable
        }

        try {
            String jsonBody = """
                    {
                       "sender":{
                          "name":"PsyConnect",
                          "email":"%s"
                       },
                       "to":[
                          {
                             "email":"%s",
                             "name":"Usuario"
                          }
                       ],
                       "subject":"Código de verificación - PsyConnect",
                       "textContent":"Tu código de verificación es: %s"
                    }
                    """.formatted(senderEmail, email, code);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.err.println("Error enviando correo con Brevo: " + response.body());
            } else {
                System.out.println("Correo enviado exitosamente a " + email);
            }

        } catch (Exception e) {
            System.err.println("Fallo al enviar correo con Brevo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}