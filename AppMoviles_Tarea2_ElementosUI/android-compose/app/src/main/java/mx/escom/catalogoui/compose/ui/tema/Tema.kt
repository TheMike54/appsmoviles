package mx.escom.catalogoui.compose.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Tema Material 3 con los colores base (morado #6750A4), igual que la versión de Views.
 * Se elige el esquema claro u oscuro según el modo del sistema.
 */
@Composable
fun CatalogoTema(oscuro: Boolean = isSystemInDarkTheme(), contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) darkColorScheme() else lightColorScheme(),
        content = contenido,
    )
}
