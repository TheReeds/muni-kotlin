package com.capachica.turismokotlin.ui.screens.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.capachica.turismokotlin.data.model.CartItemRemoto
import com.capachica.turismokotlin.data.model.MetodoPago
import com.capachica.turismokotlin.ui.viewmodel.CartViewModel
import com.capachica.turismokotlin.ui.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReservas: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val checkoutState by checkoutViewModel.uiState.collectAsState()

    var observaciones by remember { mutableStateOf("") }
    var contactoEmergencia by remember { mutableStateOf("") }
    var telefonoEmergencia by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf(MetodoPago.EFECTIVO) }

    // Manejar éxito de reserva
    LaunchedEffect(checkoutState.reservaCreada) {
        checkoutState.reservaCreada?.let {
            onNavigateToReservas()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Finalizar Compra") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        if (cartState.cart == null || cartState.cart!!.items.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "El carrito está vacío",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Volver al carrito")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resumen del carrito
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resumen del Pedido",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // Items del carrito
                items(cartState.cart!!.items) { item ->
                    CheckoutItemCard(item = item)
                }

                // Información de contacto
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Información de Contacto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = contactoEmergencia,
                                onValueChange = { contactoEmergencia = it },
                                label = { Text("Contacto de emergencia") },
                                placeholder = { Text("Nombre del contacto") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = telefonoEmergencia,
                                onValueChange = { telefonoEmergencia = it },
                                label = { Text("Teléfono de emergencia") },
                                placeholder = { Text("+51 999 999 999") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                            )
                        }
                    }
                }

                // Método de pago
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Método de Pago",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            MetodoPago.values().forEach { metodo ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = metodoPago == metodo,
                                            onClick = { metodoPago = metodo }
                                        )
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = metodoPago == metodo,
                                        onClick = { metodoPago = metodo }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = when (metodo) {
                                            MetodoPago.EFECTIVO -> Icons.Default.Money
                                            MetodoPago.TARJETA -> Icons.Default.CreditCard
                                            MetodoPago.TRANSFERENCIA -> Icons.Default.AccountBalance
                                            MetodoPago.YAPE -> Icons.Default.PhoneAndroid
                                            MetodoPago.PLIN -> Icons.Default.PhoneAndroid
                                        },
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (metodo) {
                                            MetodoPago.EFECTIVO -> "Efectivo"
                                            MetodoPago.TARJETA -> "Tarjeta de crédito/débito"
                                            MetodoPago.TRANSFERENCIA -> "Transferencia bancaria"
                                            MetodoPago.YAPE -> "Yape"
                                            MetodoPago.PLIN -> "Plin"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Observaciones
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Observaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = { observaciones = it },
                                label = { Text("Observaciones adicionales (opcional)") },
                                placeholder = { Text("Solicitudes especiales...") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                minLines = 2
                            )
                        }
                    }
                }
            }

            // Footer con total y botón de pago
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Subtotal:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Total a pagar:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "S/ ${cartState.cart!!.totalCarrito}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "S/ ${cartState.cart!!.totalCarrito}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de confirmar
                    Button(
                        onClick = {
                            checkoutViewModel.crearReserva(
                                observaciones = observaciones.takeIf { it.isNotBlank() },
                                contactoEmergencia = contactoEmergencia.takeIf { it.isNotBlank() },
                                telefonoEmergencia = telefonoEmergencia.takeIf { it.isNotBlank() },
                                metodoPago = metodoPago
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !checkoutState.isLoading
                    ) {
                        if (checkoutState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Confirmar Reserva")
                    }

                    // Error message
                    checkoutState.error?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutItemCard(
    item: CartItemRemoto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.servicio.imagenUrl ?: "https://via.placeholder.com/60x60",
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.servicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Cantidad: ${item.cantidad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Fecha: ${item.fechaServicio}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                item.notasEspeciales?.let { notas ->
                    Text(
                        text = "Notas: $notas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "S/ ${item.subtotal}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}