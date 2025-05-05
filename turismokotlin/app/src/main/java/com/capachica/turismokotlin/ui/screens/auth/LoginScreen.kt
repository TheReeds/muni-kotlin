package com.capachica.turismokotlin.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.TurismoTextField
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: AuthViewModel = viewModel(factory = factory)
    val loginState by viewModel.loginState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        // Verificar estado de login al iniciar
        viewModel.checkLoginStatus()
    }

    LaunchedEffect(key1 = isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Iniciar Sesión") }
            )
        }
    ) { paddingValues ->
        // Solo mostrar LoadingScreen si realmente estamos procesando un login,
        // no al cargar la pantalla inicialmente
        if (loginState is Result.Loading && username.isNotEmpty() && password.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bienvenido al Sistema de Turismo",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                TurismoTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = false
                    },
                    label = "Nombre de usuario",
                    isError = usernameError,
                    errorMessage = "Ingrese un nombre de usuario válido",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                TurismoTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = "Contraseña",
                    isPassword = true,
                    isError = passwordError,
                    errorMessage = "Ingrese una contraseña válida",
                    leadingIcon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (loginState is Result.Error) {
                    Text(
                        text = (loginState as Result.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        // Validación básica
                        usernameError = username.isBlank()
                        passwordError = password.isBlank()

                        if (!usernameError && !passwordError) {
                            scope.launch {
                                viewModel.login(username, password)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text("¿No tienes una cuenta? Regístrate")
                }
            }
        }
    }
}