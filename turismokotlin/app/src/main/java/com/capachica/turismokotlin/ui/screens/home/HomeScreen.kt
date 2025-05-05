package com.capachica.turismokotlin.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMunicipalidades: () -> Unit,
    onNavigateToEmprendedores: () -> Unit,
    onLogout: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: AuthViewModel = viewModel(factory = factory)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sistema de Turismo") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.logout()
                            onLogout()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido al Sistema de Turismo",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Explora municipalidades y emprendedores turísticos",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            HomeMenuButton(
                text = "Municipalidades",
                icon = Icons.Default.LocationCity,
                onClick = onNavigateToMunicipalidades
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeMenuButton(
                text = "Emprendedores",
                icon = Icons.Default.Business,
                onClick = onNavigateToEmprendedores
            )
        }
    }
}

@Composable
fun HomeMenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }
}