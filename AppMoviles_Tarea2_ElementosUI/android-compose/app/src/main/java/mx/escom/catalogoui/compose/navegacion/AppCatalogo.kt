package mx.escom.catalogoui.compose.navegacion

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.CatalogoViewModel
import mx.escom.catalogoui.compose.comun.LocalSnackbar
import mx.escom.catalogoui.compose.inicio.PantallaInicio
import mx.escom.catalogoui.compose.secciones.SeccionBotones
import mx.escom.catalogoui.compose.secciones.SeccionContenedores
import mx.escom.catalogoui.compose.secciones.SeccionEntradaTexto
import mx.escom.catalogoui.compose.secciones.SeccionInformacion
import mx.escom.catalogoui.compose.secciones.SeccionSeleccion
import mx.escom.catalogoui.compose.secciones.listas.SeccionListas

/** Destinos de la app: Inicio y las seis secciones (composables de destino). */
enum class Destino(val ruta: String, @StringRes val titulo: Int, @StringRes val descripcion: Int, @DrawableRes val icono: Int) {
    INICIO("inicio", R.string.titulo_inicio, R.string.inicio_descripcion, R.drawable.ic_home),
    ENTRADA("entrada_texto", R.string.titulo_s1, R.string.inicio_desc_s1, R.drawable.ic_edit),
    BOTONES("botones", R.string.titulo_s2, R.string.inicio_desc_s2, R.drawable.ic_touch),
    SELECCION("seleccion", R.string.titulo_s3, R.string.inicio_desc_s3, R.drawable.ic_check_box),
    LISTAS("listas", R.string.titulo_s4, R.string.inicio_desc_s4, R.drawable.ic_list),
    INFORMACION("informacion", R.string.titulo_s5, R.string.inicio_desc_s5, R.drawable.ic_info),
    CONTENEDORES("contenedores", R.string.titulo_s6, R.string.inicio_desc_s6, R.drawable.ic_dashboard),
}

/** Ir a un destino sin apilar pantallas repetidas; Atrás siempre regresa a Inicio. */
fun NavHostController.irA(destino: Destino) {
    if (destino == Destino.INICIO) {
        popBackStack(Destino.INICIO.ruta, inclusive = false)
        return
    }
    navigate(destino.ruta) {
        popUpTo(Destino.INICIO.ruta)
        launchSingleTop = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCatalogo() {
    val navController = rememberNavController()
    val catalogo: CatalogoViewModel = viewModel()
    val snackbar = remember { SnackbarHostState() }
    val estadoMenu = rememberDrawerState(DrawerValue.Closed)
    val alcance = rememberCoroutineScope()

    val entrada by navController.currentBackStackEntryAsState()
    val actual = Destino.entries.firstOrNull { it.ruta == entrada?.destination?.route } ?: Destino.INICIO

    // Si el menú lateral está abierto, Atrás lo cierra en lugar de salir.
    BackHandler(enabled = estadoMenu.isOpen) { alcance.launch { estadoMenu.close() } }

    ModalNavigationDrawer(
        drawerState = estadoMenu,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        stringResource(R.string.drawer_subtitulo),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Destino.entries.forEach { destino ->
                    NavigationDrawerItem(
                        label = { Text(stringResource(destino.titulo)) },
                        icon = { Icon(painterResource(destino.icono), contentDescription = null) },
                        selected = destino == actual,
                        onClick = {
                            navController.irA(destino)
                            alcance.launch { estadoMenu.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(stringResource(actual.titulo))
                            Text(stringResource(R.string.drawer_subtitulo), style = MaterialTheme.typography.titleSmall)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { alcance.launch { estadoMenu.open() } }) {
                            Icon(painterResource(R.drawable.ic_menu), contentDescription = stringResource(R.string.abrir_menu))
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbar) },
        ) { relleno ->
            CompositionLocalProvider(LocalSnackbar provides snackbar) {
                NavHost(
                    navController = navController,
                    startDestination = Destino.INICIO.ruta,
                    // imePadding: el contenido se recorre para que el teclado no tape los campos.
                    modifier = Modifier
                        .padding(relleno)
                        .consumeWindowInsets(relleno)
                        .imePadding(),
                ) {
                    composable(Destino.INICIO.ruta) { PantallaInicio(alElegir = { navController.irA(it) }) }
                    composable(Destino.ENTRADA.ruta) {
                        SeccionEntradaTexto(catalogo, alVerLista = { navController.irA(Destino.LISTAS) })
                    }
                    composable(Destino.BOTONES.ruta) { SeccionBotones() }
                    composable(Destino.SELECCION.ruta) { SeccionSeleccion() }
                    composable(Destino.LISTAS.ruta) { SeccionListas(catalogo) }
                    composable(Destino.INFORMACION.ruta) { SeccionInformacion() }
                    composable(Destino.CONTENEDORES.ruta) { SeccionContenedores() }
                }
            }
        }
    }
}
