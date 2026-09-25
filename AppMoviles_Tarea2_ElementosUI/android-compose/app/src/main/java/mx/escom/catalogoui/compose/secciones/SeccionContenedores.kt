package mx.escom.catalogoui.compose.secciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.ElementoCard
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.PantallaSeccion
import mx.escom.catalogoui.compose.comun.Resultado
import mx.escom.catalogoui.compose.comun.Subtitulo

/** Sección 6: contenedores y estructura de pantalla. */
@Composable
fun SeccionContenedores() {
    PantallaSeccion {
        Distribuciones()
        ContenedorDesplazable()
        BarraSuperiorDemo()
        BarraInferiorDemo()
        PesosYAlineacion()
    }
}

/** Marco punteado que muestra el área de un contenedor de ejemplo. */
@Composable
private fun Modifier.zona(): Modifier = this
    .clip(RoundedCornerShape(8.dp))
    .background(MaterialTheme.colorScheme.surfaceContainerLow)
    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(8.dp))

@Composable
private fun Caja(texto: String, color: Color, colorTexto: Color, lado: Int = 36, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(lado.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) { Text(texto, color = colorTexto, fontWeight = FontWeight.Bold) }
}

// S6-01 Fila, columna y superpuesta
@Composable
private fun Distribuciones() {
    var cajas by rememberSaveable { mutableIntStateOf(3) }
    // Orden de dibujo de las capas: la última de la lista queda al frente.
    val capas = remember { mutableStateListOf("A", "B", "C") }
    val colores = mapOf(
        "A" to (MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary),
        "B" to (MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary),
        "C" to (MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary),
    )
    val posiciones = mapOf("A" to (16 to 12), "B" to (56 to 32), "C" to (96 to 52))

    ElementoCard(R.string.s6_distribucion_titulo, R.string.s6_distribucion_desc) {
        Subtitulo(stringResource(R.string.s6_fila))
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .zona()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(cajas) { Caja("${it + 1}", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary) }
        }
        Subtitulo(stringResource(R.string.s6_columna))
        Column(
            Modifier
                .width(120.dp)
                .zona()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(cajas) { Caja("${it + 1}", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary) }
        }
        Row(Modifier.padding(top = Espacios.chico), horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            FilledTonalButton(onClick = { if (cajas < 6) cajas++ }) { Text(stringResource(R.string.s6_agregar_caja)) }
            TextButton(onClick = { if (cajas > 1) cajas-- }) { Text(stringResource(R.string.s6_quitar_caja)) }
        }
        Subtitulo(stringResource(R.string.s6_superpuesta))
        Box(
            Modifier
                .fillMaxWidth()
                .height(130.dp)
                .zona(),
        ) {
            capas.forEachIndexed { orden, nombre ->
                val (x, y) = posiciones.getValue(nombre)
                val (fondo, texto) = colores.getValue(nombre)
                Caja(
                    nombre,
                    fondo,
                    texto,
                    lado = 56,
                    modifier = Modifier
                        .offset(x.dp, y.dp)
                        .zIndex(orden.toFloat()),
                )
            }
        }
        FilledTonalButton(
            onClick = { capas.add(capas.removeAt(0)) },
            modifier = Modifier.padding(top = Espacios.chico),
        ) { Text(stringResource(R.string.s6_rotar_capas)) }
        Resultado(stringResource(R.string.s6_distribucion_estado, cajas, capas.last()))
    }
}

// S6-02 Contenedor con desplazamiento vertical
@Composable
private fun ContenedorDesplazable() {
    val desplazamiento = rememberScrollState()
    val alturaRenglon = 28.dp
    val alturaPx = with(LocalDensity.current) { alturaRenglon.toPx() }

    ElementoCard(R.string.s6_scroll_titulo, R.string.s6_scroll_desc) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .zona()
                .verticalScroll(desplazamiento)
                .padding(Espacios.chico),
        ) {
            repeat(30) { i ->
                Box(Modifier.height(alturaRenglon), contentAlignment = Alignment.CenterStart) {
                    Text(stringResource(R.string.s6_renglon, i + 1))
                }
            }
        }
        Resultado(
            stringResource(
                R.string.s6_scroll_estado,
                desplazamiento.value,
                (desplazamiento.value / alturaPx).toInt() + 1,
            ),
        )
    }
}

