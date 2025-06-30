package com.capachica.turismokotlin.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.UsuarioDetallado
import com.capachica.turismokotlin.ui.viewmodel.AdminUsuariosViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminUsuariosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedUsuario by remember { mutableStateOf<UsuarioDetallado?>(null) }
    var filterRol by remember { mutableStateOf("TODOS") }

    // Manejar mensajes
    LaunchedEffect(uiState.successMessage, uiState.error) {
        if (uiState.successMessage != null || uiState.error != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Usuarios") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                if (uiState.isOperating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )

        // Filtros
        LazyRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val roles = listOf("TODOS", "ROLE_USER", "ROLE_ADMIN", "ROLE_EMPRENDEDOR", "ROLE_MUNICIPALIDAD")
            items(roles) { rol ->
                FilterChip(
                    onClick = {
                        filterRol = rol
                        if (rol == "TODOS") {
                            viewModel.loadUsuarios()
                        } else {
                            viewModel.loadUsuariosPorRol(rol)
                        }
                    },
                    label = { Text(rol.removePrefix("ROLE_")) },
                    selected = filterRol == rol
                )
            }
        }

        // Mensajes de estado
        uiState.successMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                val usuariosToShow = if (filterRol == "TODOS") uiState.usuarios else uiState.usuariosFiltrados

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(usuariosToShow) { usuario ->
                        AdminUsuarioItem(
                            usuario = usuario,
                            onEditClick = { selectedUsuario = it },
                            onRoleAction = { usuarioId, action, rol ->
                                when (action) {
                                    "ADD" -> viewModel.asignarRol(usuarioId, rol)
                                    "REMOVE" -> viewModel.quitarRol(usuarioId, rol)
                                    "RESET" -> viewModel.resetearRoles(usuarioId)
                                }
                            },
                            onEmprendedorAction = { usuarioId, action, emprendedorId ->
                                when (action) {
                                    "ASSIGN" -> emprendedorId?.let { viewModel.asignarEmprendedor(usuarioId, it) }
                                    "UNASSIGN" -> viewModel.desasignarEmprendedor(usuarioId)
                                    "CHANGE" -> emprendedorId?.let { viewModel.cambiarEmprendedor(usuarioId, it) }
                                }
                            },
                            isOperating = uiState.isOperating
                        )
                    }
                }
            }
        }
    }

    // Dialog para editar usuario
    selectedUsuario?.let { usuario ->
        AdminUsuarioEditDialog(
            usuario = usuario,
            emprendedoresDisponibles = uiState.emprendedoresDisponibles,
            onDismiss = { selectedUsuario = null },
            onRoleAction = { action, rol ->
                when (action) {
                    "ADD" -> viewModel.asignarRol(usuario.id, rol)
                    "REMOVE" -> viewModel.quitarRol(usuario.id, rol)
                    "RESET" -> viewModel.resetearRoles(usuario.id)
                }
                selectedUsuario = null
            },
            onEmprendedorAction = { action, emprendedorId ->
                when (action) {
                    "ASSIGN" -> emprendedorId?.let { viewModel.asignarEmprendedor(usuario.id, it) }
                    "UNASSIGN" -> viewModel.desasignarEmprendedor(usuario.id)
                    "CHANGE" -> emprendedorId?.let { viewModel.cambiarEmprendedor(usuario.id, it) }
                }
                selectedUsuario = null
            },
            isOperating = uiState.isOperating
        )
    }
}

