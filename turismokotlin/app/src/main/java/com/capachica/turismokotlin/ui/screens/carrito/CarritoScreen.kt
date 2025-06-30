package com.capachica.turismokotlin.ui.screens.carrito

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.viewmodel.CarritoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    onNavigateToCheckout: () -> Unit,
    onNavigateToServicio: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val carritoViewModel: CarritoViewModel = viewModel(factory = factory)
    
    val carritoState by carritoViewModel.carritoState.collectAsState()
    val totalState by carritoViewModel.totalState.collectAsState()
    val operationState by carritoViewModel.operationState.collectAsState()
    
    var showClearDialog by remember { mutableStateOf(false) }

    // Mostrar snackbar para operaciones
    operationState?.let { result ->
        when (result) {
            is Result.Success -> {
                LaunchedEffect(result) {
                    // Aquí podrías mostrar un snackbar
                    carritoViewModel.clearOperationState()
                }
            }
            is Result.Error -> {
                LaunchedEffect(result) {
                    // Aquí podrías mostrar un snackbar de error
                    carritoViewModel.clearOperationState()
                }
            }
            is Result.Loading -> {
                // No hacer nada
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (carritoState is Result.Success && (carritoState as Result.Success<CarritoResponse>).data.items.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Limpiar carrito"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (carritoState is Result.Success && (carritoState as Result.Success<CarritoResponse>).data.items.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "S/ %.2f".format((carritoState as Result.Success<CarritoResponse>).data.totalCarrito),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier.width(140.dp)
                        ) {
                            Text("Reservar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when (carritoState) {
            is Result.Loading -> {
                LoadingScreen()
            }
            is Result.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = (carritoState as Result.Error).message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = { carritoViewModel.cargarCarrito() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is Result.Success -> {
                val carrito = (carritoState as Result.Success<CarritoResponse>).data
                if (carrito.items.isEmpty()) {
                    EmptyCartContent(
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(carrito.items) { item ->
                            CarritoItemCard(
                                item = item,
                                onUpdateQuantity = { newQuantity ->
                                    carritoViewModel.actualizarCantidad(item.id, newQuantity)
                                },
                                onRemoveItem = {
                                    carritoViewModel.eliminarItem(item.id)
                                },
                                onNavigateToServicio = onNavigateToServicio
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el bottom bar
                        }
                    }
                }
            }
        }
    }

    // Dialog para confirmar limpiar carrito
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Limpiar carrito") },
            text = { Text("¿Estás seguro de que quieres eliminar todos los elementos del carrito?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        carritoViewModel.limpiarCarrito()
                        showClearDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EmptyCartContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCartCheckout,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "Tu carrito está vacío",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Agrega servicios turísticos para empezar a planificar tu viaje",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoItemCard(
    item: CarritoItemResponse,
    onUpdateQuantity: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    onNavigateToServicio: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigateToServicio(item.servicio.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.servicio.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = item.servicio.emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Fecha: ${item.fechaServicio}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (!item.notasEspeciales.isNullOrEmpty()) {
                        Text(
                            text = "Notas: ${item.notasEspeciales}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(onClick = onRemoveItem) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { 
                            if (item.cantidad > 1) {
                                onUpdateQuantity(item.cantidad - 1)
                            }
                        },
                        enabled = item.cantidad > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir cantidad"
                        )
                    }
                    
                    Text(
                        text = item.cantidad.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    IconButton(
                        onClick = { onUpdateQuantity(item.cantidad + 1) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar cantidad"
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "S/ %.2f".format(item.subtotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "S/ %.2f c/u".format(item.precioUnitario),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}