package mx.escom.catalogoui.compose.secciones

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.ElementoCard
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.LocalSnackbar
import mx.escom.catalogoui.compose.comun.PantallaSeccion
import mx.escom.catalogoui.compose.comun.Resultado

private const val URL_IMAGEN = "https://picsum.photos/id/1015/800/500"

/** Sección 5: elementos que informan o dan retroalimentación al usuario. */
@Composable
fun SeccionInformacion() {
    PantallaSeccion {
        EstilosTexto()
        Imagenes()
        Progreso()
        Mensajes()
        DialogoConfirmacion()
        HojaInferior()
        TarjetaBadge()
    }
}

// S5-01 Estilos de texto
@Composable
private fun EstilosTexto() {
    var tamano by rememberSaveable { mutableFloatStateOf(22f) }
    val primario = MaterialTheme.colorScheme.primary

    ElementoCard(R.string.s5_textos_titulo, R.string.s5_textos_desc) {
        Text(stringResource(R.string.s5_texto_titular), fontSize = tamano.sp, lineHeight = (tamano * 1.25f).sp)
        Text(stringResource(R.string.s5_texto_subtitulo), style = MaterialTheme.typography.titleMedium, color = primario)
        // Un mismo Text con varios énfasis usando un AnnotatedString.
        Text(
            buildAnnotatedString {
                append("Texto normal, ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("negrita") }
                append(", ")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("cursiva") }
                append(", ")
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("subrayado") }
                append(" y ")
                withStyle(SpanStyle(color = primario, fontWeight = FontWeight.Bold)) { append("color") }
                append(".")
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            stringResource(R.string.s5_texto_etiqueta).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            stringResource(R.string.s5_tamano_valor, tamano.toInt()),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = Espacios.chico),
        )
        // Se redondea a números pares en lugar de usar "steps" para no dibujar marcas en la pista.
        Slider(value = tamano, onValueChange = { tamano = ((it / 2).roundToInt() * 2).toFloat() }, valueRange = 12f..40f)
    }
}

// S5-02 Imagen local e imagen desde URL
@Composable
private fun Imagenes() {
    val modos = listOf(
        R.string.s5_recortar to ContentScale.Crop,
        R.string.s5_ajustar to ContentScale.Fit,
        R.string.s5_original to ContentScale.None,
    )
    var modo by rememberSaveable { mutableIntStateOf(0) }
    var estadoUrl by remember { mutableIntStateOf(R.string.s5_imagen_cargando) }
    var intento by remember { mutableIntStateOf(0) }
    val escala = modos[modo].second
    val fondo = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .background(MaterialTheme.colorScheme.surfaceContainerHighest)

    ElementoCard(R.string.s5_imagen_titulo, R.string.s5_imagen_desc) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            modos.forEachIndexed { i, (texto, _) ->
                FilterChip(selected = i == modo, onClick = { modo = i }, label = { Text(stringResource(texto)) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painterResource(R.drawable.paisaje_local),
                    contentDescription = stringResource(R.string.s5_imagen_local),
                    contentScale = escala,
                    modifier = fondo,
                )
                Text(stringResource(R.string.s5_imagen_local), style = MaterialTheme.typography.bodySmall)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                // Coil descarga la imagen en segundo plano; si no hay internet muestra el ícono de error.
                AsyncImage(
                    model = "$URL_IMAGEN?intento=$intento",
                    contentDescription = stringResource(R.string.s5_imagen_url),
                    contentScale = escala,
                    error = painterResource(R.drawable.ic_info),
                    onLoading = { estadoUrl = R.string.s5_imagen_cargando },
                    onSuccess = { estadoUrl = R.string.s5_imagen_url },
                    onError = { estadoUrl = R.string.s5_imagen_error },
                    modifier = fondo,
                )
                Text(stringResource(estadoUrl), style = MaterialTheme.typography.bodySmall)
            }
        }
        TextButton(onClick = { intento++ }) { Text(stringResource(R.string.s5_recargar)) }
    }
}

// S5-03 Indicadores de progreso
@Composable
private fun Progreso() {
    var progreso by rememberSaveable { mutableIntStateOf(30) }

    ElementoCard(R.string.s5_progreso_titulo, R.string.s5_progreso_desc) {
        Text(stringResource(R.string.s5_progreso_valor, progreso), style = MaterialTheme.typography.labelLarge)
        LinearProgressIndicator(
            progress = { progreso / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Espacios.chico),
        )
        Row(Modifier.padding(top = Espacios.interno), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(progress = { progreso / 100f })
            Spacer(Modifier.width(Espacios.interno))
            FilledTonalButton(onClick = { progreso = (progreso + 10).coerceAtMost(100) }, enabled = progreso < 100) {
                Text(stringResource(R.string.s5_avanzar))
            }
            TextButton(onClick = { progreso = 0 }) { Text(stringResource(R.string.s5_reiniciar)) }
        }
        Text(
            stringResource(R.string.s5_indeterminado),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = Espacios.interno),
        )
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Espacios.chico),
        )
        CircularProgressIndicator(modifier = Modifier.padding(top = Espacios.chico))
    }
}

