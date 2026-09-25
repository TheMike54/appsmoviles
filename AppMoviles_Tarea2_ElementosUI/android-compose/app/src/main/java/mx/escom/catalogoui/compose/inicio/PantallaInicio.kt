package mx.escom.catalogoui.compose.inicio

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.PantallaSeccion
import mx.escom.catalogoui.compose.navegacion.Destino

/** Pantalla principal: explica la app y muestra una tarjeta por sección. */
@Composable
fun PantallaInicio(alElegir: (Destino) -> Unit) {
    PantallaSeccion {
        Text(stringResource(R.string.inicio_titulo), style = MaterialTheme.typography.headlineSmall)
        Text(
            stringResource(R.string.inicio_descripcion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Destino.entries.drop(1).forEachIndexed { indice, destino ->
            ElevatedCard(onClick = { alElegir(destino) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(Espacios.interno), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(destino.icono),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.width(Espacios.interno))
                    androidx.compose.foundation.layout.Column {
                        Text(
                            stringResource(R.string.inicio_formato_seccion, indice + 1, stringResource(destino.titulo)),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            stringResource(destino.descripcion),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
