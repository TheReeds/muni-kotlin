package com.capachica.turismokotlin.ui.screens.servicios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.*
import com.capachica.turismokotlin.ui.viewmodel.ServicioTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.CarritoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioDetailScreen(
    servicioId: Long,
    onNavigateToEdit: () -> Unit = {},
    onNavigateToEmprendedor: (Long) -> Unit = {},
    onNavigateToCarrito: () -> Unit = {},
    onBack: () -> Unit,
    canEdit: Boolean = false,
    factory: ViewModelFactory
) {
    val viewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    val carritoViewModel: CarritoViewModel = viewModel(factory = factory)
    val servicioState by viewModel.servicioState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val cambiarEstadoState by viewModel.cambiarEstadoState.collectAsState()
    val carritoOperationState by carritoViewModel.operationState.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEstadoDialog by remember { mutableStateOf(false) }
    var showAddToCartDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(servicioId) {
        viewModel.getServicioById(servicioId)
    }
    
    // Manejar resultado de eliminación
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success && (deleteState as Result.Success<Boolean>).data) {
            onBack()
        }
    }
    
    // Manejar cambio de estado
    LaunchedEffect(cambiarEstadoState) {
        if (cambiarEstadoState is Result.Success) {
            viewModel.getServicioById(servicioId) // Recargar datos
        }
    }
    
    // Manejar operaciones del carrito
    LaunchedEffect(carritoOperationState) {
        if (carritoOperationState is Result.Success) {
            showAddToCartDialog = false
            carritoViewModel.clearOperationState()
        }
    }
    
    when (val state = servicioState) {
        is Result.Loading -> LoadingScreen()
        is Result.Error -> ErrorScreen(
            message = state.message,
            onRetry = { viewModel.getServicioById(servicioId) }
        )
        is Result.Success -> {
            val servicio = state.data
            
            Scaffold(
                topBar = {
                    TurismoAppBar(
                        title = "Detalle del Servicio",
                        onBackClick = onBack,
                        actions = {
                            if (canEdit) {
                                IconButton(onClick = { showEstadoDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ToggleOn,
                                        contentDescription = "Cambiar estado"
                                    )
                                }
                                IconButton(onClick = onNavigateToEdit) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar"
                                    )
                                }
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ServicioDetailContent(
                    servicio = servicio,
                    onNavigateToEmprendedor = onNavigateToEmprendedor,
                    onAddToCart = { showAddToCartDialog = true },
                    onNavigateToCarrito = onNavigateToCarrito,
                    canEdit = canEdit,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            // Diálogo de confirmación para eliminar
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Está seguro que desea eliminar este servicio? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteServicio(servicioId)
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
            
            // Diálogo para cambiar estado
            if (showEstadoDialog) {
                EstadoDialog(
                    estadoActual = servicio.estado,
                    onEstadoSelected = { nuevoEstado ->
                        viewModel.cambiarEstadoServicio(servicioId, nuevoEstado)
                        showEstadoDialog = false
                    },
                    onDismiss = { showEstadoDialog = false }
                )
            }
            
            // Diálogo para agregar al carrito
            if (showAddToCartDialog) {
                AddToCartDialog(
                    servicio = servicio,
                    onAddToCart = { cantidad, fechaServicio, notas ->
                        carritoViewModel.agregarItem(servicioId, cantidad, fechaServicio, notas)
                    },
                    onDismiss = { showAddToCartDialog = false },
                    isLoading = carritoOperationState is Result.Loading,
                    error = (carritoOperationState as? Result.Error)?.message
                )
            }
        }
    }
}

@Composable
fun ServicioDetailContent(
    servicio: ServicioTuristico,
    onNavigateToEmprendedor: (Long) -> Unit,
    onAddToCart: () -> Unit = {},
    onNavigateToCarrito: () -> Unit = {},
    canEdit: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen principal
        AsyncImage(
            model = servicio.imagenUrl ?: "https://via.placeholder.com/400x250",
            contentDescription = servicio.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        
        // Información básica
        ServicioBasicInfo(servicio = servicio)
        
        // Estado y tipo
        ServicioStatusInfo(servicio = servicio)
        
        // Información del emprendedor
        EmprendedorInfo(
            emprendedor = servicio.emprendedor,
            onNavigateToEmprendedor = onNavigateToEmprendedor
        )
        
        // Descripción
        if (!servicio.descripcion.isNullOrEmpty()) {
            ServicioDescription(descripcion = servicio.descripcion)
        }
        
        // Detalles del servicio
        ServicioDetails(servicio = servicio)
        
        // Información adicional
        if (!servicio.incluye.isNullOrEmpty() || !servicio.noIncluye.isNullOrEmpty() || !servicio.requisitos.isNullOrEmpty()) {
            ServicioAdditionalInfo(servicio = servicio)
        }
        
        // Botones de acción del carrito (solo si no es editor)
        if (!canEdit && servicio.estado == EstadoServicio.ACTIVO) {
            CartActionButtons(
                servicio = servicio,
                onAddToCart = onAddToCart,
                onNavigateToCarrito = onNavigateToCarrito
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ServicioBasicInfo(servicio: ServicioTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${servicio.precio}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "por persona",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${servicio.duracionHoras}h",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Max ${servicio.capacidadMaxima}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServicioStatusInfo(servicio: ServicioTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estado y Tipo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(servicio.estado.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (servicio.estado) {
                                EstadoServicio.ACTIVO -> Icons.Default.CheckCircle
                                EstadoServicio.INACTIVO -> Icons.Default.Pause
                                EstadoServicio.AGOTADO -> Icons.Default.Block
                                EstadoServicio.MANTENIMIENTO -> Icons.Default.Build
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (servicio.estado) {
                            EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
                            EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.surfaceVariant
                            EstadoServicio.AGOTADO -> MaterialTheme.colorScheme.errorContainer
                            EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                )
                
                AssistChip(
                    onClick = { },
                    label = { Text(servicio.tipo.name.replace("_", " ")) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (servicio.tipo) {
                                TipoServicio.ALOJAMIENTO -> Icons.Default.Hotel
                                TipoServicio.TRANSPORTE -> Icons.Default.DirectionsBus
                                TipoServicio.ALIMENTACION -> Icons.Default.Restaurant
                                TipoServicio.GUIA_TURISTICO -> Icons.Default.Person
                                TipoServicio.TOUR -> Icons.Default.Tour
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
            
            if (!servicio.ubicacion.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = servicio.ubicacion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EmprendedorInfo(
    emprendedor: EmprendedorWithMunicipalidad,
    onNavigateToEmprendedor: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigateToEmprendedor(emprendedor.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = emprendedor.nombreEmpresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = emprendedor.rubro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = emprendedor.municipalidad.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ServicioDescription(descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun ServicioDetails(servicio: ServicioTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detalles del Servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DetailItem(
                    icon = Icons.Default.Schedule,
                    label = "Duración",
                    value = "${servicio.duracionHoras} horas"
                )
                
                DetailItem(
                    icon = Icons.Default.Group,
                    label = "Capacidad",
                    value = "${servicio.capacidadMaxima} personas"
                )
                
                DetailItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Precio",
                    value = "$${servicio.precio}"
                )
            }
        }
    }
}

@Composable
fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ServicioAdditionalInfo(servicio: ServicioTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información Adicional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!servicio.incluye.isNullOrEmpty()) {
                AdditionalInfoSection(
                    title = "Incluye",
                    content = servicio.incluye,
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (!servicio.noIncluye.isNullOrEmpty()) {
                AdditionalInfoSection(
                    title = "No Incluye",
                    content = servicio.noIncluye,
                    icon = Icons.Default.Cancel,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (!servicio.requisitos.isNullOrEmpty()) {
                AdditionalInfoSection(
                    title = "Requisitos",
                    content = servicio.requisitos,
                    icon = Icons.Default.Info,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun AdditionalInfoSection(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EstadoDialog(
    estadoActual: EstadoServicio,
    onEstadoSelected: (EstadoServicio) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Estado") },
        text = {
            Column {
                Text("Estado actual: ${estadoActual.name}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Seleccione el nuevo estado:")
                
                Spacer(modifier = Modifier.height(12.dp))
                
                EstadoServicio.values().forEach { estado ->
                    if (estado != estadoActual) {
                        TextButton(
                            onClick = { onEstadoSelected(estado) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(estado.name)
                        }
                    }
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

@Composable
fun CartActionButtons(
    servicio: ServicioTuristico,
    onAddToCart: () -> Unit,
    onNavigateToCarrito: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🛒 Reservar Servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Precio destacado
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$${servicio.precio}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "por persona",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (servicio.capacidadMaxima > 0) {
                        Text(
                            text = "Máximo ${servicio.capacidadMaxima} personas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                // Botones de acción
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar al Carrito")
                    }
                    
                    OutlinedButton(
                        onClick = onNavigateToCarrito,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Carrito")
                    }
                }
            }
        }
    }
}

@Composable
fun AddToCartDialog(
    servicio: ServicioTuristico,
    onAddToCart: (Int, String, String?) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    var cantidad by remember { mutableStateOf(1) }
    var fechaServicio by remember { mutableStateOf("") }
    var notasEspeciales by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Agregar al Carrito")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información del servicio
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = servicio.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$${servicio.precio} por persona",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (servicio.capacidadMaxima > 0) {
                            Text(
                                text = "Capacidad máxima: ${servicio.capacidadMaxima}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Cantidad
                Column {
                    Text(
                        text = "Cantidad de personas",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (cantidad > 1) cantidad-- },
                            enabled = cantidad > 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Disminuir"
                            )
                        }
                        Text(
                            text = cantidad.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(
                            onClick = { 
                                if (servicio.capacidadMaxima <= 0 || cantidad < servicio.capacidadMaxima) {
                                    cantidad++
                                }
                            },
                            enabled = servicio.capacidadMaxima <= 0 || cantidad < servicio.capacidadMaxima
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aumentar"
                            )
                        }
                    }
                }
                
                // Fecha del servicio
                OutlinedTextField(
                    value = fechaServicio,
                    onValueChange = { fechaServicio = it },
                    label = { Text("Fecha deseada (YYYY-MM-DD)") },
                    placeholder = { Text("2024-12-25") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Notas especiales
                OutlinedTextField(
                    value = notasEspeciales,
                    onValueChange = { notasEspeciales = it },
                    label = { Text("Notas especiales (opcional)") },
                    placeholder = { Text("Requisitos dietarios, alergias, etc.") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Note,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                // Total
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$${servicio.precio * cantidad}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Mostrar error si existe
                error?.let { errorMessage ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fechaServicio.isNotEmpty()) {
                        onAddToCart(
                            cantidad,
                            fechaServicio,
                            if (notasEspeciales.isNotEmpty()) notasEspeciales else null
                        )
                    }
                },
                enabled = !isLoading && fechaServicio.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}