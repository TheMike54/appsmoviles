package mx.escom.catalogoui.compose.comun

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Snackbar compartido de la app: cualquier sección puede mostrar un mensaje con acción. */
val LocalSnackbar = staticCompositionLocalOf<SnackbarHostState> { error("Sin SnackbarHostState") }

/** Espaciado común para que todas las pantallas se vean igual. */
object Espacios {
    val pantalla = 16.dp
    val tarjetas = 12.dp
    val interno = 16.dp
    val chico = 8.dp
}

/**
 * Pantalla de sección: una columna desplazable con las tarjetas separadas de forma uniforme.
 */
@Composable
fun PantallaSeccion(contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(Espacios.pantalla)),
        verticalArrangement = Arrangement.spacedBy(Espacios.tarjetas),
        content = contenido,
    )
}

/**
 * Tarjeta que documenta un elemento del catálogo: nombre, explicación corta y, debajo,
 * la demostración interactiva que recibe como contenido.
 */
@Composable
fun ElementoCard(
    @StringRes titulo: Int,
    @StringRes descripcion: Int,
    modifier: Modifier = Modifier,
    demo: @Composable ColumnScope.() -> Unit = {},
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Espacios.interno)) {
            Text(
                text = stringResource(titulo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(descripcion),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            demo()
        }
    }
}

/** Texto donde cada demostración muestra la respuesta a lo que hizo el usuario. */
@Composable
fun Resultado(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(top = Espacios.chico),
    )
}

/** Subtítulo pequeño dentro de una demostración. */
@Composable
fun Subtitulo(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Espacios.chico, bottom = 4.dp),
    )
}
