package com.tecsup.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.ui.theme.AmbarOrigen
import com.tecsup.avisoscampus.ui.theme.AzulOrigen
import com.tecsup.avisoscampus.ui.theme.FondoFila
import com.tecsup.avisoscampus.ui.theme.MoradoOrigen
import com.tecsup.avisoscampus.ui.theme.TextoSecundario

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    Tarjeta("Historial") {
        if (avisos.isEmpty()) {
            Text(
                text = "Sin avisos todavía",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        } else {
            avisos.take(20).forEach { aviso ->
                FilaAviso(aviso)
            }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoFila, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EtiquetaOrigen(aviso.origen)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = aviso.titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = aviso.texto,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
        Text(
            text = aviso.hora,
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
    }
}

@Composable
private fun EtiquetaOrigen(origen: Origen) {
    val (fondo, color) = when (origen) {
        Origen.BOTON -> AzulOrigen to Color.White
        Origen.RECORDATORIO -> AmbarOrigen to Color.Black
        Origen.MODO_AVION -> MoradoOrigen to Color.White
    }
    Text(
        text = origen.texto,
        color = color,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
