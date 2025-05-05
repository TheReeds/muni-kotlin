package com.capachica.turismokotlin.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.RegisterRequest
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.TurismoTextField
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: AuthViewModel = viewModel(factory = factory)
    val registerState by viewModel.registerState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf(false) }
    var apellidoError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var roleError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isLoggedIn) {
        if (isLoggedIn) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Registro",
                onBackClick = onNavigateToLogin
            )
        }
    ) { paddingValues ->
        if (registerState is Result.Loading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                TurismoTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = false
                    },
                    label = "Nombre",
                    isError = nombreError,
                    errorMessage = "Ingrese su nombre",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = apellido,
                    onValueChange = {
                        apellido = it
                        apellidoError = false
                    },
                    label = "Apellido",
                    isError = apellidoError,
                    errorMessage = "Ingrese su apellido",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = false
                    },
                    label = "Nombre de usuario",
                    isError = usernameError,
                    errorMessage = "Ingrese un nombre de usuario válido",
                    leadingIcon = Icons.Default.AccountCircle
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    label = "Correo electrónico",
                    isError = emailError,
                    errorMessage = "Ingrese un correo electrónico válido",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = "Contraseña",
                    isPassword = true,
                    isError = passwordError,
                    errorMessage = "Ingrese una contraseña válida (mínimo 6 caracteres)",
                    leadingIcon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = false
                    },
                    label = "Confirmar contraseña",
                    isPassword = true,
                    isError = confirmPasswordError,
                    errorMessage = "Las contraseñas no coinciden",
                    leadingIcon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selección de rol
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Seleccione su rol",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (roleError) {
                        Text(
                            text = "Seleccione un rol",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    val roles = listOf(
                        "Municipalidad" to "municipalidad",
                        "Emprendedor" to "emprendedor",
                        "Usuario" to "user"
                    )

                    roles.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRole == value,
                                onClick = {
                                    selectedRole = value
                                    roleError = false
                                }
                            )
                            Text(
                                text = label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (registerState is Result.Error) {
                    Text(
                        text = (registerState as Result.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        // Validación básica
                        nombreError = nombre.isBlank()
                        apellidoError = apellido.isBlank()
                        usernameError = username.isBlank()
                        emailError = email.isBlank() || !email.contains("@")
                        passwordError = password.length < 6
                        confirmPasswordError = password != confirmPassword
                        roleError = selectedRole.isBlank()

                        if (!nombreError && !apellidoError && !usernameError &&
                            !emailError && !passwordError && !confirmPasswordError && !roleError) {

                            val registerRequest = RegisterRequest(
                                nombre = nombre,
                                apellido = apellido,
                                username = username,
                                email = email,
                                password = password,
                                roles = listOf(selectedRole)
                            )

                            scope.launch {
                                viewModel.register(registerRequest)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text("¿Ya tienes una cuenta? Inicia sesión")
                }
            }
        }
    }
}