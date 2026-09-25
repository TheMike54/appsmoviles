package mx.escom.catalogoui.compose.secciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.ElementoCard
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.PantallaSeccion
import mx.escom.catalogoui.compose.comun.Resultado
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/** Sección 3: elementos para elegir entre opciones. */
@Composable
fun SeccionSeleccion() {
    PantallaSeccion {
        Casillas()
        BotonesOpcion()
        Interruptor()
        Deslizadores()
        ListaDesplegable()
        SelectoresFechaHora()
        ChipsFiltro()
    }
}

// S3-01 Casillas con estado indeterminado
@Composable
private fun Casillas() {
    val nombres = listOf(R.string.s3_queso, R.string.s3_champinones, R.string.s3_pimiento)
    val marcadas = remember { mutableStateListOf(true, false, false) }
    val estadoPadre = when (marcadas.count { it }) {
        0 -> ToggleableState.Off
        marcadas.size -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }

    ElementoCard(R.string.s3_checkbox_titulo, R.string.s3_checkbox_desc) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .triStateToggleable(state = estadoPadre, role = Role.Checkbox, onClick = {
                    // Tocar «Todos» marca o desmarca a todas las hijas.
                    val marcar = estadoPadre != ToggleableState.On
                    for (i in marcadas.indices) marcadas[i] = marcar
                }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TriStateCheckbox(state = estadoPadre, onClick = null)
            Text(stringResource(R.string.s3_todos), modifier = Modifier.padding(start = Espacios.chico))
        }
        nombres.forEachIndexed { i, nombre ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
                    .height(48.dp)
                    .toggleable(value = marcadas[i], role = Role.Checkbox, onValueChange = { marcadas[i] = it }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = marcadas[i], onCheckedChange = null)
                Text(stringResource(nombre), modifier = Modifier.padding(start = Espacios.chico))
            }
        }
        val estado = stringResource(
            when (estadoPadre) {
                ToggleableState.On -> R.string.s3_estado_marcado
                ToggleableState.Indeterminate -> R.string.s3_estado_indeterminado
                ToggleableState.Off -> R.string.s3_estado_desmarcado
            },
        )
        Resultado(stringResource(R.string.s3_checkbox_estado, marcadas.count { it }, estado))
    }
}

// S3-02 Botones de opción
@Composable
private fun BotonesOpcion() {
    val opciones = listOf(R.string.s3_chica, R.string.s3_mediana, R.string.s3_grande)
    var elegida by rememberSaveable { mutableIntStateOf(-1) }

    ElementoCard(R.string.s3_radio_titulo, R.string.s3_radio_desc) {
        Column(Modifier.selectableGroup()) {
            opciones.forEachIndexed { i, opcion ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(selected = i == elegida, onClick = { elegida = i }, role = Role.RadioButton),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = i == elegida, onClick = null)
                    Text(stringResource(opcion), modifier = Modifier.padding(start = Espacios.chico))
                }
            }
        }
        Resultado(
            if (elegida < 0) {
                stringResource(R.string.s3_radio_ninguno)
            } else {
                stringResource(R.string.s3_radio_elegido, stringResource(opciones[elegida]))
            },
        )
    }
}

// S3-03 Interruptor
@Composable
private fun Interruptor() {
    var activo by rememberSaveable { mutableStateOf(false) }

    ElementoCard(R.string.s3_switch_titulo, R.string.s3_switch_desc) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.s3_notificaciones), modifier = Modifier.weight(1f))
            Switch(checked = activo, onCheckedChange = { activo = it })
        }
        Resultado(stringResource(if (activo) R.string.s3_switch_on else R.string.s3_switch_off))
    }
}

// S3-04 Deslizadores
@Composable
private fun Deslizadores() {
    var volumen by rememberSaveable { mutableFloatStateOf(40f) }
    var minimo by rememberSaveable { mutableFloatStateOf(200f) }
    var maximo by rememberSaveable { mutableFloatStateOf(700f) }

    ElementoCard(R.string.s3_slider_titulo, R.string.s3_slider_desc) {
        Text(stringResource(R.string.s3_volumen_valor, volumen.toInt()), style = MaterialTheme.typography.labelLarge)
        Slider(value = volumen, onValueChange = { volumen = it.roundToInt().toFloat() }, valueRange = 0f..100f)
        Text(stringResource(R.string.s3_rango_valor, minimo.toInt(), maximo.toInt()), style = MaterialTheme.typography.labelLarge)
        RangeSlider(
            value = minimo..maximo,
            // Se redondea de 50 en 50 sin "steps" para no dibujar marcas en la pista.
            onValueChange = {
                minimo = ((it.start / 50).roundToInt() * 50).toFloat()
                maximo = ((it.endInclusive / 50).roundToInt() * 50).toFloat()
            },
            valueRange = 0f..1000f,
        )
    }
}

