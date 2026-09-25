package mx.escom.catalogoui.compose.secciones.listas

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.CatalogoViewModel
import mx.escom.catalogoui.compose.comun.ElementoCard
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.ItemCatalogo
import mx.escom.catalogoui.compose.comun.LocalSnackbar

/**
 * Sección 4: listas y colecciones. Una fila de pestañas enlazada a un HorizontalPager
 * reparte los elementos en tres páginas.
 */
@Composable
fun SeccionListas(catalogo: CatalogoViewModel) {
    val pestanas = listOf(
        Triple(R.string.s4_tab_lista, R.drawable.ic_list, 0),
        Triple(R.string.s4_tab_cuadricula, R.drawable.ic_dashboard, 1),
        Triple(R.string.s4_tab_secciones, R.drawable.ic_info, 2),
    )
    val estadoPager = rememberPagerState { pestanas.size }
    val alcance = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        // S4-01 Pestañas con contenido deslizable
        ElementoCard(
            R.string.s4_pestanas_titulo,
            R.string.s4_pestanas_desc,
            modifier = Modifier.padding(horizontal = Espacios.pantalla, vertical = Espacios.chico),
        )
        PrimaryTabRow(selectedTabIndex = estadoPager.currentPage) {
            pestanas.forEach { (titulo, icono, indice) ->
                Tab(
                    selected = estadoPager.currentPage == indice,
                    onClick = { alcance.launch { estadoPager.animateScrollToPage(indice) } },
                    text = { Text(stringResource(titulo)) },
                    icon = { Icon(painterResource(icono), contentDescription = null) },
                )
            }
        }
        // En la pestaña Lista el gesto lateral es para borrar filas, así que ahí el pager
        // no se desliza con el dedo; se cambia de pestaña tocando su título.
        HorizontalPager(
            state = estadoPager,
            userScrollEnabled = estadoPager.currentPage != 0,
            modifier = Modifier.weight(1f),
        ) { pagina ->
            when (pagina) {
                0 -> PaginaLista(catalogo)
                1 -> PaginaCuadricula()
                else -> PaginaSecciones(catalogo)
            }
        }
    }
}

/** Tarjeta de documentación que se pliega para dejar espacio a la colección. */
@Composable
private fun DocPlegable(@StringRes cuerpo: Int, abiertaAlInicio: Boolean = false) {
    var abierta by rememberSaveable { mutableStateOf(abiertaAlInicio) }
    Card(
        onClick = { abierta = !abierta },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        modifier = Modifier
            .fillMaxWidth()
            .padding(Espacios.chico),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    stringResource(R.string.s4_doc_encabezado),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = Espacios.chico),
                )
            }
            if (abierta) {
                Text(
                    AnnotatedString.fromHtml(stringResource(cuerpo)),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = Espacios.chico),
                )
            }
        }
    }
}

