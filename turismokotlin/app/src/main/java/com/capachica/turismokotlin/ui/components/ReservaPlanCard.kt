package com.capachica.turismokotlin.ui.components
import com.capachica.turismokotlin.ui.components.formatFecha as formatFechaPlan

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
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
import com.capachica.turismokotlin.data.model.EstadoReserva
import com.capachica.turismokotlin.data.model.ReservaPlan
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaPlanCard(
    reserva: ReservaPlan,
    onConfirmarClick: () -> Unit = {},
    onCompletarClick: () -> Unit = {},
    onCancelarClick: () -> Unit = {},
    isOperating: Boolean = false,
    showActions: Boolean = true
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
                        text = reserva.plan.nombre,
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
            val precioUnitario = reserva.serviciosPersonalizados
                ?.firstOrNull()
                ?.servicioPlan
                ?.servicio
                ?.precio ?: 0.0

            // Información del plan
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ReservaInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Fechas",
                    value = "${formatFechaPlan(reserva.fechaInicio)} - ${formatFechaPlan(reserva.fechaFin)}"
                )

                ReservaInfoRow(
                    icon = Icons.Default.People,
                    label = "Personas",
                    value = "${reserva.numeroPersonas}"
                )
                ReservaInfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Precio unitario",
                    value = "S/ %.2f".format(precioUnitario)
                )
                ReservaInfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Total",
                    value = "S/ ${reserva.montoTotal}"
                )

                // Mostrar descuento si existe
                if (reserva.montoDescuento > 0) {
                    ReservaInfoRow(
                        icon = Icons.Default.AttachMoney,
                        label = "Descuento",
                        value = "- S/ ${reserva.montoDescuento}"
                    )
                    ReservaInfoRow(
                        icon = Icons.Default.AttachMoney,
                        label = "Monto final",
                        value = "S/ ${reserva.montoFinal}",
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                }

                ReservaInfoRow(
                    icon = Icons.Default.Payment,
                    label = "Método de pago",
                    value = getMetodoPagoText(reserva.metodoPago)
                )
            }

            // Fechas importantes
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reservado: ${formatFechaHora(reserva.fechaReserva)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            reserva.fechaConfirmacion?.let {
                Text(
                    text = "Confirmado: ${formatFechaHora(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            reserva.fechaCancelacion?.let {
                Text(
                    text = "Cancelado: ${formatFechaHora(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                reserva.motivoCancelacion?.let { motivo ->
                    Text(
                        text = "Motivo: $motivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
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

            // Solicitudes especiales
            if (!reserva.solicitudesEspeciales.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Solicitudes especiales: ${reserva.solicitudesEspeciales}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val servicios = reserva.serviciosPersonalizados ?: emptyList()

            // Servicios personalizados (si los hay)
            if (servicios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Servicios modificados (${servicios.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                servicios.take(2).forEach { servicio ->
                    Text(
                        text = "• ${servicio.servicioPlan.servicio.nombre} - ${
                            when {
                                !servicio.incluido -> "Excluido"
                                servicio.precioPersonalizado != null -> "Precio: S/ ${servicio.precioPersonalizado}"
                                else -> "Incluido"
                            }
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (servicios.size > 2) {
                    Text(
                        text = "... y ${servicios.size - 2} más",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val pagos = reserva.pagos ?: emptyList()

            // Información de pagos
            if (pagos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pagos (${pagos.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                reserva.pagos.forEach { pago ->
                    Text(
                        text = "• ${pago.tipo.name}: S/ ${pago.monto} - ${pago.estado.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botones de acción
            if (showActions && isActionableState(reserva.estado)) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (reserva.estado) {
                        EstadoReserva.PENDIENTE -> {
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

                            OutlinedButton(
                                onClick = onCancelarClick,
                                enabled = !isOperating,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancelar")
                            }
                        }

                        EstadoReserva.CONFIRMADA -> {
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

                            OutlinedButton(
                                onClick = onCancelarClick,
                                enabled = !isOperating,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancelar")
                            }
                        }

                        else -> { /* No actions for completed/cancelled */ }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservaInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

private fun getEstadoReservaText(estado: EstadoReserva): String {
    return when (estado) {
        EstadoReserva.PENDIENTE -> "Pendiente"
        EstadoReserva.CONFIRMADA -> "Confirmada"
        EstadoReserva.COMPLETADA -> "Completada"
        EstadoReserva.CANCELADA -> "Cancelada"
    }
}

@Composable
private fun getEstadoReservaColor(estado: EstadoReserva): androidx.compose.ui.graphics.Color {
    return when (estado) {
        EstadoReserva.PENDIENTE -> MaterialTheme.colorScheme.secondaryContainer
        EstadoReserva.CONFIRMADA -> MaterialTheme.colorScheme.primaryContainer
        EstadoReserva.COMPLETADA -> MaterialTheme.colorScheme.tertiaryContainer
        EstadoReserva.CANCELADA -> MaterialTheme.colorScheme.errorContainer
    }
}

private fun getMetodoPagoText(metodoPago: com.capachica.turismokotlin.data.model.MetodoPago): String {
    return when (metodoPago) {
        com.capachica.turismokotlin.data.model.MetodoPago.EFECTIVO -> "Efectivo"
        com.capachica.turismokotlin.data.model.MetodoPago.TARJETA -> "Tarjeta"
        com.capachica.turismokotlin.data.model.MetodoPago.TRANSFERENCIA -> "Transferencia"
        com.capachica.turismokotlin.data.model.MetodoPago.YAPE -> "Yape"
        com.capachica.turismokotlin.data.model.MetodoPago.PLIN -> "Plin"
    }
}

private fun isActionableState(estado: EstadoReserva): Boolean {
    return when (estado) {
        EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA -> true
        EstadoReserva.COMPLETADA, EstadoReserva.CANCELADA -> false
    }
}

fun formatFecha(fecha: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(fecha)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        fecha
    }
}

private fun formatFechaHora(fechaHora: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(fechaHora)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        try {
            val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat2.parse(fechaHora)
            outputFormat.format(date ?: Date())
        } catch (e2: Exception) {
            fechaHora
        }
    }
}