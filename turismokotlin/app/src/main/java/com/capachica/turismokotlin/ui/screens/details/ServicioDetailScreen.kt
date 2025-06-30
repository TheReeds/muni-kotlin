package com.capachica.turismokotlin.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.capachica.turismokotlin.data.model.EstadoServicio
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.model.TipoServicio
import com.capachica.turismokotlin.ui.viewmodel.CartViewModel
import com.capachica.turismokotlin.ui.viewmodel.ServicioDetailViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioDetailScreen(
    servicioId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToChat: (Long) -> Unit, // Agregar parámetro para navegación al chat
    viewModel: ServicioDetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()

    var showAddToCartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(servicioId) {
        viewModel.loadServicioDetails(servicioId)
    }

    // Manejar mensajes del carrito
    LaunchedEffect(cartState.successMessage) {
        cartState.successMessage?.let {
            kotlinx.coroutines.delay(2000)
            cartViewModel.clearMessages()
        }
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
                Button(onClick = { viewModel.loadServicioDetails(servicioId) }) {
                    Text("Reintentar")
                }
            }
        }

        uiState.servicio != null -> {
            ServicioDetailContent(
                servicio = uiState.servicio!!,
                onNavigateBack = onNavigateBack,
                onNavigateToEmprendedor = onNavigateToEmprendedor,
                onNavigateToChat = onNavigateToChat, // Pasar callback
                onAddToCart = { showAddToCartDialog = true },
                cartSuccessMessage = cartState.successMessage
            )
        }
    }

    // Dialog para agregar al carrito
    if (showAddToCartDialog && uiState.servicio != null) {
        AddToCartDialog(
            servicio = uiState.servicio!!,
            onDismiss = { showAddToCartDialog = false },
            onConfirm = { servicio, fechaServicio, cantidad, notasEspeciales ->
                cartViewModel.addToCart(
                    servicioId = servicio.id,
                    cantidad = cantidad,
                    fechaServicio = fechaServicio,
                    notasEspeciales = notasEspeciales
                )
                showAddToCartDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicioDetailContent(
    servicio: Servicio,
    onNavigateBack: () -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToChat: (Long) -> Unit, // Agregar parámetro
    onAddToCart: () -> Unit,
    cartSuccessMessage: String?
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Box {
            // Imagen principal
            AsyncImage(
                model = servicio.imagenUrl ?: "https://via.placeholder.com/400x250",
                contentDescription = servicio.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // Top bar transparente
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Compartir */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                    IconButton(
                        onClick = { /* Favorito */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }

        // Mensaje de éxito del carrito
        cartSuccessMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
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
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Información básica
                Column {
                    Text(
                        text = servicio.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        servicio.tipo?.let { tipo ->
                            AssistChip(
                                onClick = { },
                                label = { Text(getTipoServicioText(tipo)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getTipoServicioIcon(tipo),
                                        contentDescription = null
                                    )
                                }
                            )
                        }

                        AssistChip(
                            onClick = { },
                            label = { Text("${servicio.duracionHoras}h") },
                            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) }
                        )

                        AssistChip(
                            onClick = { },
                            label = { Text("Máx. ${servicio.capacidadMaxima}") },
                            leadingIcon = { Icon(Icons.Default.People, contentDescription = null) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = servicio.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                // Precio y disponibilidad
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Precio por persona",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "S/ ${servicio.precio}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (servicio.estado == EstadoServicio.ACTIVO) "Disponible" else "No disponible",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (servicio.estado != EstadoServicio.ACTIVO)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Cap. ${servicio.capacidadMaxima}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            item {
                // Información del emprendedor
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToEmprendedor(servicio.emprendedor.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ofrecido por",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = servicio.emprendedor.nombreEmpresa,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = servicio.emprendedor.rubro,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = servicio.emprendedor.municipalidad.nombre,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Botón de chat agregado aquí
                            IconButton(
                                onClick = { onNavigateToChat(servicio.emprendedor.id) }
                            ) {
                                Icon(
                                    Icons.Default.Chat,
                                    contentDescription = "Chatear con emprendedor",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Ver más",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Ubicación
            if (servicio.longitud != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ubicación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (servicio.latitud != null && servicio.longitud != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Lat: ${servicio.latitud}, Lng: ${servicio.longitud}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Incluye / No incluye / Requisitos / Recomendaciones
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!servicio.incluye.isNullOrEmpty()) {
                        InfoCard(
                            title = "Incluye",
                            content = servicio.incluye,
                            icon = Icons.Default.CheckCircle,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    if (!servicio.noIncluye.isNullOrEmpty()) {
                        InfoCard(
                            title = "No incluye",
                            content = servicio.noIncluye,
                            icon = Icons.Default.Cancel,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    if (!servicio.requisitos.isNullOrEmpty()) {
                        InfoCard(
                            title = "Requisitos",
                            content = servicio.requisitos,
                            icon = Icons.Default.Assignment,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        // Botones inferiores - Agregar al carrito y Chat
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Botón principal - Agregar al carrito
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = servicio.estado == EstadoServicio.ACTIVO
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar al Carrito - S/ ${servicio.precio}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón secundario - Chat (actualizado)
                OutlinedButton(
                    onClick = { onNavigateToChat(servicio.emprendedor.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consultar con ${servicio.emprendedor.nombreEmpresa}")
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}

@Composable
private fun AddToCartDialog(
    servicio: Servicio,
    onDismiss: () -> Unit,
    onConfirm: (Servicio, String, Int, String?) -> Unit
) {
    var fechaServicio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf(1) }
    var notasEspeciales by remember { mutableStateOf("") }

    // Fecha por defecto (mañana)
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fechaServicio = dateFormat.format(calendar.time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar al Carrito") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = servicio.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${servicio.precio} por persona",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    OutlinedTextField(
                        value = cantidad.toString(),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let {
                                if (it > 0 && it <= servicio.capacidadMaxima) {
                                    cantidad = it
                                }
                            }
                        },
                        label = { Text("Cantidad de personas *") },
                        supportingText = { Text("Máximo: ${servicio.capacidadMaxima}") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = fechaServicio,
                        onValueChange = { fechaServicio = it },
                        label = { Text("Fecha del servicio *") },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notasEspeciales,
                        onValueChange = { notasEspeciales = it },
                        label = { Text("Notas especiales (opcional)") },
                        placeholder = { Text("Solicitudes específicas...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total:")
                            Text(
                                text = "S/ ${servicio.precio * cantidad}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fechaServicio.isNotBlank()) {
                        onConfirm(
                            servicio,
                            fechaServicio,
                            cantidad,
                            notasEspeciales.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = fechaServicio.isNotBlank()
            ) {
                Text("Agregar al Carrito")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getTipoServicioText(tipo: TipoServicio): String {
    return when (tipo) {
        TipoServicio.ALOJAMIENTO -> "Alojamiento"
        TipoServicio.TRANSPORTE -> "Transporte"
        TipoServicio.ALIMENTACION -> "Alimentación"
        TipoServicio.GUIA_TURISTICO -> "Guía turística"
        TipoServicio.ACTIVIDAD_RECREATIVA -> "Recreación"
        TipoServicio.CULTURAL -> "Cultural"
        TipoServicio.AVENTURA -> "Aventura"
        TipoServicio.WELLNESS -> "Bienestar"
        TipoServicio.TOUR -> "Tour"
        TipoServicio.GASTRONOMICO -> "Gastronómico"
        TipoServicio.OTRO -> "Otro"
    }
}

private fun getTipoServicioIcon(tipo: TipoServicio): androidx.compose.ui.graphics.vector.ImageVector {
    return when (tipo) {
        TipoServicio.ALOJAMIENTO -> Icons.Default.Hotel
        TipoServicio.TRANSPORTE -> Icons.Default.DirectionsBus
        TipoServicio.ALIMENTACION -> Icons.Default.Restaurant
        TipoServicio.GUIA_TURISTICO -> Icons.Default.Person
        TipoServicio.ACTIVIDAD_RECREATIVA -> Icons.Default.SportsEsports
        TipoServicio.CULTURAL -> Icons.Default.Museum
        TipoServicio.AVENTURA -> Icons.Default.Hiking
        TipoServicio.WELLNESS -> Icons.Default.Favorite
        TipoServicio.TOUR -> Icons.Default.Map
        TipoServicio.GASTRONOMICO -> Icons.Default.LocalDining
        TipoServicio.OTRO -> Icons.Default.Category
    }
}