// S5-04 Toast y snackbar
@Composable
private fun Mensajes() {
    val contexto = LocalContext.current
    val snackbar = LocalSnackbar.current
    val alcance = rememberCoroutineScope()
    var resultado by rememberSaveable { mutableIntStateOf(R.string.toca_para_probar) }
    val textoToast = stringResource(R.string.s5_toast)
    val textoSnackbar = stringResource(R.string.s5_snackbar)
    val textoDeshacer = stringResource(R.string.s4_deshacer)

    ElementoCard(R.string.s5_mensajes_titulo, R.string.s5_mensajes_desc) {
        Row(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
            OutlinedButton(onClick = {
                // Compose no tiene toast propio: se usa el Toast de Android.
                Toast.makeText(contexto, textoToast, Toast.LENGTH_SHORT).show()
                resultado = R.string.s5_toast_mostrado
            }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.s5_mostrar_toast)) }
            Button(onClick = {
                alcance.launch {
                    val r = snackbar.showSnackbar(textoSnackbar, actionLabel = textoDeshacer, duration = SnackbarDuration.Long)
                    if (r == SnackbarResult.ActionPerformed) resultado = R.string.s5_snackbar_deshecho
                }
            }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.s5_mostrar_snackbar)) }
        }
        Resultado(stringResource(resultado))
    }
}

// S5-05 Diálogo de confirmación
@Composable
private fun DialogoConfirmacion() {
    var abierto by remember { mutableStateOf(false) }
    var resultado by rememberSaveable { mutableIntStateOf(R.string.toca_para_probar) }

    ElementoCard(R.string.s5_dialogo_titulo, R.string.s5_dialogo_desc) {
        Button(onClick = { abierto = true }) {
            Icon(painterResource(R.drawable.ic_delete), contentDescription = null, modifier = Modifier.size(18.dp))
            Text(stringResource(R.string.s5_eliminar_archivo), modifier = Modifier.padding(start = Espacios.chico))
        }
        Resultado(stringResource(resultado))
    }

    if (abierto) {
        AlertDialog(
            onDismissRequest = { abierto = false },
            icon = { Icon(painterResource(R.drawable.ic_delete), contentDescription = null) },
            title = { Text(stringResource(R.string.s5_dialogo_pregunta)) },
            text = { Text(stringResource(R.string.s5_dialogo_mensaje)) },
            confirmButton = {
                TextButton(onClick = { resultado = R.string.s5_dialogo_eliminado; abierto = false }) {
                    Text(stringResource(R.string.s5_eliminar))
                }
            },
            dismissButton = {
                TextButton(onClick = { resultado = R.string.s5_dialogo_cancelado; abierto = false }) {
                    Text(stringResource(R.string.s5_cancelar))
                }
            },
        )
    }
}

// S5-06 Hoja inferior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HojaInferior() {
    var abierta by remember { mutableStateOf(false) }
    var resultado by rememberSaveable { mutableStateOf<String?>(null) }
    val opciones = listOf(
        R.string.s5_opcion_compartir to R.drawable.ic_share,
        R.string.s5_opcion_copiar to R.drawable.ic_copy,
        R.string.s5_opcion_editar to R.drawable.ic_edit,
    )
    val textoCerrada = stringResource(R.string.s5_hoja_cerrada)
    val textosOpciones = opciones.map { stringResource(it.first) }
    val formatoElegido = stringResource(R.string.s5_hoja_elegido)

    ElementoCard(R.string.s5_hoja_titulo, R.string.s5_hoja_desc) {
        FilledTonalButton(onClick = { abierta = true }) { Text(stringResource(R.string.s5_abrir_hoja)) }
        Resultado(resultado ?: stringResource(R.string.toca_para_probar))
    }

    if (abierta) {
        ModalBottomSheet(onDismissRequest = { abierta = false; resultado = textoCerrada }) {
            Text(
                stringResource(R.string.s5_hoja_encabezado),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = Espacios.pantalla, vertical = Espacios.chico),
            )
            Column(Modifier.navigationBarsPadding()) {
                opciones.forEachIndexed { i, (_, icono) ->
                    ListItem(
                        headlineContent = { Text(textosOpciones[i]) },
                        leadingContent = { Icon(painterResource(icono), contentDescription = null) },
                        modifier = Modifier.clickable {
                            resultado = formatoElegido.format(textosOpciones[i])
                            abierta = false
                        },
                    )
                }
            }
        }
    }
}

// S5-07 Tarjeta, separador y badge
@Composable
private fun TarjetaBadge() {
    var sinLeer by rememberSaveable { mutableIntStateOf(3) }

    ElementoCard(R.string.s5_tarjeta_titulo, R.string.s5_tarjeta_desc) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(Espacios.interno)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.padding(6.dp)) {
                        BadgedBox(badge = { if (sinLeer > 0) Badge { Text("$sinLeer") } }) {
                            Icon(
                                painterResource(R.drawable.ic_mail),
                                contentDescription = stringResource(R.string.s5_bandeja),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.s5_bandeja_estado, sinLeer),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = Espacios.interno),
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(Espacios.chico)) {
                    FilledTonalButton(onClick = { sinLeer++ }) { Text(stringResource(R.string.s5_nuevo_correo)) }
                    TextButton(onClick = { sinLeer = 0 }) { Text(stringResource(R.string.s5_leer_todo)) }
                }
            }
        }
    }
}
