package mx.escom.catalogoui.compose.secciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.escom.catalogoui.compose.R
import mx.escom.catalogoui.compose.comun.CatalogoViewModel
import mx.escom.catalogoui.compose.comun.ElementoCard
import mx.escom.catalogoui.compose.comun.Espacios
import mx.escom.catalogoui.compose.comun.LocalSnackbar
import mx.escom.catalogoui.compose.comun.PantallaSeccion
import mx.escom.catalogoui.compose.comun.Resultado

/** Sección 1: elementos para capturar texto. */
@Composable
fun SeccionEntradaTexto(catalogo: CatalogoViewModel, alVerLista: () -> Unit) {
    PantallaSeccion {
        CampoSimple(catalogo, alVerLista)
        CampoValidacion()
        CampoPassword()
        CamposTeclado()
        CampoMultilinea()
        CampoSugerencias()
        BarraBusqueda()
    }
}

// S1-01 Campo de texto simple (conectado con la Sección 4)
@Composable
private fun CampoSimple(catalogo: CatalogoViewModel, alVerLista: () -> Unit) {
    var texto by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    val snackbar = LocalSnackbar.current
    val alcance = rememberCoroutineScope()
    val mensajeAgregado = stringResource(R.string.s1_simple_agregado, texto.trim())
    val verLista = stringResource(R.string.s1_simple_ver_lista)

    ElementoCard(R.string.s1_simple_titulo, R.string.s1_simple_desc) {
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it; error = false },
            label = { Text(stringResource(R.string.s1_simple_hint)) },
            isError = error,
            supportingText = if (error) {
                { Text(stringResource(R.string.s1_simple_falta)) }
            } else {
                null
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
        )
        Resultado(
            if (texto.isBlank()) stringResource(R.string.s1_simple_vacio) else stringResource(R.string.s1_simple_eco, texto),
        )
        Spacer(Modifier.height(Espacios.chico))
        Button(onClick = {
            val nombre = texto.trim()
            if (nombre.isEmpty()) {
                error = true
                return@Button
            }
            catalogo.agregarDesdeEntrada(nombre)
            texto = ""
            alcance.launch {
                val resultado = snackbar.showSnackbar(mensajeAgregado, actionLabel = verLista, withDismissAction = false)
                if (resultado == SnackbarResult.ActionPerformed) alVerLista()
            }
        }) {
            Icon(painterResource(R.drawable.ic_add), contentDescription = null)
            Text(stringResource(R.string.s1_simple_agregar), modifier = Modifier.padding(start = Espacios.chico))
        }
    }
}