// S6-03 Barra superior con título y acciones
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraSuperiorDemo() {
    var favorito by rememberSaveable { mutableStateOf(false) }
    var menuAbierto by remember { mutableStateOf(false) }
    var accion by rememberSaveable { mutableStateOf<String?>(null) }
    val textoInicio = stringResource(R.string.s6_accion_inicio)
    val textoBuscar = stringResource(R.string.s6_accion_buscar)
    val textoFavorito = stringResource(R.string.s6_accion_favorito)
    val textoAjustes = stringResource(R.string.s6_accion_ajustes)
    val textoAyuda = stringResource(R.string.s6_accion_ayuda)

    ElementoCard(R.string.s6_toolbar_titulo, R.string.s6_toolbar_desc) {
        TopAppBar(
            title = {
                Column {
                    Text(stringResource(R.string.s6_toolbar_nombre))
                    Text(stringResource(R.string.s6_toolbar_subtitulo), style = MaterialTheme.typography.bodySmall)
                }
            },
            navigationIcon = {
                IconButton(onClick = { accion = textoInicio }) { Icon(painterResource(R.drawable.ic_home), contentDescription = textoInicio) }
            },
            actions = {
                IconButton(onClick = { accion = textoBuscar }) { Icon(painterResource(R.drawable.ic_search), contentDescription = textoBuscar) }
                IconButton(onClick = { favorito = !favorito; accion = textoFavorito }) {
                    Icon(
                        painterResource(if (favorito) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                        contentDescription = textoFavorito,
                    )
                }
                Box {
                    IconButton(onClick = { menuAbierto = true }) { Icon(painterResource(R.drawable.ic_more_vert), contentDescription = null) }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        DropdownMenuItem(text = { Text(textoAjustes) }, onClick = { accion = textoAjustes; menuAbierto = false })
                        DropdownMenuItem(text = { Text(textoAyuda) }, onClick = { accion = textoAyuda; menuAbierto = false })
                    }
                }
            },
            // Dentro de una tarjeta no debe reservar espacio para la barra de estado.
            windowInsets = WindowInsets(0),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        )
        Resultado(
            accion?.let { stringResource(R.string.s6_toolbar_accion, it) } ?: stringResource(R.string.s6_toolbar_instruccion),
        )
    }
}

// S6-04 Barra de navegación inferior
@Composable
private fun BarraInferiorDemo() {
    val destinos = listOf(
        R.string.s6_destino_inicio to R.drawable.ic_home,
        R.string.s6_destino_buscar to R.drawable.ic_search,
        R.string.s6_destino_perfil to R.drawable.ic_person,
    )
    var elegido by rememberSaveable { mutableIntStateOf(0) }

    ElementoCard(R.string.s6_bottomnav_titulo, R.string.s6_bottomnav_desc) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .zona(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painterResource(destinos[elegido].second), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    stringResource(R.string.s6_bottomnav_contenido, stringResource(destinos[elegido].first)),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        NavigationBar(windowInsets = WindowInsets(0)) {
            destinos.forEachIndexed { i, (texto, icono) ->
                NavigationBarItem(
                    selected = i == elegido,
                    onClick = { elegido = i },
                    icon = {
                        // Ejemplo de badge en la barra inferior.
                        if (i == 2) {
                            BadgedBox(badge = { Badge { Text("2") } }) { Icon(painterResource(icono), contentDescription = null) }
                        } else {
                            Icon(painterResource(icono), contentDescription = null)
                        }
                    },
                    label = { Text(stringResource(texto)) },
                )
            }
        }
    }
}

// S6-05 Pesos proporcionales y alineación relativa
@Composable
private fun PesosYAlineacion() {
    var peso by rememberSaveable { mutableFloatStateOf(1f) }
    var sesgo by rememberSaveable { mutableFloatStateOf(0.5f) }

    ElementoCard(R.string.s6_pesos_titulo, R.string.s6_pesos_desc) {
        Subtitulo(stringResource(R.string.s6_pesos_estado, peso.toInt()))
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text("A · 1", color = MaterialTheme.colorScheme.onPrimaryContainer) }
            Box(
                Modifier
                    .weight(peso)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text("B · ${peso.toInt()}", color = MaterialTheme.colorScheme.onTertiaryContainer) }
        }
        Slider(value = peso, onValueChange = { peso = it }, valueRange = 1f..4f, steps = 2)

        Subtitulo(stringResource(R.string.s6_restricciones_estado, sesgo))
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .zona(),
        ) {
            // BiasAlignment: -1 = izquierda, 1 = derecha. El slider va de 0 a 1 como en Views.
            Row(
                Modifier.align(BiasAlignment(horizontalBias = sesgo * 2 - 1, verticalBias = 0f)),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) { Text(stringResource(R.string.s6_caja), color = MaterialTheme.colorScheme.onPrimary) }
                Text(
                    stringResource(R.string.s6_etiqueta_atada),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = Espacios.chico),
                )
            }
        }
        Slider(value = sesgo, onValueChange = { sesgo = it }, valueRange = 0f..1f, steps = 9)
    }
}
