package ni.edu.uam.psyconnect.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.psyconnect.ui.viewmodel.RegisterUiState
import ni.edu.uam.psyconnect.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onBirthdateChange: (String) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onVerifyCode: () -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.YEAR, -10)
                    return utcTimeMillis <= calendar.timeInMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val date = sdf.format(Date(it))
                        onBirthdateChange(date)
                    }
                    showDatePicker.value = false
                }) {
                    Text("Confirmar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(onDismiss = { showTermsDialog = false })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Crear Cuenta",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Atrás",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(32.dp)) }

            item {
                RegisterTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = "Nombre Completo",
                    icon = Icons.Default.Person
                )
            }

            item {
                Column {
                    RegisterTextField(
                        value = state.username,
                        onValueChange = onUsernameChange,
                        label = "Nombre de Usuario",
                        icon = Icons.Default.AccountCircle
                    )
                    if (state.username.isNotEmpty()) {
                        state.isUsernameAvailable?.let { available ->
                            Text(
                                text = if (available) "✅ Usuario disponible" else "❌ Usuario ocupado",
                                color = if (available) Color(0xFF4CAF50) else Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RegisterTextField(
                            value = state.email,
                            onValueChange = onEmailChange,
                            label = "Correo Electrónico",
                            icon = Icons.Default.Email,
                            enabled = !state.isEmailVerified,
                            modifier = Modifier.weight(1f)
                        )
                        if (!state.isEmailVerified && state.isEmailAvailable == true) {
                            TextButton(onClick = onSendCode, enabled = !state.isLoading && !state.isTimerActive) {
                                Text(
                                    if (state.isTimerActive) state.timerText else "Enviar",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    if (state.email.isNotEmpty()) {
                        state.isEmailAvailable?.let { available ->
                            Text(
                                text = if (available) "✅ Correo disponible" else "❌ Correo ya registrado",
                                color = if (available) Color(0xFF4CAF50) else Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            if (state.codeSent && !state.isEmailVerified) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RegisterTextField(
                            value = state.verificationCode,
                            onValueChange = onVerificationCodeChange,
                            label = "Código",
                            icon = Icons.Default.VpnKey,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number
                        )
                        Button(
                            onClick = onVerifyCode,
                            enabled = !state.isLoading && state.verificationCode.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Validar")
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = state.birthdate,
                    onValueChange = {},
                    label = { Text("Fecha de Nacimiento") },
                    leadingIcon = { Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker.value = true },
                    enabled = false,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                Column {
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    )

                    if (state.password.isNotEmpty()) {
                        PasswordRequirementsView(state.password)
                    }
                }
            }

            // Sección de Términos Realista
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = onTermsChange,
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )

                    val annotatedString = buildAnnotatedString {
                        append("Acepto los ")
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "terms",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = Color(0xFF2196F3),
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold
                                    )
                                ),
                                linkInteractionListener = { _ ->
                                    showTermsDialog = true
                                }
                            )
                        ) {
                            append("Términos y Condiciones")
                        }
                        append(". Entiendo que esta es una herramienta de apoyo al bienestar.")
                    }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            item {
                Button(
                    onClick = onRegister,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = isRegisterEnabled(state)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                item {
                    Text(
                        text = """
                            Bienvenido a PsyConnect. Al utilizar nuestra aplicación, usted acepta los siguientes términos:

                            1. Uso de la Aplicación: PsyConnect es una herramienta diseñada para apoyar el bienestar emocional y el autocuidado. No es un servicio de emergencia ni sustituye la terapia profesional brindada por especialistas titulados.

                            2. Privacidad: Sus datos personales y registros en el diario emocional se almacenan de forma segura. Nos comprometemos a proteger su privacidad según las leyes vigentes de protección de datos.

                            3. Responsabilidad: El usuario es el único responsable del uso que haga de la información y herramientas proporcionadas por la aplicación. PsyConnect no se hace responsable por decisiones tomadas basadas en el contenido de la app.

                            4. No es Asistencia Médica: Si usted se encuentra en una situación de crisis o riesgo para su vida, debe contactar inmediatamente a los servicios de emergencia de su localidad.

                            5. Actualizaciones: Podemos actualizar estos términos periódicamente. El uso continuado de la app tras una modificación implica la aceptación de los nuevos términos.

                            Al marcar la casilla de aceptación, usted confirma que ha leído, comprendido y aceptado la totalidad de estos términos.
                        """.trimIndent(),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

fun isRegisterEnabled(state: RegisterUiState): Boolean {
    val passwordMet = state.password.length >= 8 &&
            state.password.any { it.isUpperCase() } &&
            state.password.any { it.isLowerCase() } &&
            state.password.any { it.isDigit() } &&
            state.password.any { !it.isLetterOrDigit() }

    return state.name.isNotBlank() &&
            state.username.isNotBlank() &&
            state.isUsernameAvailable == true &&
            state.email.isNotBlank() &&
            state.isEmailAvailable == true &&
            state.isEmailVerified &&
            passwordMet &&
            state.birthdate.isNotBlank() &&
            state.termsAccepted &&
            !state.isLoading
}