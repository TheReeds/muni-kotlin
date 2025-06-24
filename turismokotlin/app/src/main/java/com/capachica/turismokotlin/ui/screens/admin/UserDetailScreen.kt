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
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.UsuarioResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.EmprendedorViewModel
import com.capachica.turismokotlin.ui.viewmodel.UserViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Long,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val userViewModel: UserViewModel = viewModel(factory = factory)
    val emprendedorViewModel: EmprendedorViewModel = viewModel(factory = factory)
    
    val usuarioState by userViewModel.usuarioState.collectAsState()
    val emprendedoresState by emprendedorViewModel.emprendedoresState.collectAsState()
    val operacionState by userViewModel.operacionState.collectAsState()
    
    var showEmprendedorDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf("") }
    
    // Cargar datos al inicio
    LaunchedEffect(userId) {
        userViewModel.getUsuarioById(userId)
        emprendedorViewModel.getAllEmprendedores()
    }
    
    // Mostrar mensaje de operación completada
    LaunchedEffect(operacionState) {
        if (operacionState is Result.Success) {
            // Recargar usuario después de operación exitosa
            userViewModel.getUsuarioById(userId)
            userViewModel.resetOperacionState()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Detalles del Usuario",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        when (val state = usuarioState) {
            is Result.Loading -> {
                LoadingScreen()
            }
            is Result.Success -> {
                val usuario = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        UserInfoCard(usuario = usuario)
                    }
                    
                    item {
                        RoleManagementCard(
                            usuario = usuario,
                            onManageRoles = { showRoleDialog = true }
                        )
                    }
                    
                    item {
                        EmprendedorManagementCard(
                            usuario = usuario,
                            onAssignEmprendedor = { 
                                selectedAction = "assign"
                                showEmprendedorDialog = true 
                            },
                            onChangeEmprendedor = { 
                                selectedAction = "change"
                                showEmprendedorDialog = true 
                            },
                            onUnassignEmprendedor = {
                                userViewModel.desasignarUsuarioDeEmprendedor(usuario.id)
                            }
                        )
                    }
                    
                    // Mostrar estado de operación
                    operacionState?.let { result ->
                        item {
                            when (result) {
                                is Result.Loading -> {
                                    Card(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text("Procesando operación...")
                                        }
                                    }
                                }
                                is Result.Success -> {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = result.data,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
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
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Error,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = result.message,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is Result.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    // Dialog para seleccionar emprendedor
    if (showEmprendedorDialog) {
        EmprendedorSelectionDialog(
            emprendedoresState = emprendedoresState,
            action = selectedAction,
            onDismiss = { showEmprendedorDialog = false },
            onEmprendedorSelected = { emprendedor ->
                when (selectedAction) {
                    "assign" -> userViewModel.asignarUsuarioAEmprendedor(userId, emprendedor.id)
                    "change" -> userViewModel.cambiarUsuarioDeEmprendedor(userId, emprendedor.id)
                }
                showEmprendedorDialog = false
            }
        )
    }
    
    // Dialog para gestionar roles
    if (showRoleDialog) {
        usuarioState.let { state ->
            if (state is Result.Success) {
                RoleManagementDialog(
                    usuario = state.data,
                    onDismiss = { showRoleDialog = false },
                    onAssignRole = { rol -> userViewModel.asignarRolAUsuario(userId, rol) },
                    onRemoveRole = { rol -> userViewModel.quitarRolAUsuario(userId, rol) },
                    onResetRoles = { userViewModel.resetearRolesUsuario(userId) }
                )
            }
        }
    }
}

@Composable
fun UserInfoCard(usuario: UsuarioResponse) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(label = "Nombre", value = "${usuario.nombre} ${usuario.apellido}")
            InfoRow(label = "Usuario", value = usuario.username)
            InfoRow(label = "Email", value = usuario.email)
            InfoRow(label = "ID", value = usuario.id.toString())
        }
    }
}

@Composable
fun RoleManagementCard(
    usuario: UsuarioResponse,
    onManageRoles: () -> Unit
) {
    Card(
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Roles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onManageRoles) {
                    Text("Gestionar")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (usuario.roles.isEmpty()) {
                Text(
                    text = "Sin roles asignados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    usuario.roles.forEach { rol ->
                        AssistChip(
                            onClick = { },
                            label = { Text(rol) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmprendedorManagementCard(
    usuario: UsuarioResponse,
    onAssignEmprendedor: () -> Unit,
    onChangeEmprendedor: () -> Unit,
    onUnassignEmprendedor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Emprendedor Asignado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (usuario.emprendedor != null) {
                Column {
                    InfoRow(label = "Empresa", value = usuario.emprendedor.nombreEmpresa)
                    InfoRow(label = "Rubro", value = usuario.emprendedor.rubro)
                    InfoRow(label = "ID", value = usuario.emprendedor.id.toString())
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onChangeEmprendedor,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cambiar")
                        }
                        
                        OutlinedButton(
                            onClick = onUnassignEmprendedor,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Desasignar")
                        }
                    }
                }
            } else {
                Text(
                    text = "Sin emprendedor asignado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onAssignEmprendedor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Asignar Emprendedor")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EmprendedorSelectionDialog(
    emprendedoresState: Result<List<Emprendedor>>,
    action: String,
    onDismiss: () -> Unit,
    onEmprendedorSelected: (Emprendedor) -> Unit
) {
    val title = when (action) {
        "assign" -> "Asignar Emprendedor"
        "change" -> "Cambiar Emprendedor"
        else -> "Seleccionar Emprendedor"
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            when (emprendedoresState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    if (emprendedoresState.data.isEmpty()) {
                        Text("No hay emprendedores disponibles")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            items(emprendedoresState.data) { emprendedor ->
                                Card(
                                    onClick = { onEmprendedorSelected(emprendedor) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = emprendedor.nombreEmpresa,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = emprendedor.rubro,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        emprendedor.municipalidad?.let { municipalidad ->
                                            Text(
                                                text = "Municipalidad: ${municipalidad.nombre}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Text(
                        text = emprendedoresState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleManagementDialog(
    usuario: UsuarioResponse,
    onDismiss: () -> Unit,
    onAssignRole: (String) -> Unit,
    onRemoveRole: (String) -> Unit,
    onResetRoles: () -> Unit
) {
    val availableRoles = listOf("ADMIN", "MUNICIPALIDAD", "EMPRENDEDOR", "TURISTA")
    var selectedRole by remember { mutableStateOf("") }
    var showConfirmReset by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gestionar Roles") },
        text = {
            Column {
                Text("Roles actuales:")
                usuario.roles.forEach { rol ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(rol)
                        IconButton(
                            onClick = { onRemoveRole(rol) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Quitar rol",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Asignar nuevo rol:")
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Seleccionar rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableRoles.filter { it !in usuario.roles }.forEach { rol ->
                            DropdownMenuItem(
                                text = { Text(rol) },
                                onClick = {
                                    selectedRole = rol
                                    expanded = false
                                    onAssignRole(rol)
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showConfirmReset = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Resetear todos los roles")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
    
    if (showConfirmReset) {
        AlertDialog(
            onDismissRequest = { showConfirmReset = false },
            title = { Text("Confirmar") },
            text = { Text("¿Estás seguro de que quieres resetear todos los roles de este usuario?") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        onResetRoles()
                        showConfirmReset = false
                        onDismiss()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmReset = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}