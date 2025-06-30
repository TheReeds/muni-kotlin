package com.capachica.turismokotlin.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import com.capachica.turismokotlin.ui.viewmodel.ChatUiState
import com.capachica.turismokotlin.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlans: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    emprendedorId: Long? = null,
    reservaId: Long? = null,
    chatViewModel: ChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by chatViewModel.uiState.collectAsState()
    val conversacionActual by chatViewModel.conversacionActual.collectAsState()
    val mensajes by chatViewModel.mensajes.collectAsState()
    val userRoles by authViewModel.userRoles.collectAsState(initial = emptySet())

    val esEmprendedorOAdmin = userRoles.contains("ROLE_EMPRENDEDOR") || userRoles.contains("ROLE_ADMIN")

    LaunchedEffect(emprendedorId) {
        if (emprendedorId != null && conversacionActual == null) {
            // Crear nueva conversación si se especifica un emprendedor
            // Esto se manejará desde la UI con un dialog
        }
    }

    when {
        conversacionActual == null -> {
            // Mostrar lista de conversaciones o crear nueva
            if (emprendedorId != null) {
                // Mostrar dialog para crear conversación
                CrearConversacionDialog(
                    emprendedorId = emprendedorId,
                    reservaId = reservaId,
                    onDismiss = onNavigateBack,
                    onCrearConversacion = { mensaje ->
                        // Llamar al método corregido
                        chatViewModel.iniciarConversacion(emprendedorId, mensaje, reservaId)
                    },
                    isLoading = uiState.isLoading
                )
            } else {
                // Lista de conversaciones
                ConversacionesListScreen(
                    conversaciones = uiState.conversaciones,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    onNavigateBack = onNavigateBack,
                    onConversacionClick = { conversacion ->
                        chatViewModel.abrirConversacion(conversacion.id)
                    },
                    onRefresh = { chatViewModel.loadConversaciones() },
                    esEmprendedorOAdmin = esEmprendedorOAdmin
                )
            }
        }
        else -> {
            // Mostrar conversación específica
            ConversacionScreen(
                conversacion = conversacionActual!!,
                mensajes = mensajes,
                uiState = uiState,
                onNavigateBack = {
                    chatViewModel.limpiarConversacionActual()
                    if (emprendedorId != null) {
                        onNavigateBack()
                    }
                },
                onEnviarMensaje = { mensaje ->
                    chatViewModel.enviarMensaje(mensaje)
                },
                onCargarMasMensajes = {
                    chatViewModel.cargarMasMensajes()
                },
                onCerrarConversacion = {
                    chatViewModel.cerrarConversacion()
                }
            )
        }
    }

    // Manejar conversación creada
    LaunchedEffect(uiState.conversacionCreada) {
        uiState.conversacionCreada?.let {
            // La conversación se abrirá automáticamente
        }
    }
    // Manejar errores y mensajes de éxito
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Aquí podrías mostrar un Snackbar o Toast si lo deseas
            kotlinx.coroutines.delay(3000)
            chatViewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            kotlinx.coroutines.delay(2000)
            chatViewModel.clearMessages()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversacionesListScreen(
    conversaciones: List<Conversacion>,
    isLoading: Boolean,
    error: String?,
    onNavigateBack: () -> Unit,
    onConversacionClick: (Conversacion) -> Unit,
    onRefresh: () -> Unit,
    esEmprendedorOAdmin: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    if (esEmprendedorOAdmin) "Chat Empresarial" else "Mis Conversaciones"
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
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
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = onRefresh) {
                        Text("Reintentar")
                    }
                }
            }

            conversaciones.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (esEmprendedorOAdmin)
                            "No tienes conversaciones con clientes"
                        else
                            "No tienes conversaciones activas",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = if (esEmprendedorOAdmin)
                            "Las conversaciones aparecerán cuando los usuarios te contacten"
                        else
                            "Contacta a emprendedores desde sus perfiles o servicios",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(conversaciones) { conversacion ->
                        ConversacionItem(
                            conversacion = conversacion,
                            onClick = { onConversacionClick(conversacion) },
                            esEmprendedorOAdmin = esEmprendedorOAdmin
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversacionItem(
    conversacion: Conversacion,
    onClick: () -> Unit,
    esEmprendedorOAdmin: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (esEmprendedorOAdmin) Icons.Default.Person else Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nombre
                Text(
                    text = if (esEmprendedorOAdmin)
                        "${conversacion.usuario.nombre} ${conversacion.usuario.apellido}"
                    else
                        conversacion.emprendedor.nombreEmpresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Último mensaje
                conversacion.ultimoMensaje?.let { ultimoMensaje ->
                    Text(
                        text = ultimoMensaje.mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Información adicional
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!esEmprendedorOAdmin) {
                        Text(
                            text = conversacion.emprendedor.rubro,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = formatFecha(conversacion.fechaUltimoMensaje),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Badge de mensajes no leídos
                if (conversacion.mensajesNoLeidos > 0) {
                    Badge {
                        Text(
                            text = if (conversacion.mensajesNoLeidos > 99) "99+" else "${conversacion.mensajesNoLeidos}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Estado de la conversación
                if (conversacion.estado != EstadoConversacion.ACTIVA) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = conversacion.estado.name,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversacionScreen(
    conversacion: Conversacion,
    mensajes: List<MensajeChat>,
    uiState: ChatUiState,
    onNavigateBack: () -> Unit,
    onEnviarMensaje: (String) -> Unit,
    onCargarMasMensajes: () -> Unit,
    onCerrarConversacion: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(mensajes.size - 1)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = conversacion.emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${conversacion.emprendedor.rubro} • ${conversacion.emprendedor.municipalidad?.nombre ?: "Municipalidad desconocida"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                if (conversacion.estado == EstadoConversacion.ACTIVA) {
                    IconButton(onClick = onCerrarConversacion) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar conversación")
                    }
                }
                IconButton(onClick = { /* Más opciones */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
            }
        )

        // Información de reserva asociada
        conversacion.codigoReservaAsociada?.let { codigoReserva ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Conversación sobre reserva: $codigoReserva",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón cargar más mensajes
            if (uiState.puedeCargarMasMensajes) {
                item {
                    TextButton(
                        onClick = onCargarMasMensajes,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoadingMensajes
                    ) {
                        if (uiState.isLoadingMensajes) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Cargar mensajes anteriores")
                    }
                }
            }

            items(mensajes) { mensaje ->
                MensajeChatItem(mensaje = mensaje)
            }

            // Indicador de envío
            if (uiState.isEnviandoMensaje) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enviando...",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Message Input
        if (conversacion.estado == EstadoConversacion.ACTIVA) {
            MessageInputSection(
                message = messageText,
                onMessageChange = { messageText = it },
                onSendMessage = {
                    onEnviarMensaje(messageText)
                    messageText = ""
                    keyboardController?.hide()
                },
                isEnabled = !uiState.isEnviandoMensaje
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Esta conversación está ${conversacion.estado.name.lowercase()}",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MensajeChatItem(mensaje: MensajeChat) {
    val esDelUsuario = !mensaje.esDeEmprendedor

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esDelUsuario) Arrangement.End else Arrangement.Start
    ) {
        if (!esDelUsuario) {
            // Avatar del emprendedor
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (esDelUsuario) Alignment.End else Alignment.Start
        ) {
            // Message Bubble
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (esDelUsuario) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (esDelUsuario) 16.dp else 4.dp,
                    bottomEnd = if (esDelUsuario) 4.dp else 16.dp
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (!esDelUsuario) {
                        Text(
                            text = mensaje.remitenteNombre,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = mensaje.mensaje,
                        color = if (esDelUsuario) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Message Info
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatHora(mensaje.fechaEnvio),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (esDelUsuario) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        if (mensaje.leido) Icons.Default.DoneAll else Icons.Default.Done,
                        contentDescription = if (mensaje.leido) "Leído" else "Enviado",
                        modifier = Modifier.size(12.dp),
                        tint = if (mensaje.leido) Color.Blue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (esDelUsuario) {
            Spacer(modifier = Modifier.width(8.dp))
            // Avatar del usuario
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageInputSection(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Campo de texto
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = { Text("Escribe tu mensaje...") },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                enabled = isEnabled,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { if (message.isNotBlank() && isEnabled) onSendMessage() }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de enviar
            FloatingActionButton(
                onClick = onSendMessage,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar mensaje"
                )
            }
        }
    }
}

@Composable
private fun CrearConversacionDialog(
    emprendedorId: Long,
    reservaId: Long?,
    onDismiss: () -> Unit,
    onCrearConversacion: (String) -> Unit,
    isLoading: Boolean
) {
    var mensaje by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Iniciar Conversación") },
        text = {
            Column {
                Text(
                    text = if (reservaId != null)
                        "Inicia una conversación sobre tu reserva"
                    else
                        "¿Qué te gustaría preguntar?",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Tu mensaje (opcional)") },
                    placeholder = {
                        Text(
                            if (reservaId != null)
                                "Tengo una pregunta sobre mi reserva..."
                            else
                                "Hola, me interesa conocer más sobre..."
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nota: Puedes iniciar la conversación sin mensaje y escribir después.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCrearConversacion(mensaje) },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Iniciar Chat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


// Funciones de utilidad
private fun formatFecha(fechaString: String): String {
    return try {
        val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(fechaString)
        val now = Date()
        val diffInMillis = now.time - (fecha?.time ?: 0)
        val diffInHours = diffInMillis / (1000 * 60 * 60)

        when {
            diffInHours < 1 -> "Hace un momento"
            diffInHours < 24 -> "${diffInHours}h"
            diffInHours < 48 -> "Ayer"
            else -> {
                val diffInDays = diffInHours / 24
                "${diffInDays}d"
            }
        }
    } catch (e: Exception) {
        "Reciente"
    }
}

private fun formatHora(fechaString: String): String {
    return try {
        val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(fechaString)
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha ?: Date())
    } catch (e: Exception) {
        "00:00"
    }
}