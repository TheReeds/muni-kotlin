package com.capachica.turismokotlin.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToServicios: () -> Unit,
    onNavigateToPlanes: () -> Unit,
    onNavigateToReservas: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val userRoles by authViewModel.userRoles.collectAsState(initial = emptySet())
    val isAdmin = userRoles.contains("ROLE_ADMIN")
    val isEmprendedor = userRoles.contains("ROLE_EMPRENDEDOR")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    if (isAdmin) "Panel de Administración"
                    else "Gestión de Negocio"
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "¿Qué deseas gestionar?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Gestión de Servicios
            item {
                GestionCard(
                    title = "Gestionar Servicios",
                    description = if (isAdmin)
                        "Ver y administrar todos los servicios del sistema"
                    else "Crear, editar y gestionar tus servicios turísticos",
                    icon = Icons.Default.RoomService,
                    onClick = onNavigateToServicios
                )
            }

            // Gestión de Planes (solo para admin y municipalidades)
            if (isAdmin || userRoles.contains("ROLE_MUNICIPALIDAD")) {
                item {
                    GestionCard(
                        title = "Gestionar Planes",
                        description = if (isAdmin)
                            "Ver y administrar todos los planes turísticos"
                        else "Crear y gestionar planes turísticos para tu municipalidad",
                        icon = Icons.Default.Map,
                        onClick = onNavigateToPlanes
                    )
                }
            }

            // Gestión de Reservas
            item {
                GestionCard(
                    title = "Gestionar Reservas",
                    description = if (isAdmin)
                        "Ver todas las reservas del sistema"
                    else "Ver y gestionar las reservas de tus servicios",
                    icon = Icons.Default.BookmarkBorder,
                    onClick = onNavigateToReservas
                )
            }

            // Estadísticas (placeholder)
            if (isEmprendedor || isAdmin) {
                item {
                    GestionCard(
                        title = "Estadísticas",
                        description = "Ver métricas y reportes de rendimiento",
                        icon = Icons.Default.Analytics,
                        onClick = { /* TODO: Implementar estadísticas */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GestionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