/** Fila de lista con el círculo inicial, nombre y categoría. */
@Composable
private fun FilaElemento(item: ItemCatalogo, alTocar: () -> Unit) {
    ListItem(
        headlineContent = { Text(item.nombre) },
        supportingContent = {
            Text(if (item.desdeEntrada) stringResource(R.string.s4_desde_entrada, "S1 → S4") else item.categoria)
        },
        leadingContent = {
            Box(
                Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (item.desdeEntrada) item.nombre.first().uppercase() else item.nombre.substringAfterLast(' '),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        modifier = Modifier.clickable(onClick = alTocar),
    )
}

// Pestaña «Lista»: S4-02 a S4-06
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaginaLista(catalogo: CatalogoViewModel) {
    val items = catalogo.items
    val snackbar = LocalSnackbar.current
    val alcance = rememberCoroutineScope()
    val estadoLista = rememberLazyListState()
    var actualizando by remember { mutableStateOf(false) }
    var detalle by remember { mutableStateOf<ItemCatalogo?>(null) }
    val textoDeshacer = stringResource(R.string.s4_deshacer)
    val formatoEliminado = stringResource(R.string.s4_eliminado)
    val formatoActualizado = stringResource(R.string.s4_actualizado)

    // S4-05 Arrastrar para actualizar: espera 1.5 s y agrega un elemento nuevo.
    LaunchedEffect(actualizando) {
        if (actualizando) {
            delay(1500)
            val nuevo = catalogo.agregarNuevo()
            actualizando = false
            estadoLista.animateScrollToItem(0)
            snackbar.showSnackbar(formatoActualizado.format(nuevo.nombre), duration = SnackbarDuration.Short)
        }
    }

    Column(Modifier.fillMaxSize()) {
        DocPlegable(R.string.s4_doc_lista)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Espacios.chico),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (items.isEmpty()) stringResource(R.string.s4_total_vacio) else stringResource(R.string.s4_total, items.size),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { catalogo.vaciar() }, enabled = items.isNotEmpty()) {
                Icon(painterResource(R.drawable.ic_delete), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.s4_vaciar), modifier = Modifier.padding(start = 4.dp))
            }
        }

        if (items.isEmpty()) {
            // S4-06 Estado vacío
            EstadoVacio(alRestaurar = { catalogo.restaurar() })
        } else {
            PullToRefreshBox(
                isRefreshing = actualizando,
                onRefresh = { actualizando = true },
                modifier = Modifier.weight(1f),
            ) {
                // S4-02 Lista vertical
                LazyColumn(state = estadoLista, modifier = Modifier.fillMaxSize()) {
                    items(items, key = { it.id }) { item ->
                        // S4-04 Deslizar para eliminar
                        val estado = rememberSwipeToDismissBoxState()
                        LaunchedEffect(estado.currentValue) {
                            if (estado.currentValue != SwipeToDismissBoxValue.Settled) {
                                val posicion = items.indexOf(item)
                                catalogo.eliminar(item)
                                // El aviso se lanza en el alcance de la lista: la fila ya no existe.
                                alcance.launch {
                                    val r = snackbar.showSnackbar(formatoEliminado.format(item.nombre), actionLabel = textoDeshacer, duration = SnackbarDuration.Long)
                                    if (r == SnackbarResult.ActionPerformed) catalogo.agregar(item, posicion)
                                }
                            }
                        }
                        SwipeToDismissBox(
                            state = estado,
                            backgroundContent = {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(horizontal = Espacios.pantalla),
                                    contentAlignment = Alignment.CenterEnd,
                                ) {
                                    Icon(painterResource(R.drawable.ic_delete), contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            },
                        ) {
                            // S4-03 Detalle al seleccionar
                            FilaElemento(item, alTocar = { detalle = item })
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    detalle?.let { item ->
        val nota = stringResource(if (item.desdeEntrada) R.string.s4_detalle_entrada else R.string.s4_detalle_normal)
        AlertDialog(
            onDismissRequest = { detalle = null },
            icon = { Icon(painterResource(R.drawable.ic_info), contentDescription = null) },
            title = { Text(item.nombre) },
            text = { Text(stringResource(R.string.s4_detalle_mensaje, item.categoria, item.id, nota)) },
            confirmButton = { TextButton(onClick = { detalle = null }) { Text(stringResource(R.string.s4_cerrar)) } },
        )
    }
}

@Composable
private fun EstadoVacio(alRestaurar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(Espacios.pantalla),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painterResource(R.drawable.ic_inbox),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(120.dp),
        )
        Text(stringResource(R.string.s4_vacio_titulo), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = Espacios.interno))
        Text(
            stringResource(R.string.s4_vacio_mensaje),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Button(onClick = alRestaurar, modifier = Modifier.padding(top = Espacios.interno)) {
            Text(stringResource(R.string.s4_restaurar))
        }
    }
}

// Pestaña «Cuadrícula»: S4-07
@Composable
private fun PaginaCuadricula() {
    val total = 12
    val seleccionadas = remember { mutableStateListOf<Int>() }

    Column(Modifier.fillMaxSize()) {
        DocPlegable(R.string.s4_doc_cuadricula, abiertaAlInicio = true)
        Text(
            stringResource(R.string.s4_cuadricula_estado, seleccionadas.size, total),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = Espacios.pantalla, vertical = 4.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(Espacios.chico),
            horizontalArrangement = Arrangement.spacedBy(Espacios.chico),
            verticalArrangement = Arrangement.spacedBy(Espacios.chico),
        ) {
            itemsIndexed((1..total).toList()) { _, numero ->
                val elegida = numero in seleccionadas
                OutlinedCard(
                    onClick = { if (elegida) seleccionadas.remove(numero) else seleccionadas.add(numero) },
                    colors = if (elegida) {
                        CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    } else {
                        CardDefaults.outlinedCardColors()
                    },
                    border = if (elegida) {
                        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        CardDefaults.outlinedCardBorder()
                    },
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = Espacios.interno),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("$numero", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Text(stringResource(R.string.s4_cuadricula_etiqueta, numero), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// Pestaña «Secciones»: S4-08 (encabezados fijos + elementos)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PaginaSecciones(catalogo: CatalogoViewModel) {
    val grupos = catalogo.items.groupBy { it.categoria }.toSortedMap()
    var detalle by remember { mutableStateOf<ItemCatalogo?>(null) }

    Column(Modifier.fillMaxSize()) {
        DocPlegable(R.string.s4_doc_secciones, abiertaAlInicio = true)
        Text(
            stringResource(R.string.s4_secciones_estado, grupos.size, catalogo.items.size),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = Espacios.pantalla, vertical = 4.dp),
        )
        LazyColumn(Modifier.fillMaxSize()) {
            grupos.forEach { (categoria, elementos) ->
                stickyHeader(key = "encabezado_$categoria") {
                    Text(
                        "$categoria (${elementos.size})",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = Espacios.pantalla, vertical = Espacios.chico),
                    )
                }
                items(elementos, key = { it.id }) { item -> FilaElemento(item, alTocar = { detalle = item }) }
            }
        }
    }

    detalle?.let { item ->
        AlertDialog(
            onDismissRequest = { detalle = null },
            title = { Text(item.nombre) },
            text = { Text(item.categoria) },
            confirmButton = { TextButton(onClick = { detalle = null }) { Text(stringResource(R.string.s4_cerrar)) } },
        )
    }
}
