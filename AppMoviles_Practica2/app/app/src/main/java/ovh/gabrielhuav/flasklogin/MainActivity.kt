package ovh.gabrielhuav.flasklogin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ovh.gabrielhuav.flasklogin.ui.theme.FlaskLoginTheme
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ---------- Modelos ----------

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(
    val message: String? = null,
    val status: String? = null,
    val token: String? = null,
    val user_id: Int? = null,
    val username: String? = null,
    val role: String? = null,
)

data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val completada: Boolean,
    val owner_id: Int,
)
data class TareaRequest(
    val titulo: String? = null,
    val descripcion: String? = null,
    val completada: Boolean? = null,
)

// ---------- Retrofit ----------

interface AuthApi {
    @POST("register")
    suspend fun register(@Body body: AuthRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body body: AuthRequest): Response<AuthResponse>
}

interface TareasApi {
    @GET("tareas")
    suspend fun listar(@Header("Authorization") token: String): Response<List<Tarea>>

    @POST("tareas")
    suspend fun crear(@Header("Authorization") token: String, @Body body: TareaRequest): Response<Tarea>

    @PUT("tareas/{id}")
    suspend fun actualizar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: TareaRequest,
    ): Response<Tarea>

    @DELETE("tareas/{id}")
    suspend fun borrar(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>
}

object ApiClient {
    // 10.0.2.2 = el "localhost" de tu PC visto desde el emulador de Android.
    // En celular físico en la misma red, cambia por la IP local de tu PC (ipconfig).
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val tareasApi: TareasApi by lazy { retrofit.create(TareasApi::class.java) }
}

// ---------- Sesión en memoria ----------
// Vive mientras la app está abierta. Si quieres que sobreviva a cerrar la app,
// se puede migrar a SharedPreferences (mejora opcional, no obligatoria).
object Session {
    var token: String? by mutableStateOf(null)
    var username: String? by mutableStateOf(null)
    var role: String? by mutableStateOf(null)

    val estaLogueado: Boolean get() = token != null

    fun tokenHeader(): String = "Bearer ${token.orEmpty()}"

    fun cerrarSesion() {
        token = null
        username = null
        role = null
    }
}

// ---------- MainActivity, navegación y pantallas ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlaskLoginTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("login") { LoginScreen(navController) }
            composable("registro") { RegistroScreen(navController) }
            composable("tareas") { TareasScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavHostController) {
    var menuAbierto by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Práctica 2") },
        actions = {
            IconButton(onClick = { menuAbierto = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menú")
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                DropdownMenuItem(
                    text = { Text("Iniciar sesión") },
                    onClick = { menuAbierto = false; navController.navigate("login") },
                )
                DropdownMenuItem(
                    text = { Text("Registro") },
                    onClick = { menuAbierto = false; navController.navigate("registro") },
                )
                DropdownMenuItem(
                    text = { Text("Operaciones CRUD") },
                    onClick = { menuAbierto = false; navController.navigate("tareas") },
                )
                if (Session.estaLogueado) {
                    DropdownMenuItem(
                        text = { Text("Cerrar sesión") },
                        onClick = {
                            menuAbierto = false
                            Session.cerrarSesion()
                            navController.navigate("login")
                        },
                    )
                }
            }
        },
    )
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)

        if (Session.estaLogueado) {
            Text("Sesión iniciada como ${Session.username} (${Session.role})")
        }

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )

        Button(
            enabled = !cargando,
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    estado = "Usuario y contraseña son obligatorios"
                    return@Button
                }
                scope.launch {
                    cargando = true
                    estado = "Cargando..."
                    try {
                        val resp = withContext(Dispatchers.IO) {
                            ApiClient.authApi.login(AuthRequest(username, password))
                        }
                        if (resp.isSuccessful && resp.body()?.token != null) {
                            val body = resp.body()!!
                            Session.token = body.token
                            Session.username = body.username
                            Session.role = body.role
                            estado = null
                            navController.navigate("tareas")
                        } else {
                            estado = resp.errorBody()?.string() ?: "Credenciales inválidas"
                        }
                    } catch (e: Exception) {
                        estado = "No se pudo conectar al backend: ${e.message}"
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) { Text("Iniciar sesión") }

        estado?.let { Text(it, modifier = Modifier.padding(top = 16.dp)) }
    }
}

@Composable
fun RegistroScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Registro de usuario", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )

        Button(
            enabled = !cargando,
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    estado = "Usuario y contraseña son obligatorios"
                    return@Button
                }
                scope.launch {
                    cargando = true
                    estado = "Cargando..."
                    try {
                        val resp = withContext(Dispatchers.IO) {
                            ApiClient.authApi.register(AuthRequest(username, password))
                        }
                        if (resp.isSuccessful) {
                            estado = "Usuario creado, ya puedes iniciar sesión"
                            delay(1500) // deja que se lea el mensaje antes de navegar
                            navController.navigate("login")
                        } else {
                            estado = resp.errorBody()?.string() ?: "Error ${resp.code()}"
                        }
                    } catch (e: Exception) {
                        estado = "No se pudo conectar al backend: ${e.message}"
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) { Text("Registrarme") }

        estado?.let { Text(it, modifier = Modifier.padding(top = 16.dp)) }
    }
}

@Composable
fun TareasScreen() {
    var tareas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
    var nuevoTitulo by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun recargar() {
        cargando = true
        error = null
        try {
            val resp = withContext(Dispatchers.IO) {
                ApiClient.tareasApi.listar(Session.tokenHeader())
            }
            if (resp.isSuccessful) {
                tareas = resp.body().orEmpty()
            } else {
                error = resp.errorBody()?.string() ?: "Error ${resp.code()}"
            }
        } catch (e: Exception) {
            error = "No se pudo conectar al backend: ${e.message}"
        } finally {
            cargando = false
        }
    }

    LaunchedEffect(Session.token) {
        if (Session.estaLogueado) recargar()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Operaciones CRUD — Tareas", style = MaterialTheme.typography.headlineSmall)

        if (!Session.estaLogueado) {
            Text(
                "Debes iniciar sesión para ver tus tareas.",
                modifier = Modifier.padding(top = 16.dp),
            )
            return@Column
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            OutlinedTextField(
                value = nuevoTitulo,
                onValueChange = { nuevoTitulo = it },
                label = { Text("Nueva tarea") },
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = {
                    if (nuevoTitulo.isBlank()) return@Button
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            ApiClient.tareasApi.crear(Session.tokenHeader(), TareaRequest(titulo = nuevoTitulo))
                        }
                        nuevoTitulo = ""
                        recargar()
                    }
                },
                modifier = Modifier.padding(start = 8.dp),
            ) { Text("Agregar") }
        }

        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        error?.let { Text(it, modifier = Modifier.padding(top = 16.dp)) }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(tareas) { tarea ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = tarea.titulo + if (tarea.completada) " ✔" else "",
                        modifier = Modifier.weight(1f),
                    )
                    Button(onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                ApiClient.tareasApi.actualizar(
                                    Session.tokenHeader(), tarea.id,
                                    TareaRequest(completada = !tarea.completada),
                                )
                            }
                            recargar()
                        }
                    }) { Text(if (tarea.completada) "Reabrir" else "Completar") }

                    Button(onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                ApiClient.tareasApi.borrar(Session.tokenHeader(), tarea.id)
                            }
                            recargar()
                        }
                    }, modifier = Modifier.padding(start = 4.dp)) { Text("Borrar") }
                }
            }
        }
    }
}