// S3-05 Lista desplegable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListaDesplegable() {
    val paises = stringArrayResource(R.array.paises)
    var abierta by remember { mutableStateOf(false) }
    var elegido by rememberSaveable { mutableIntStateOf(0) }

    ElementoCard(R.string.s3_spinner_titulo, R.string.s3_spinner_desc) {
        ExposedDropdownMenuBox(expanded = abierta, onExpandedChange = { abierta = it }) {
            OutlinedTextField(
                value = paises[elegido],
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierta) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(expanded = abierta, onDismissRequest = { abierta = false }) {
                paises.forEachIndexed { i, pais ->
                    DropdownMenuItem(
                        text = { Text(pais) },
                        onClick = { elegido = i; abierta = false },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        Resultado(stringResource(R.string.s3_spinner_elegido, paises[elegido]))
    }
}

// S3-06 Selectores de fecha y hora
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectoresFechaHora() {
    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }
    var fecha by rememberSaveable { mutableStateOf<String?>(null) }
    var hora by rememberSaveable { mutableStateOf<String?>(null) }
    val estadoFecha = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val estadoHora = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)

    ElementoCard(R.string.s3_fecha_titulo, R.string.s3_fecha_desc) {
        Row(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            FilledTonalButton(onClick = { mostrarFecha = true }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.s3_elegir_fecha))
            }
            FilledTonalButton(onClick = { mostrarHora = true }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.s3_elegir_hora))
            }
        }
        Resultado(
            stringResource(
                R.string.s3_fecha_hora,
                fecha ?: stringResource(R.string.s3_sin_elegir),
                hora ?: stringResource(R.string.s3_sin_elegir),
            ),
        )
    }

    if (mostrarFecha) {
        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let { milis ->
                        // El DatePicker entrega la fecha en UTC.
                        val formato = SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "MX"))
                        formato.timeZone = TimeZone.getTimeZone("UTC")
                        fecha = formato.format(milis)
                    }
                    mostrarFecha = false
                }) { Text(stringResource(R.string.aceptar)) }
            },
            dismissButton = { TextButton(onClick = { mostrarFecha = false }) { Text(stringResource(R.string.s5_cancelar)) } },
        ) { DatePicker(state = estadoFecha) }
    }

    if (mostrarHora) {
        // TimePicker no trae diálogo propio: se coloca dentro de un AlertDialog.
        AlertDialog(
            onDismissRequest = { mostrarHora = false },
            title = { Text(stringResource(R.string.s3_elegir_hora)) },
            text = { TimePicker(state = estadoHora) },
            confirmButton = {
                TextButton(onClick = {
                    hora = String.format(Locale.getDefault(), "%02d:%02d", estadoHora.hour, estadoHora.minute)
                    mostrarHora = false
                }) { Text(stringResource(R.string.aceptar)) }
            },
            dismissButton = { TextButton(onClick = { mostrarHora = false }) { Text(stringResource(R.string.s5_cancelar)) } },
        )
    }
}

// S3-07 Chips de filtro
@Composable
private fun ChipsFiltro() {
    val categorias = listOf(R.string.categoria_fruta, R.string.categoria_verdura, R.string.categoria_grano)
    val comidas = listOf(
        "Manzana" to 0, "Plátano" to 0, "Mango" to 0,
        "Zanahoria" to 1, "Brócoli" to 1, "Espinaca" to 1,
        "Arroz" to 2, "Maíz" to 2, "Avena" to 2,
    )
    val activos = remember { mutableStateListOf<Int>() }
    val visibles = if (activos.isEmpty()) comidas else comidas.filter { it.second in activos }

    ElementoCard(R.string.s3_chips_titulo, R.string.s3_chips_desc) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            categorias.forEachIndexed { i, categoria ->
                val seleccionado = i in activos
                FilterChip(
                    selected = seleccionado,
                    onClick = { if (seleccionado) activos.remove(i) else activos.add(i) },
                    label = { Text(stringResource(categoria)) },
                    leadingIcon = if (seleccionado) {
                        { Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                    } else {
                        null
                    },
                )
            }
        }
        Resultado(stringResource(R.string.s3_chips_resultado, visibles.size, visibles.joinToString(", ") { it.first }))
    }
}
