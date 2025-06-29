package com.capachica.turismokotlin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.ServicioTuristico
import com.capachica.turismokotlin.data.model.EstadoServicio
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.viewmodel.CarritoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

/**
 * Botón rápido para agregar servicios al carrito
 * Útil para mostrar en listas o cards pequeñas
 */
@Composable
fun QuickAddToCartButton(
    servicio: ServicioTuristico,
    factory: ViewModelFactory,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val carritoViewModel: CarritoViewModel = viewModel(factory = factory)
    val operationState by carritoViewModel.operationState.collectAsState()
    
    var showQuickDialog by remember { mutableStateOf(false) }
    
    // Manejar operaciones del carrito
    LaunchedEffect(operationState) {
        if (operationState is Result.Success) {
            showQuickDialog = false
            carritoViewModel.clearOperationState()
        }
    }
    
    // Solo mostrar si el servicio está activo
    if (servicio.estado == EstadoServicio.ACTIVO) {
        if (compact) {
            // Versión compacta - solo icono
            IconButton(
                onClick = { showQuickDialog = true },
                modifier = modifier
            ) {
                if (operationState is Result.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            // Versión completa - botón con texto
            OutlinedButton(
                onClick = { showQuickDialog = true },
                modifier = modifier,
                enabled = operationState !is Result.Loading
            ) {
                if (operationState is Result.Loading) {
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
        }
        
        // Diálogo rápido de agregar al carrito
        if (showQuickDialog) {
            QuickAddToCartDialog(
                servicio = servicio,
                onAddToCart = { cantidad, fechaServicio, notas ->
                    carritoViewModel.agregarItem(servicio.id, cantidad, fechaServicio, notas)
                },
                onDismiss = { showQuickDialog = false },
                isLoading = operationState is Result.Loading,
                error = (operationState as? Result.Error)?.message
            )
        }
    }
}

/**
 * Versión simplificada del diálogo de agregar al carrito
 * Con valores predeterminados para agregar rápidamente
 */
@Composable
fun QuickAddToCartDialog(
    servicio: ServicioTuristico,
    onAddToCart: (Int, String, String?) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    var cantidad by remember { mutableStateOf(1) }
    var fechaServicio by remember { mutableStateOf("") }
    var useQuickAdd by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCartCheckout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Agregar Rápidamente")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información del servicio
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = servicio.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$${servicio.precio}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (servicio.capacidadMaxima > 0) {
                                Text(
                                    text = "Max ${servicio.capacidadMaxima}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                
                // Selector de modo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = useQuickAdd,
                        onCheckedChange = { useQuickAdd = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar rápidamente (fecha por definir)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (!useQuickAdd) {
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
                }
                
                // Total
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
                    if (useQuickAdd) {
                        // Agregar rápidamente con fecha TBD
                        onAddToCart(1, "TBD", "Fecha por definir")
                    } else {
                        // Agregar con datos específicos
                        if (fechaServicio.isNotEmpty()) {
                            onAddToCart(cantidad, fechaServicio, null)
                        }
                    }
                },
                enabled = !isLoading && (useQuickAdd || fechaServicio.isNotEmpty())
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
                Text(if (useQuickAdd) "Agregar Rápido" else "Agregar")
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