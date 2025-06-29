package com.capachica.turismokotlin.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.ConversacionResponse
import com.capachica.turismokotlin.data.model.MensajeResponse
import com.capachica.turismokotlin.data.model.TipoMensaje
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.viewmodel.ChatViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversacion: ConversacionResponse,
    onNavigateBack: () -> Unit,
    viewModelFactory: ViewModelFactory
) {
    val chatViewModel: ChatViewModel = viewModel(factory = viewModelFactory)
    
    val mensajes by chatViewModel.mensajesConversacionActual.collectAsState()
    val enviarMensajeState by chatViewModel.enviarMensajeState.collectAsState()
    
    var textoMensaje by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Marcar mensajes como leídos al entrar
    LaunchedEffect(conversacion.id) {
        chatViewModel.seleccionarConversacion(conversacion)
        chatViewModel.marcarMensajesComoLeidos(conversacion.id)
    }
    
    // Auto-scroll cuando llegan nuevos mensajes
    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(mensajes.size - 1)
            }
        }
    }
    
    // Limpiar estados al enviar mensaje exitosamente
    LaunchedEffect(enviarMensajeState) {
        if (enviarMensajeState is Result.Success) {
            textoMensaje = ""
            chatViewModel.clearStates()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = conversacion.emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = conversacion.emprendedor.rubro,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
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

        // Lista de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mensajes) { mensaje ->
                MensajeItem(
                    mensaje = mensaje,
                    esPropio = mensaje.emisor.id == conversacion.usuarioId
                )
            }
        }

        // Error al enviar mensaje
        if (enviarMensajeState is Result.Error) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Error al enviar mensaje: ${(enviarMensajeState as Result.Error).message}",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Campo de entrada de mensaje
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = textoMensaje,
                    onValueChange = { textoMensaje = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") },
                    maxLines = 4,
                    enabled = enviarMensajeState !is Result.Loading
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Botón de enviar
                FilledIconButton(
                    onClick = {
                        if (textoMensaje.isNotBlank()) {
                            chatViewModel.enviarMensaje(
                                conversacionId = conversacion.id,
                                contenido = textoMensaje.trim(),
                                tipoMensaje = TipoMensaje.TEXTO
                            )
                        }
                    },
                    enabled = textoMensaje.isNotBlank() && enviarMensajeState !is Result.Loading,
                    modifier = Modifier.size(48.dp)
                ) {
                    if (enviarMensajeState is Result.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar mensaje"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeItem(
    mensaje: MensajeResponse,
    esPropio: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esPropio) Arrangement.End else Arrangement.Start
    ) {
        if (!esPropio) {
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (esPropio) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (esPropio) 16.dp else 4.dp,
                bottomEnd = if (esPropio) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Contenido del mensaje
                Text(
                    text = mensaje.contenido,
                    color = if (esPropio) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Información adicional
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatearHoraMensaje(mensaje.fechaEnvio),
                        color = if (esPropio) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                    
                    // Indicador de estado del mensaje (solo para mensajes propios)
                    if (esPropio) {
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Icon(
                            imageVector = if (mensaje.leido) {
                                // Doble check para leído
                                Icons.Default.Send // En una app real usarías iconos de check doble
                            } else {
                                // Check simple para enviado
                                Icons.Default.Send
                            },
                            contentDescription = if (mensaje.leido) "Leído" else "Enviado",
                            modifier = Modifier.size(12.dp),
                            tint = if (esPropio) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }
                }
            }
        }
        
        if (esPropio) {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

private fun formatearHoraMensaje(fechaString: String): String {
    return try {
        val fecha = LocalDateTime.parse(fechaString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val ahora = LocalDateTime.now()
        
        when {
            fecha.toLocalDate() == ahora.toLocalDate() -> {
                // Hoy - mostrar solo hora
                fecha.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            fecha.toLocalDate() == ahora.toLocalDate().minusDays(1) -> {
                // Ayer
                "Ayer ${fecha.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            }
            else -> {
                // Más días - mostrar fecha y hora
                fecha.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
            }
        }
    } catch (e: Exception) {
        "---"
    }
}