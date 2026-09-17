# Backend — Práctica 2 (API REST con Flask)

Backend REST en Flask + SQLAlchemy + Flask-Bcrypt + PyJWT, dockerizado con Docker
Compose. Basado en el repositorio de ejemplo del profesor
(`gabrielhuav/Flask-Compose-Login-API`, carpeta `Docker-Flask/ORM`), al que se le agregó
autenticación con JWT, roles y el CRUD de tareas.

La documentación completa de la práctica (conceptos, capturas, conclusiones) está en el
[README principal](../README.md).

## Cómo levantarlo

Desde la raíz del repositorio clonado:

```bash
cd AppMoviles_Practica2/backend
docker compose up --build
```

Deja esa ventana abierta (muestra los logs en vivo). El servicio queda disponible en
`http://localhost:5000`.

Para pararlo: `Ctrl+C` en esa misma ventana, o `docker compose down` desde otra terminal
dentro de esta carpeta.

### Variable de entorno `SECRET_KEY` (opcional)

La llave con la que se firman los JWT se lee de la variable `SECRET_KEY`. Para definirla,
copia el archivo de ejemplo y cambia el valor antes de levantar el contenedor:

```bash
# Windows (PowerShell)
Copy-Item .env.example .env
# Linux / macOS
cp .env.example .env
```

Si no existe `.env`, el backend arranca igual con una clave de desarrollo y lo avisa en
consola. El archivo `.env` está en `.gitignore` y nunca se sube al repositorio.

## Endpoints

| Método | Ruta | Protegido | Parámetros |
|---|---|---|---|
| GET | `/` | No | — |
| POST | `/register` | No | JSON: `username`, `password` |
| POST | `/login` | No | JSON: `username`, `password` |
| POST | `/tareas` | Sí | JSON: `titulo`, `descripcion` (opcional) |
| GET | `/tareas` | Sí | — |
| GET | `/tareas/<id>` | Sí | — |
| PUT | `/tareas/<id>` | Sí | JSON: `titulo`, `descripcion`, `completada` (todos opcionales) |
| DELETE | `/tareas/<id>` | Sí | — |

Los endpoints protegidos requieren el header `Authorization: Bearer <token>`, con el
token que regresa `/login`.

### `GET /`
Verifica que el servicio está corriendo.

```powershell
Invoke-RestMethod -Uri http://localhost:5000/ -Method GET
```
Respuesta (200):
```json
{"message": "API Funcionando"}
```

### `POST /register`
Crea un usuario nuevo con rol `user`. La contraseña se guarda hasheada con Bcrypt.

```powershell
Invoke-RestMethod -Uri http://localhost:5000/register -Method POST -ContentType "application/json" -Body '{"username":"demo","password":"clave123"}'
```
Respuesta (201, usuario nuevo):
```json
{"message": "Usuario creado exitosamente"}
```
Respuesta (400, usuario repetido):
```json
{"message": "El usuario ya existe"}
```

### `POST /login`
Autentica un usuario existente y regresa un JWT válido por 2 horas.

```powershell
Invoke-RestMethod -Uri http://localhost:5000/login -Method POST -ContentType "application/json" -Body '{"username":"demo","password":"clave123"}'
```
Respuesta (200, credenciales correctas):
```json
{"status": "success", "message": "Login exitoso", "token": "eyJhbGciOiJIUzI1NiIs...", "user_id": 1, "username": "demo", "role": "user"}
```
Respuesta (401, credenciales incorrectas):
```json
{"status": "error", "message": "Credenciales inválidas"}
```

### `/tareas` (protegidos)

Un usuario normal solo ve y modifica sus propias tareas; un usuario con rol `admin` ve
las de todos.

```powershell
$h = @{ Authorization = "Bearer eyJhbGciOiJIUzI1NiIs..." }

# Crear (201)
Invoke-RestMethod -Uri http://localhost:5000/tareas -Method POST -Headers $h -ContentType "application/json" -Body '{"titulo":"Estudiar","descripcion":"Repasar JWT"}'

# Listar (200)
Invoke-RestMethod -Uri http://localhost:5000/tareas -Method GET -Headers $h

# Actualizar (200)
Invoke-RestMethod -Uri http://localhost:5000/tareas/1 -Method PUT -Headers $h -ContentType "application/json" -Body '{"completada":true}'

# Borrar (200)
Invoke-RestMethod -Uri http://localhost:5000/tareas/1 -Method DELETE -Headers $h
```

Respuestas de error:

| Código | Cuándo |
|---|---|
| 400 | Falta el `titulo` al crear. |
| 401 | Sin header `Authorization`, o token inválido o vencido. |
| 403 | La tarea es de otro usuario y quien la pide no es `admin`. |
| 404 | La tarea no existe. |

## Notas técnicas

- La contraseña se guarda hasheada con Bcrypt (nunca en texto plano).
- Al registrarse siempre se asigna el rol `user`, aunque el cliente mande otro rol en la
  petición.
- La base de datos es SQLite (`site.db`, dentro de `instance/`); se crea sola al levantar
  el contenedor la primera vez y no requiere configuración externa.
- `Dockerfile`: el `pip install` se reintenta hasta 5 veces para tolerar cortes de red a
  media descarga durante la construcción de la imagen.
- `docker-compose.yml`: la red interna usa MTU 1400 para evitar timeouts de `pip` en
  conexiones con MTU menor a 1500.
