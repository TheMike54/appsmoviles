# Práctica 2 — Aplicación móvil básica para operaciones CRUD con un servicio REST

## Portada

- **Nombre completo:** Miguel Ángel Rodríguez Candelario
- **Número de boleta:** 2024630606
- **Grupo:** 7CV4
- **Asignatura:** Desarrollo de Aplicaciones Móviles Nativas
- **Profesor:** Gabriel Hurtado Avilés
- **Fecha de entrega:** viernes 18 de septiembre de 2026

---

## Introducción

Esta práctica consiste en una aplicación Android nativa que se conecta a un backend
REST propio para hacer login, registro y operaciones CRUD sobre un recurso (elegí
**tareas**, a manera de una lista de pendientes por usuario).

**Stack elegido y por qué:**

- **Backend: Flask + SQLAlchemy + Flask-Bcrypt + PyJWT, dockerizado con Docker
  Compose.** Parto del repositorio de ejemplo que el propio profesor puso a disposición
  del grupo (`gabrielhuav/Flask-Compose-Login-API`, carpeta `Docker-Flask/ORM`), que ya
  traía el login/registro básico con SQLite y Bcrypt. Lo elegí porque el profesor lo
  recomendó directamente y pensé que iba a ser sencillo partir de ahí.
- **App móvil: Kotlin + Jetpack Compose (Material 3) + Retrofit.** También parto del
  proyecto base del profesor (`Android/FlaskLogin`), que traía la estructura y el tema
  de Compose ya generados por Android Studio, pero con la conexión a la API
  intencionalmente incompleta. Retrofit lo elegí porque es el cliente HTTP estándar
  para Android y se integra directo con corrutinas (`suspend fun`), que es como está
  armada toda la lógica de red de la app. Lo que no anticipé fue que este proyecto base
  traía versiones de Gradle, AGP (Android Gradle Plugin) y Kotlin que no eran
  compatibles con el JDK que tengo instalado — eso sí me costó tiempo resolver (más
  detalle en [Conclusiones](#conclusiones)).

**Aviso de origen :** este proyecto parte del repositorio de
ejemplo `gabrielhuav/Flask-Compose-Login-API`. La sección [Qué se modificó sobre el
ejemplo](#qué-modifiqué-o-agregué-sobre-el-repositorio-de-ejemplo) detalla exactamente
qué archivos y funcionalidades agregué o cambié.

---

## Desarrollo

### Conceptos del Ejercicio 2, en mis palabras

- **Docker:** herramienta que empaqueta una aplicación junto con todo lo que necesita
  para correr (código, dependencias, configuración) en una unidad llamada contenedor,
  de modo que se comporta igual sin importar en qué máquina se ejecute. A diferencia de
  una máquina virtual, no simula hardware completo: reutiliza el kernel del sistema
  operativo anfitrión, por eso un contenedor arranca en segundos y no en minutos.
- **Imagen y contenedor:** la imagen es una plantilla de solo lectura que define qué va
  a tener el contenedor (el sistema base, las librerías, el código copiado); el
  contenedor es la ejecución real de esa imagen. Puedo apagar y volver a crear el
  contenedor las veces que quiera a partir de la misma imagen. Si necesito conservar
  datos entre reinicios (como la base SQLite), el contenedor en sí es desechable, así
  que esos datos tienen que vivir en un volumen o, como en mi caso, en un archivo
  montado desde mi propia carpeta.
- **Dockerfile:** el archivo de texto con la receta paso a paso para construir la
  imagen: de qué imagen base parto (`FROM`), en qué carpeta trabajo dentro del
  contenedor (`WORKDIR`), qué archivos copio (`COPY`), qué comandos corro para instalar
  dependencias (`RUN`) y cuál es el comando que se ejecuta cuando el contenedor arranca
  (`CMD`).
- **`docker-compose.yml`:** el archivo que describe uno o varios servicios (en mi caso
  solo el backend) indicando su puerto, sus volúmenes y sus variables de entorno, para
  poder levantar o apagar todo con un solo comando en lugar de escribir comandos largos
  de `docker run` cada vez.
- **Backend / servicio REST:** el programa que corre del lado del servidor y expone
  rutas HTTP (`GET`, `POST`, `PUT`, `DELETE`) que reciben peticiones, revisan o
  modifican la base de datos y regresan una respuesta en JSON junto con un código de
  estado que indica si salió bien o mal.
- **ORM y base de datos:** un ORM como SQLAlchemy me permite trabajar con las tablas de
  la base de datos como si fueran clases y objetos de Python, sin escribir las consultas
  SQL a mano. Uso SQLite porque guarda todo en un solo archivo (`site.db`) que se crea
  solo al levantar el contenedor, sin necesitar un motor de base de datos aparte.

### Endpoints del backend

Base URL local: `http://localhost:5000` (desde el emulador de Android: `10.0.2.2:5000`,
ver [nota de conexión](#conexión-de-la-app-a-la-api)).

| Método | Ruta | Protegido | Parámetros | Descripción |
|---|---|---|---|---|
| GET | `/` | No | — | Verifica que el servicio está corriendo. |
| POST | `/register` | No | JSON: `username`, `password` | Crea un usuario nuevo (rol `user` por default), contraseña hasheada con Bcrypt. |
| POST | `/login` | No | JSON: `username`, `password` | Autentica y regresa un JWT válido por 2 horas. |
| POST | `/tareas` | Sí (Bearer) | JSON: `titulo`, `descripcion` (opcional) | Crea una tarea propia del usuario autenticado. |
| GET | `/tareas` | Sí (Bearer) | — | Lista las tareas del usuario; un usuario con rol `admin` ve las de todos. |
| GET | `/tareas/<id>` | Sí (Bearer) | — | Obtiene una tarea (solo el dueño o un `admin`). |
| PUT | `/tareas/<id>` | Sí (Bearer) | JSON: `titulo`, `descripcion`, `completada` (todos opcionales) | Actualiza una tarea (solo el dueño o un `admin`). |
| DELETE | `/tareas/<id>` | Sí (Bearer) | — | Borra una tarea (solo el dueño o un `admin`). |

Los endpoints protegidos exigen el header `Authorization: Bearer <token>` que devuelve
`/login`; sin él, o con un token vencido/inválido, responden `401`.

**Ejemplo — registro exitoso**

```powershell
Invoke-RestMethod -Uri http://localhost:5000/register -Method POST `
  -ContentType "application/json" -Body '{"username":"demo","password":"clave123"}'
```

```json
// 201
{"message": "Usuario creado exitosamente"}
```

**Ejemplo — login**

```powershell
Invoke-RestMethod -Uri http://localhost:5000/login -Method POST `
  -ContentType "application/json" -Body '{"username":"demo","password":"clave123"}'
```

```json
// 200
{
  "status": "success",
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user_id": 1,
  "username": "demo",
  "role": "user"
}
```

```json
// 401, credenciales incorrectas
{"status": "error", "message": "Credenciales inválidas"}
```

**Ejemplo — crear tarea (protegido)**

```powershell
Invoke-RestMethod -Uri http://localhost:5000/tareas -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer eyJhbGciOiJIUzI1NiIs..." } `
  -Body '{"titulo":"Estudiar para el examen","descripcion":"Repasar JWT"}'
```

```json
// 201
{"id": 1, "titulo": "Estudiar para el examen", "descripcion": "Repasar JWT", "completada": false, "owner_id": 1}
```

```json
// 401, sin token o token inválido
{"message": "Falta el token de autenticación"}
```

### Instalación y ejecución

**Backend:**

```powershell
cd Z:\Jueguitos\Proyectitos\Appsmoviles\AppMoviles_Practica2\backend
docker compose up --build
```

- `docker compose up --build` construye la imagen (si hace falta) y levanta el
  contenedor; déjalo corriendo, ahí se ven los logs de cada petición.
- El servicio queda en `http://localhost:5000`. La base SQLite (`site.db`) se crea sola
  la primera vez.
- La `SECRET_KEY` para firmar los JWT se toma del archivo `backend/.env` (no se sube al
  repositorio). Se incluye `backend/.env.example` con el nombre de la variable como
  referencia. Si `.env` no existe, el backend arranca de todos modos con una clave de
  desarrollo y **avisa por consola** que no es segura — nunca deja de compilar/arrancar
  por falta de esa configuración.
- Para pararlo: `Ctrl+C` en esa ventana, o `docker compose down` desde otra.

**App Android:**

1. Abrir Android Studio → `Open` → carpeta
   `Z:\Jueguitos\Proyectitos\Appsmoviles\AppMoviles_Practica2\app`.
2. Esperar a que sincronice Gradle.
3. Con el backend ya corriendo, correr la app (▶) sobre un emulador.
4. También se puede compilar por línea de comandos para verificar que sí compila:

```powershell
cd Z:\Jueguitos\Proyectitos\Appsmoviles\AppMoviles_Practica2\app
.\gradlew assembleDebug
```

#### Conexión de la app a la API

Desde el emulador de Android, `localhost` apunta al propio emulador, no a la PC. Por
eso la app usa `10.0.2.2:5000` como `BASE_URL` (constante en `MainActivity.kt`, objeto
`ApiClient`) — es la dirección especial que el emulador traduce al `localhost` de la
máquina anfitriona. Si se prueba en un celular físico en la misma red WiFi, hay que
cambiar esa constante por la IP local de la PC (`ipconfig` → "Dirección IPv4"), por
ejemplo `192.168.1.X:5000`.

### QA — seguridad verificada

- **Contraseñas:** nunca se guardan en texto plano; se hashean con Flask-Bcrypt antes
  de insertarse en la base de datos (`bcrypt.generate_password_hash`).
- **Sesiones:** el login regresa un JWT firmado con `SECRET_KEY` y expiración de 2
  horas (`datetime.now(timezone.utc) + timedelta(hours=2)`); el decorador
  `requiere_token` rechaza tokens vencidos o inválidos con `401` antes de llegar a la
  lógica de cada ruta.
- **Endpoints protegidos:** verificado manualmente (`Invoke-RestMethod`) que `/tareas` y
  sus variantes responden `401` sin header `Authorization`, y `403` si el usuario
  autenticado no es dueño de la tarea ni tiene rol `admin`.
- **Credenciales fuera del código:** la `SECRET_KEY` real vive en `backend/.env`, que
  está en `.gitignore` y nunca se sube; el repositorio solo publica
  `backend/.env.example` con el nombre de la variable, sin el valor real.
- Estas verificaciones fueron manuales (con `Invoke-RestMethod`/`curl`) durante el
  desarrollo; no hay una suite de pruebas automatizadas todavía.

![Logs del backend respondiendo cada operación](docs/backend_logs.png)

*Terminal con `docker compose up --build` corriendo: se ven los `POST /register`,
`POST /login` (401 con credenciales malas, 200 con las correctas) y el CRUD completo de
`/tareas` (`POST`, `GET`, `PUT`, `DELETE`) respondidos en tiempo real durante las pruebas.*

### Qué modifiqué o agregué sobre el repositorio de ejemplo

**Backend** (`Docker-Flask/ORM` del ejemplo → `AppMoviles_Practica2/backend`):

- Agregué el campo `role` al modelo `User` (`user`/`admin`).
- Agregué autenticación con JWT: función `generar_token`, decorador `requiere_token` y
  el campo `token` en la respuesta de `/login` (el ejemplo original solo validaba
  usuario/contraseña, sin emitir ninguna sesión).
- Agregué el modelo `Tarea` y las 4 rutas CRUD (`POST/GET/PUT/DELETE /tareas`,
  `GET/PUT/DELETE /tareas/<id>`), con control de permisos por dueño/rol
  (`_obtener_tarea_o_error`) — el ejemplo original no traía ningún recurso CRUD, solo
  login/registro.
- Cambié la `SECRET_KEY` de estar fija en el código a leerse de variable de entorno,
  con aviso en consola si falta.

**App Android** (`Android/FlaskLogin` del ejemplo → `AppMoviles_Practica2/app`):

- El ejemplo traía el proyecto de Compose generado por Android Studio pero sin ninguna
  pantalla de login/registro real ni conexión a la API. Agregué en `MainActivity.kt`:
  - Los modelos de datos y las interfaces Retrofit (`AuthApi`, `TareasApi`) y el objeto
    `ApiClient`.
  - El objeto `Session` para guardar el token/usuario/rol en memoria mientras la app
    está abierta.
  - El menú de navegación (`AppTopBar` con `DropdownMenu`) y las 3 pantallas
    (`LoginScreen`, `RegistroScreen`, `TareasScreen`) con manejo de estados de carga,
    error y sesión iniciada.

### Capturas de pantalla

**Registro exitoso**
![Registro exitoso](docs/registro_exitoso.png)

**Login exitoso**
![Login exitoso](docs/login_exitoso.png)

**Login con credenciales incorrectas**
![Login con error](docs/login_error.png)

**Crear una tarea**
![Crear tarea](docs/crear_tarea.png)

**Listar tareas**
![Listar tareas](docs/listar_tareas.png)

**Completar/actualizar una tarea**
![Actualizar tarea](docs/actualizar_tarea.png)

**Borrar una tarea**
![Borrar tarea](docs/borrar_tarea.png)

---

## Conclusiones

Esta práctica me pareció muy interesante porque, aunque ya conocía el concepto de
peticiones, nunca había trabajado de cerca con un backend como tal — nunca había visto
tan de cerca cómo funciona un GET, un POST, un PUT y un DELETE funcionando de verdad
detrás de una app. Fue interesante ver cómo funciona un backend desde adentro en vez de
solo consumirlo.

Algo que también me pareció interesante fue una recomendación que me dio la IA: evitar
que alguien pudiera registrarse como administrador directamente desde la consola del
navegador, forzando el rol de usuario normal sin importar qué mande el cliente al
registrarse. Ya había escuchado sobre esto por los famosos "vibe coders" —
que muchas veces, cuando alguien arma una aplicación o página apoyándose fuertemente en
IA sin revisar bien el código, se quedan vulnerabilidades como esa sin querer. Sabía que
pasaba pero nunca había entendido bien el porqué; en esta práctica ya me quedó claro.

Lo que más tiempo me quitó de toda la práctica fue del lado de Android: el proyecto base
traía versiones de Gradle, AGP y Kotlin que no eran compatibles con el JDK que tengo
instalado, y el proyecto simplemente no compilaba. En Android Studio le daba clic al
botón de sincronizar y no pasaba nada. Le pedí ayuda a la IA (Claude) porque no entendía
bien cuál era el problema; la primera vez que lo intentamos no se pudo resolver, pero
después la IA buscó qué versiones de Gradle, AGP y Kotlin son compatibles entre sí y
encontró una combinación que sí funcionaba, con la que terminamos actualizando el
proyecto. De ahí me queda la costumbre de revisar la compatibilidad de versiones entre
un proyecto base y mi propio entorno antes de ponerme a programar, en vez de
descubrirlo a medio camino.

---

## Bibliografía

Docker Inc. (s. f.). *Docker Compose overview*. Docker Docs. https://docs.docker.com/compose/

Google. (s. f.). *Jetpack Compose*. Android Developers. https://developer.android.com/jetpack/compose

Hurtado Avilés, G. (s. f.). *Flask-Compose-Login-API* [Repositorio]. GitHub. https://github.com/gabrielhuav/Flask-Compose-Login-API

Pallets Projects. (s. f.). *Flask Documentation*. https://flask.palletsprojects.com/

Pallets Projects. (s. f.). *Flask-SQLAlchemy Documentation*. https://flask-sqlalchemy.palletsprojects.com/

PyJWT. (s. f.). *PyJWT Documentation*. https://pyjwt.readthedocs.io/

Rougeth, M. (s. f.). *Flask-Bcrypt Documentation*. https://flask-bcrypt.readthedocs.io/

Square Inc. (s. f.). *Retrofit*. https://square.github.io/retrofit/

> Si consultaste algo más (un video, un foro, una respuesta de Stack Overflow) que yo no
> haya visto en esta sesión, agrégalo aquí en formato APA.
