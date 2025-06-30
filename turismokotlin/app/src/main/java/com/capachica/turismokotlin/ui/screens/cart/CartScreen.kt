package com.capachica.turismokotlin.ui.screens.cart

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.capachica.turismokotlin.data.model.CartRemoto
import com.capachica.turismokotlin.ui.components.CartItemCard
import com.capachica.turismokotlin.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by cartViewModel.uiState.collectAsState()
    val cart = uiState.cart

    LaunchedEffect(Unit) {
        cartViewModel.loadCart()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Mi Carrito") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                if (cart != null && cart.items.isNotEmpty()) {
                    TextButton(onClick = { cartViewModel.clearCart() }) {
                        Text("Limpiar")
                    }
                }
            }
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { cartViewModel.loadCart() }) {
                        Text("Reintentar")
                    }
                }
            }

            cart == null || cart.items.isEmpty() -> {
                EmptyCartContent(onNavigateBack = onNavigateBack)
            }

            else -> {
                CartContent(
                    cart = cart,
                    onUpdateQuantity = { itemId, quantity ->
                        cartViewModel.updateCartItem(itemId, quantity)
                    },
                    onRemoveItem = { itemId ->
                        cartViewModel.removeCartItem(itemId)
                    },
                    onNavigateToCheckout = onNavigateToCheckout,
                    isOperating = uiState.isOperating
                )
            }
        }
    }

    // Mostrar mensaje de éxito si existe
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            cartViewModel.clearMessages()
        }
    }
}

@Composable
private fun EmptyCartContent(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingCartCheckout,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Explora nuestros planes y servicios turísticos",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Explorar Servicios")
        }
    }
}

@Composable
private fun CartContent(
    cart: CartRemoto,
    onUpdateQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onNavigateToCheckout: () -> Unit,
    isOperating: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cart.items) { item ->
                CartItemCard(
                    item = item,
                    onUpdateQuantity = { quantity ->
                        onUpdateQuantity(item.id, quantity)
                    },
                    onRemove = { onRemoveItem(item.id) }
                )
            }
        }

        // Footer con total y botón de checkout
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total (${cart.totalItems} items)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "S/ ${"%.2f".format(cart.totalCarrito)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToCheckout,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isOperating
                ) {
                    if (isOperating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Proceder al Pago")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}