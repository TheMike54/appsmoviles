package mx.escom.catalogoui.compose.secciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.ElementoCard
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.PantallaSeccion
import mx.escom.catalogoui.compose.comun.Resultado

/** Sección 2: botones y acciones. Cada botón produce una respuesta visible. */
@Composable
fun SeccionBotones() {
    PantallaSeccion {
        BotonesBasicos()
        BotonesIcono()
        BotonesFlotantes()
        SelectorSegmentado()
        BotonDeshabilitado()
        BotonCarga()
    }
}

// S2-01 Relleno, contorno y texto
@Composable
private fun BotonesBasicos() {
    var relleno by rememberSaveable { mutableIntStateOf(0) }
    var contorno by rememberSaveable { mutableIntStateOf(0) }
    var texto by rememberSaveable { mutableIntStateOf(0) }

    ElementoCard(R.string.s2_basicos_titulo, R.string.s2_basicos_desc) {
        Row(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            Button(onClick = { relleno++ }) { Text(stringResource(R.string.s2_relleno)) }
            OutlinedButton(onClick = { contorno++ }) { Text(stringResource(R.string.s2_contorno)) }
            TextButton(onClick = { texto++ }) { Text(stringResource(R.string.s2_texto)) }
        }
        Resultado(stringResource(R.string.s2_basicos_contador, relleno, contorno, texto))
    }
}

// S2-02 Botones con ícono
@Composable
private fun BotonesIcono() {
    var favorito by rememberSaveable { mutableStateOf(false) }
    var compartido by rememberSaveable { mutableIntStateOf(0) }
    var mensaje by rememberSaveable { mutableStateOf<String?>(null) }
    val textoSi = stringResource(R.string.s2_favorito_si)
    val textoNo = stringResource(R.string.s2_favorito_no)

    ElementoCard(R.string.s2_icono_titulo, R.string.s2_icono_desc) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconToggleButton(checked = favorito, onCheckedChange = {
                favorito = it
                mensaje = if (it) textoSi else textoNo
            }) {
                Icon(
                    painterResource(if (favorito) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                    contentDescription = stringResource(R.string.s2_favorito),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.size(Espacios.interno))
            FilledTonalButton(
                onClick = { compartido++; mensaje = null },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(painterResource(R.drawable.ic_share), contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.s2_compartir))
            }
        }
        Resultado(
            mensaje ?: if (compartido > 0) stringResource(R.string.s2_compartido, compartido) else stringResource(R.string.toca_para_probar),
        )
    }
}

// S2-03 Botones de acción flotantes
@Composable
private fun BotonesFlotantes() {
    var toques by rememberSaveable { mutableIntStateOf(0) }
    var extendido by rememberSaveable { mutableStateOf(true) }
    var mensaje by rememberSaveable { mutableStateOf<String?>(null) }
    val textoEncogido = stringResource(R.string.s2_fab_encogido)
    val textoExpandido = stringResource(R.string.s2_fab_expandido)

    ElementoCard(R.string.s2_fab_titulo, R.string.s2_fab_desc) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Espacios.interno)) {
            FloatingActionButton(onClick = { toques++; mensaje = null }) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.s2_fab_normal))
            }
            ExtendedFloatingActionButton(
                onClick = {
                    extendido = !extendido
                    mensaje = if (extendido) textoExpandido else textoEncogido
                },
                expanded = extendido,
                icon = { Icon(painterResource(R.drawable.ic_edit), contentDescription = null) },
                text = { Text(stringResource(R.string.s2_fab_extendido)) },
            )
        }
        Resultado(mensaje ?: if (toques > 0) stringResource(R.string.s2_fab_contador, toques) else stringResource(R.string.toca_para_probar))
    }
}

// S2-04 Selector segmentado
@Composable
private fun SelectorSegmentado() {
    val opciones = listOf(R.string.s2_dia, R.string.s2_semana, R.string.s2_mes)
    var elegido by rememberSaveable { mutableIntStateOf(0) }

    ElementoCard(R.string.s2_toggle_titulo, R.string.s2_toggle_desc) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            opciones.forEachIndexed { indice, opcion ->
                SegmentedButton(
                    selected = indice == elegido,
                    onClick = { elegido = indice },
                    shape = SegmentedButtonDefaults.itemShape(indice, opciones.size),
                ) { Text(stringResource(opcion)) }
            }
        }
        Resultado(stringResource(R.string.s2_toggle_elegido, stringResource(opciones[elegido])))
    }
}

// S2-05 Botón deshabilitado
@Composable
private fun BotonDeshabilitado() {
    var aceptado by rememberSaveable { mutableStateOf(false) }
    var continuo by rememberSaveable { mutableStateOf(false) }

    ElementoCard(R.string.s2_deshabilitado_titulo, R.string.s2_deshabilitado_desc) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.s2_aceptar_terminos), modifier = Modifier.weight(1f))
            Switch(checked = aceptado, onCheckedChange = { aceptado = it; continuo = false })
        }
        Button(onClick = { continuo = true }, enabled = aceptado, modifier = Modifier.padding(top = Espacios.chico)) {
            Text(stringResource(R.string.s2_continuar))
        }
        Resultado(
            stringResource(
                when {
                    continuo -> R.string.s2_continuaste
                    aceptado -> R.string.s2_deshabilitado_estado_on
                    else -> R.string.s2_deshabilitado_estado_off
                },
            ),
        )
    }
}

// S2-06 Botón en estado de carga
@Composable
private fun BotonCarga() {
    var cargando by rememberSaveable { mutableStateOf(false) }
    var listo by rememberSaveable { mutableStateOf(false) }

    // Simula una descarga de 2 segundos mientras el estado sea "cargando".
    LaunchedEffect(cargando) {
        if (cargando) {
            delay(2000)
            cargando = false
            listo = true
        }
    }

    ElementoCard(R.string.s2_carga_titulo, R.string.s2_carga_desc) {
        Button(
            onClick = { cargando = true; listo = false },
            enabled = !cargando,
            modifier = Modifier.widthIn(min = 160.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.s2_descargar))
                }
            }
        }
        Resultado(
            stringResource(
                when {
                    cargando -> R.string.s2_cargando
                    listo -> R.string.s2_listo
                    else -> R.string.toca_para_probar
                },
            ),
        )
    }
}
