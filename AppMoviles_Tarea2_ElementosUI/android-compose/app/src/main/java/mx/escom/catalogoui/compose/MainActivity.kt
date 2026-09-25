package mx.escom.catalogoui.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import mx.escom.catalogoui.compose.navegacion.AppCatalogo
import mx.escom.catalogoui.compose.ui.tema.CatalogoTema

/**
 * Única Activity de la app. Todo lo que se ve se dibuja con funciones composable;
 * la navegación entre Inicio y las seis secciones la maneja Navigation Compose.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Toda la interfaz va en español aunque el teléfono esté en otro idioma; así también
        // los componentes del sistema (calendario, reloj) salen en español.
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es-MX"))
            return // La Activity se vuelve a crear ya con el idioma aplicado.
        }
        setContent {
            CatalogoTema {
                AppCatalogo()
            }
        }
    }
}