@Composable
private fun AdminUsuarioItem(
    usuario: UsuarioDetallado,
    onEditClick: (UsuarioDetallado) -> Unit,
    onRoleAction: (Long, String, String) -> Unit,
    onEmprendedorAction: (Long, String, Long?) -> Unit,
    isOperating: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${usuario.nombre} ${usuario.apellido}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "@${usuario.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { onEditClick(usuario) },
                    enabled = !isOperating
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Roles
            Text(
                text = "Roles:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(usuario.roles) { rol ->
                    InputChip(
                        onClick = { },
                        label = { Text(rol.removePrefix("ROLE_")) },
                        selected = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Quitar rol",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        if (!isOperating) {
                                            onRoleAction(usuario.id, "REMOVE", rol)
                                        }
                                    }
                            )
                        }
                    )
                }
            }

            // Emprendedor asignado
            usuario.emprendedor?.let { emprendedor ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emprendedor:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = emprendedor.nombreEmpresa,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = emprendedor.rubro,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Row {
                            IconButton(
                                onClick = { onEmprendedorAction(usuario.id, "CHANGE", null) },
                                enabled = !isOperating
                            ) {
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = "Cambiar emprendedor",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            IconButton(
                                onClick = { onEmprendedorAction(usuario.id, "UNASSIGN", null) },
                                enabled = !isOperating
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Desasignar emprendedor",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (usuario.roles.size > 1) {
                    OutlinedButton(
                        onClick = { onRoleAction(usuario.id, "RESET", "") },
                        enabled = !isOperating
                    ) {
                        Text("Resetear Roles")
                    }
                }

                Button(
                    onClick = { onEditClick(usuario) },
                    enabled = !isOperating
                ) {
                    Text("Gestionar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminUsuarioEditDialog(
    usuario: UsuarioDetallado,
    emprendedoresDisponibles: List<Emprendedor>,
    onDismiss: () -> Unit,
    onRoleAction: (String, String) -> Unit,
    onEmprendedorAction: (String, Long?) -> Unit,
    isOperating: Boolean
) {
    var selectedEmprendedor by remember { mutableStateOf<Emprendedor?>(null) }

    val availableRoles = listOf("ROLE_USER", "ROLE_ADMIN", "ROLE_EMPRENDEDOR", "ROLE_MUNICIPALIDAD")
    val rolesNoAsignados = availableRoles.filter { !usuario.roles.contains(it) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Gestionar Usuario: ${usuario.nombre}")
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Información del Usuario",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Email: ${usuario.email}")
                            Text("Username: @${usuario.username}")
                            Text("Roles actuales: ${usuario.roles.joinToString(", ") { it.removePrefix("ROLE_") }}")
                        }
                    }
                }

                // Agregar nuevos roles
                if (rolesNoAsignados.isNotEmpty()) {
                    item {
                        Text(
                            text = "Agregar Roles:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(rolesNoAsignados) { rol ->
                                FilterChip(
                                    onClick = {
                                        if (!isOperating) {
                                            onRoleAction("ADD", rol)
                                        }
                                    },
                                    label = { Text(rol.removePrefix("ROLE_")) },
                                    selected = false,
                                    enabled = !isOperating,
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Gestión de emprendedor
                item {
                    Text(
                        text = "Gestión de Emprendedor:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    usuario.emprendedor?.let { emprendedorActual ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Emprendedor actual:",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = emprendedorActual.nombreEmpresa,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = emprendedorActual.rubro,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            if (!isOperating) {
                                                onEmprendedorAction("UNASSIGN", null)
                                            }
                                        },
                                        enabled = !isOperating,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Desasignar")
                                    }

                                    Button(
                                        onClick = {
                                            // Mostrar lista para cambiar
                                        },
                                        enabled = !isOperating && emprendedoresDisponibles.isNotEmpty(),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.SwapHoriz,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cambiar")
                                    }
                                }
                            }
                        }
                    }

                    // Si no tiene emprendedor, mostrar lista para asignar
                    if (usuario.emprendedor == null && emprendedoresDisponibles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Asignar Emprendedor:",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                // Lista de emprendedores disponibles
                if ((usuario.emprendedor == null || selectedEmprendedor != null) && emprendedoresDisponibles.isNotEmpty()) {
                    items(emprendedoresDisponibles.take(5)) { emprendedor ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isOperating) {
                                        if (usuario.emprendedor == null) {
                                            onEmprendedorAction("ASSIGN", emprendedor.id)
                                        } else {
                                            onEmprendedorAction("CHANGE", emprendedor.id)
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedEmprendedor?.id == emprendedor.id)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = emprendedor.nombreEmpresa,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = emprendedor.rubro,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${emprendedor.municipalidad.nombre} - ${emprendedor.categoria.nombre}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    if (usuario.emprendedor == null) Icons.Default.Add else Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (emprendedoresDisponibles.size > 5) {
                        item {
                            Text(
                                text = "... y ${emprendedoresDisponibles.size - 5} emprendedores más",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Mensaje si no hay emprendedores disponibles
                if (emprendedoresDisponibles.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "No hay emprendedores disponibles para asignar",
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isOperating
            ) {
                if (isOperating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Cerrar")
            }
        },
        dismissButton = {
            if (usuario.roles.size > 1) {
                TextButton(
                    onClick = {
                        if (!isOperating) {
                            onRoleAction("RESET", "")
                        }
                    },
                    enabled = !isOperating
                ) {
                    Text("Resetear Roles", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

// 6. FUNCIÓN AUXILIAR PARA MOSTRAR NOTIFICACIONES

// ui/components/AdminNotification.kt
@Composable
fun AdminNotificationCard(
    message: String,
    type: NotificationType,
    onDismiss: () -> Unit = {}
) {
    val containerColor = when (type) {
        NotificationType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
        NotificationType.ERROR -> MaterialTheme.colorScheme.errorContainer
        NotificationType.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        NotificationType.INFO -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (type) {
        NotificationType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
        NotificationType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        NotificationType.WARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        NotificationType.INFO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val icon = when (type) {
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
        NotificationType.ERROR -> Icons.Default.Error
        NotificationType.WARNING -> Icons.Default.Warning
        NotificationType.INFO -> Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

enum class NotificationType {
    SUCCESS, ERROR, WARNING, INFO
}