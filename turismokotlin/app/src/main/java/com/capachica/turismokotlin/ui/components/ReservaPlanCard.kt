package com.capachica.turismokotlin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capachica.turismokotlin.data.model.EstadoServicio
import com.capachica.turismokotlin.data.model.ReservaPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaPlanCard(
    reserva: ReservaPlan,
    onConfirmarClick: () -> Unit,
    onCompletarClick: () -> Unit,
    isOperating: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
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
                        text = reserva.planNombre,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${reserva.usuario.nombre} ${reserva.usuario.apellido}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = { },
                    label = { Text(getEstadoReservaText(reserva.estado)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = getEstadoReservaColor(reserva.estado)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del plan
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ReservaInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Inicio",
                    value = formatFecha(reserva.fechaInicio)
                )
                ReservaInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Fin",
                    value = formatFecha(reserva.fechaFin)
                )
                ReservaInfoRow(
                    icon = Icons.Default.People,
                    label = "Personas",
                    value = "${reserva.cantidad}"
                )
                ReservaInfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Total",
                    value = "S/ ${reserva.montoFinal}"
                )
                ReservaInfoRow(
                    icon = Icons.Default.Payment,
                    label = "Método",
                    value = reserva.metodoPago.name
                )
            }

            // Contacto de emergencia
            if (!reserva.contactoEmergencia.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Contacto emergencia: ${reserva.contactoEmergencia}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!reserva.telefonoEmergencia.isNullOrBlank()) {
                    Text(
                        text = "Tel: ${reserva.telefonoEmergencia}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
            if (reserva.estado == EstadoServicio.ACTIVO || reserva.estado == EstadoServicio.ACTIVO) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (reserva.estado == EstadoServicio.ACTIVO) {
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

                    if (reserva.estado == EstadoServicio.ACTIVO) {
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

private fun getEstadoReservaText(estado: EstadoServicio): String {
    return when (estado) {
        EstadoServicio.ACTIVO -> "Pendiente"
        EstadoServicio.MANTENIMIENTO -> "Confirmada"
        EstadoServicio.INACTIVO -> "Completada"
        else -> estado.name
    }
}

@Composable
private fun getEstadoReservaColor(estado: EstadoServicio): androidx.compose.ui.graphics.Color {
    return when (estado) {
        EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.secondaryContainer
        EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.primaryContainer
        EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}