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
import com.capachica.turismokotlin.data.model.ReservaCarrito
import com.capachica.turismokotlin.data.model.EstadoReserva
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaServicioCard(
    reserva: ReservaCarrito,
    onConfirmarClick: () -> Unit,
    onCompletarClick: () -> Unit,
    isOperating: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con código y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Reserva ${reserva.codigoReserva}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${reserva.usuario.nombre} ${reserva.usuario.apellido}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = { },
                    label = { Text(getEstadoText(reserva.estado)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = getEstadoColor(reserva.estado)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información de la reserva
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ReservaInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Fecha",
                    value = formatFecha(reserva.fechaReserva)
                )
                ReservaInfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Total",
                    value = "S/ ${reserva.montoFinal}"
                )
                ReservaInfoRow(
                    icon = Icons.Default.Payment,
                    label = "Método de pago",
                    value = reserva.metodoPago.name
                )
            }

            // Servicios de la reserva
            if (reserva.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Servicios:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                reserva.items.forEach { item ->
                    Text(
                        text = "• ${item.servicio.nombre} (${item.cantidad}x)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            // Observaciones
            if (!reserva.observaciones.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Observaciones: ${reserva.observaciones}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botones de acción
            if (reserva.estado == EstadoReserva.PENDIENTE || reserva.estado == EstadoReserva.CONFIRMADA) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (reserva.estado == EstadoReserva.PENDIENTE) {
                        Button(
                            onClick = onConfirmarClick,
                            enabled = !isOperating,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isOperating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Confirmar")
                            }
                        }
                    }

                    if (reserva.estado == EstadoReserva.CONFIRMADA) {
                        Button(
                            onClick = onCompletarClick,
                            enabled = !isOperating,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isOperating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.Default.TaskAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Completar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getEstadoText(estado: EstadoReserva): String {
    return when (estado) {
        EstadoReserva.PENDIENTE -> "Pendiente"
        EstadoReserva.CONFIRMADA -> "Confirmada"
        EstadoReserva.COMPLETADA -> "Completada"
        EstadoReserva.CANCELADA -> "Cancelada"
    }
}

@Composable
private fun getEstadoColor(estado: EstadoReserva): androidx.compose.ui.graphics.Color {
    return when (estado) {
        EstadoReserva.PENDIENTE -> MaterialTheme.colorScheme.secondaryContainer
        EstadoReserva.CONFIRMADA -> MaterialTheme.colorScheme.primaryContainer
        EstadoReserva.COMPLETADA -> MaterialTheme.colorScheme.tertiaryContainer
        EstadoReserva.CANCELADA -> MaterialTheme.colorScheme.errorContainer
    }
}

fun formatFecha(fechaString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
        val fecha = inputFormat.parse(fechaString)
        outputFormat.format(fecha ?: Date())
    } catch (e: Exception) {
        fechaString
    }
}