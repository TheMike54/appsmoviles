package mx.escom.catalogoui.views

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import mx.escom.catalogoui.views.databinding.ActivityMainBinding

/**
 * Única Activity de la app. Aloja la barra superior, el menú lateral y el
 * contenedor donde Navigation cambia entre el Fragment de inicio y las seis secciones.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Toda la interfaz va en español aunque el teléfono esté en otro idioma; así también
        // los componentes del sistema (calendario, reloj, botones de diálogos) salen en español.
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es-MX"))
            return // La Activity se vuelve a crear ya con el idioma aplicado.
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // Todas las pantallas del menú lateral son de primer nivel: muestran el ícono del menú.
        // El botón Atrás del sistema regresa a Inicio.
        val destinosPrincipales = binding.navView.menu.let { menu ->
            (0 until menu.size()).map { menu.getItem(it).itemId }.toSet()
        }
        appBarConfiguration = AppBarConfiguration(destinosPrincipales, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        supportActionBar?.subtitle = getString(R.string.drawer_subtitulo)

        // Si el menú lateral está abierto, Atrás lo cierra en lugar de salir de la app.
        val cerrarMenu = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() = binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        onBackPressedDispatcher.addCallback(this, cerrarMenu)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) { cerrarMenu.isEnabled = true }
            override fun onDrawerClosed(drawerView: View) { cerrarMenu.isEnabled = false }
        })

        // La app dibuja detrás de las barras del sistema; se deja el espacio que ocupan.
        ViewCompat.setOnApplyWindowInsetsListener(binding.contenidoPrincipal) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            vista.updatePadding(left = barras.left, right = barras.right, bottom = barras.bottom)
            binding.appBar.updatePadding(top = barras.top)
            insets
        }
        val encabezado = binding.navView.getHeaderView(0)
        val paddingOriginal = encabezado.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(encabezado) { vista, insets ->
            vista.updatePadding(top = paddingOriginal + insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
