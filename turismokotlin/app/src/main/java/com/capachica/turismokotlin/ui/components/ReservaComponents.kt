package com.capachica.turismokotlin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.capachica.turismokotlin.data.model.EstadoReserva

@Composable
fun EstadoReservaChip(estado: EstadoReserva) {
    val (color, icon) = when (estado) {
        EstadoReserva.PENDIENTE -> MaterialTheme.colorScheme.tertiary to Icons.Default.HourglassEmpty
        EstadoReserva.CONFIRMADA -> MaterialTheme.colorScheme.primary to Icons.Default.CheckCircle
        EstadoReserva.PAGADA -> MaterialTheme.colorScheme.secondary to Icons.Default.Payment
        EstadoReserva.EN_PROCESO -> MaterialTheme.colorScheme.primary to Icons.Default.DirectionsRun
        EstadoReserva.COMPLETADA -> MaterialTheme.colorScheme.tertiary to Icons.Default.Done
        EstadoReserva.CANCELADA -> MaterialTheme.colorScheme.error to Icons.Default.Cancel
        EstadoReserva.NO_SHOW -> MaterialTheme.colorScheme.error to Icons.Default.PersonOff
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = estado.name,
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color,
            leadingIconContentColor = color
        )
    )
}

@Composable
fun InfoRowSmall(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}