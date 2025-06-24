package com.capachica.turismokotlin.ui.screens.admin

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.UsuarioResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.UserViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateToUserDetail: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val userViewModel: UserViewModel = viewModel(factory = factory)
    val usuariosState by userViewModel.usuariosState.collectAsState()
    
    var selectedRoleFilter by remember { mutableStateOf("TODOS") }
    var showOnlyWithoutEmprendedor by remember { mutableStateOf(false) }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        userViewModel.getAllUsuarios()
    }
    
    // Filtrar por rol cuando cambie el filtro
    LaunchedEffect(selectedRoleFilter, showOnlyWithoutEmprendedor) {
        if (showOnlyWithoutEmprendedor) {
            userViewModel.getUsuariosSinEmprendedor()
        } else if (selectedRoleFilter != "TODOS") {
            userViewModel.getUsuariosPorRol(selectedRoleFilter)
        } else {
            userViewModel.getAllUsuarios()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Gestión de Usuarios",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filtros
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Filtro por rol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rol:",
                            modifier = Modifier.width(80.dp)
                        )
                        
                        var expanded by remember { mutableStateOf(false) }
                        val roles = listOf("TODOS", "ADMIN", "MUNICIPALIDAD", "EMPRENDEDOR", "TURISTA")
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedRoleFilter,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                roles.forEach { rol ->
                                    DropdownMenuItem(
                                        text = { Text(rol) },
                                        onClick = {
                                            selectedRoleFilter = rol
                                            showOnlyWithoutEmprendedor = false
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Filtro por usuarios sin emprendedor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showOnlyWithoutEmprendedor,
                            onCheckedChange = { 
                                showOnlyWithoutEmprendedor = it
                                if (it) {
                                    selectedRoleFilter = "TODOS"
                                }
                            }
                        )
                        Text(
                            text = "Solo usuarios sin emprendedor asignado",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de usuarios
            when (val state = if (showOnlyWithoutEmprendedor) {
                userViewModel.usuariosSinEmprendedorState.collectAsState().value
            } else if (selectedRoleFilter != "TODOS") {
                userViewModel.usuariosPorRolState.collectAsState().value
            } else {
                usuariosState
            }) {
                is Result.Loading -> {
                    LoadingScreen()
                }
                is Result.Success -> {
                    if (state.data.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No se encontraron usuarios",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { usuario ->
                                UserCard(
                                    usuario = usuario,
                                    onUserClick = { onNavigateToUserDetail(usuario.id) }
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    usuario: UsuarioResponse,
    onUserClick: () -> Unit
) {
    Card(
        onClick = onUserClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${usuario.nombre} ${usuario.apellido}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Usuario: ${usuario.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Email: ${usuario.email}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Roles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                usuario.roles.forEach { rol ->
                    SuggestionChip(
                        onClick = { },
                        label = { 
                            Text(
                                text = rol,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
            
            // Emprendedor asignado
            if (usuario.emprendedor != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Emprendedor: ${usuario.emprendedor.nombreEmpresa}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sin emprendedor asignado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}