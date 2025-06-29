package com.capachica.turismokotlin.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.ConversacionResponse
import com.capachica.turismokotlin.data.model.MensajesNoLeidosResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.viewmodel.ChatViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversacionesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (ConversacionResponse) -> Unit,
    viewModelFactory: ViewModelFactory
) {
    val chatViewModel: ChatViewModel = viewModel(factory = viewModelFactory)
    
    val conversacionesState by chatViewModel.conversacionesState.collectAsState()
    val mensajesNoLeidosState by chatViewModel.mensajesNoLeidosState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conversaciones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Badge de mensajes no leídos
                    if (mensajesNoLeidosState is Result.Success && 
                        (mensajesNoLeidosState as Result.Success<MensajesNoLeidosResponse>).data.cantidadNoLeidos > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = (mensajesNoLeidosState as Result.Success<MensajesNoLeidosResponse>).data.cantidadNoLeidos.toString(),
                                color = MaterialTheme.colorScheme.onError,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Contenido
        when (conversacionesState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is Result.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error al cargar conversaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (conversacionesState as Result.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { chatViewModel.cargarConversaciones() }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
            
            is Result.Success -> {
                if ((conversacionesState as Result.Success<List<ConversacionResponse>>).data.isEmpty()) {
                    // Estado vacío
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes conversaciones",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Las conversaciones aparecerán cuando inicies un chat con un emprendedor",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    // Lista de conversaciones
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items((conversacionesState as Result.Success<List<ConversacionResponse>>).data) { conversacion ->
                            ConversacionItem(
                                conversacion = conversacion,
                                onClick = { onNavigateToChat(conversacion) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversacionItem(
    conversacion: ConversacionResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del emprendedor
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversacion.emprendedor.nombreEmpresa.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información de la conversación
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversacion.emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Fecha del último mensaje
                    conversacion.ultimoMensaje?.let { ultimoMensaje ->
                        Text(
                            text = formatearFecha(ultimoMensaje.fechaEnvio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Último mensaje
                    Text(
                        text = conversacion.ultimoMensaje?.contenido ?: "Conversación iniciada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (conversacion.ultimoMensaje?.leido == false) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        fontWeight = if (conversacion.ultimoMensaje?.leido == false) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        },
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Badge de mensajes no leídos
                    if (conversacion.mensajesNoLeidos > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = conversacion.mensajesNoLeidos.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatearFecha(fechaString: String): String {
    return try {
        val fecha = LocalDateTime.parse(fechaString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val ahora = LocalDateTime.now()
        
        when {
            fecha.toLocalDate() == ahora.toLocalDate() -> {
                // Hoy - mostrar hora
                fecha.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            fecha.toLocalDate() == ahora.toLocalDate().minusDays(1) -> {
                // Ayer
                "Ayer"
            }
            fecha.year == ahora.year -> {
                // Este año - mostrar día y mes
                fecha.format(DateTimeFormatter.ofPattern("dd/MM"))
            }
            else -> {
                // Otro año - mostrar día, mes y año
                fecha.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
            }
        }
    } catch (e: Exception) {
        "---"
    }
}