// S1-02 Campo con validación
@Composable
private fun CampoValidacion() {
    var usuario by rememberSaveable { mutableStateOf("") }
    val valido = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]{3,}$").matches(usuario)
    val conError = usuario.isNotEmpty() && !valido

    ElementoCard(R.string.s1_validacion_titulo, R.string.s1_validacion_desc) {
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text(stringResource(R.string.s1_validacion_hint)) },
            isError = conError,
            supportingText = when {
                conError -> { { Text(stringResource(R.string.s1_validacion_error)) } }
                usuario.isNotEmpty() -> { { Text(stringResource(R.string.s1_validacion_ok)) } }
                else -> null
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// S1-03 Campo de contraseña
@Composable
private fun CampoPassword() {
    var clave by rememberSaveable { mutableStateOf("") }
    var visible by rememberSaveable { mutableStateOf(false) }
    val fuerza = when {
        clave.length >= 10 -> stringResource(R.string.s1_password_fuerte)
        clave.length >= 6 -> stringResource(R.string.s1_password_media)
        else -> stringResource(R.string.s1_password_debil)
    }

    ElementoCard(R.string.s1_password_titulo, R.string.s1_password_desc) {
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text(stringResource(R.string.s1_password_hint)) },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        painterResource(if (visible) R.drawable.ic_visibility_off else R.drawable.ic_visibility),
                        contentDescription = stringResource(R.string.s1_password_hint),
                    )
                }
            },
            supportingText = if (clave.isNotEmpty()) {
                { Text(stringResource(R.string.s1_password_fuerza, clave.length, fuerza)) }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// S1-04 Tipos de teclado
@Composable
private fun CamposTeclado() {
    var edad by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }

    ElementoCard(R.string.s1_teclados_titulo, R.string.s1_teclados_desc) {
        OutlinedTextField(
            value = edad,
            onValueChange = { nuevo -> edad = nuevo.filter { it.isDigit() }.take(3) },
            label = { Text(stringResource(R.string.s1_teclado_numero)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text(stringResource(R.string.s1_teclado_correo)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Espacios.chico),
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text(stringResource(R.string.s1_teclado_telefono)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Espacios.chico),
        )
        Resultado(
            stringResource(
                R.string.s1_teclados_resumen,
                edad.ifEmpty { "—" },
                correo.ifEmpty { "—" },
                telefono.ifEmpty { "—" },
            ),
        )
    }
}

// S1-05 Campo multilínea
@Composable
private fun CampoMultilinea() {
    var comentario by rememberSaveable { mutableStateOf("") }
    val lineas = if (comentario.isEmpty()) 0 else comentario.lines().size

    ElementoCard(R.string.s1_multilinea_titulo, R.string.s1_multilinea_desc) {
        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it.take(200) },
            label = { Text(stringResource(R.string.s1_multilinea_hint)) },
            minLines = 3,
            supportingText = { Text("${comentario.length} / 200") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
        Resultado(stringResource(R.string.s1_multilinea_lineas, lineas))
    }
}

// S1-06 Sugerencias automáticas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoSugerencias() {
    val estados = stringArrayResource(R.array.estados_mexico)
    var texto by rememberSaveable { mutableStateOf("") }
    var abierto by remember { mutableStateOf(false) }
    var elegido by rememberSaveable { mutableStateOf<String?>(null) }
    val sugerencias = estados.filter { texto.isNotEmpty() && it.contains(texto, ignoreCase = true) }

    ElementoCard(R.string.s1_sugerencias_titulo, R.string.s1_sugerencias_desc) {
        ExposedDropdownMenuBox(
            expanded = abierto && sugerencias.isNotEmpty(),
            onExpandedChange = { abierto = it },
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it; abierto = true },
                label = { Text(stringResource(R.string.s1_sugerencias_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            )
            ExposedDropdownMenu(
                expanded = abierto && sugerencias.isNotEmpty(),
                onDismissRequest = { abierto = false },
            ) {
                sugerencias.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado) },
                        onClick = {
                            texto = estado
                            elegido = estado
                            abierto = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        elegido?.let { Resultado(stringResource(R.string.s1_sugerencias_elegido, it)) }
    }
}

// S1-07 Barra de búsqueda
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraBusqueda() {
    val lenguajes = stringArrayResource(R.array.lenguajes)
    var consulta by rememberSaveable { mutableStateOf("") }
    var expandida by remember { mutableStateOf(false) }
    val resultados = lenguajes.filter { it.contains(consulta.trim(), ignoreCase = true) }

    ElementoCard(R.string.s1_busqueda_titulo, R.string.s1_busqueda_desc) {
        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = consulta,
                    onQueryChange = { consulta = it; expandida = it.isNotEmpty() },
                    onSearch = { expandida = false },
                    expanded = expandida,
                    onExpandedChange = { expandida = it && consulta.isNotEmpty() },
                    placeholder = { Text(stringResource(R.string.s1_busqueda_hint)) },
                    leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
                    trailingIcon = if (consulta.isNotEmpty()) {
                        {
                            IconButton(onClick = { consulta = ""; expandida = false }) {
                                Icon(painterResource(R.drawable.ic_close), contentDescription = null)
                            }
                        }
                    } else {
                        null
                    },
                )
            },
            expanded = expandida,
            onExpandedChange = { expandida = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.heightIn(max = 240.dp).verticalScroll(rememberScrollState())) {
                resultados.forEach { lenguaje ->
                    ListItem(
                        headlineContent = { Text(lenguaje) },
                        modifier = Modifier.clickable { consulta = lenguaje; expandida = false },
                    )
                }
            }
        }
        Resultado(
            if (resultados.isEmpty()) {
                stringResource(R.string.s1_busqueda_sin_resultados, consulta)
            } else {
                stringResource(R.string.s1_busqueda_resultados, resultados.size, resultados.joinToString(", "))
            },
        )
    }
}
