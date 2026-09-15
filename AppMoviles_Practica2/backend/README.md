# Backend — Práctica 2 (login con Flask)

Backend REST minimalista en Flask + SQLAlchemy + Bcrypt, dockerizado. Basado en el
repositorio de ejemplo del profesor (`gabrielhuav/Flask-Compose-Login-API`, carpeta
`Docker-Flask/ORM`).

## Cómo levantarlo

```powershell
cd Z:\Jueguitos\Proyectitos\Appsmoviles\AppMoviles_Practica2\backend
docker compose up --build
```

Deja esa ventana abierta (muestra los logs en vivo). El servicio queda disponible en
`http://localhost:5000`.

Para pararlo: `Ctrl+C` en esa misma ventana, o `docker compose down` desde otra.

## Endpoints

### `GET /`
Verifica que el servicio está corriendo.

```powershell
Invoke-RestMethod -Uri http://localhost:5000/ -Method GET
```
Respuesta:
```json
{"message": "API Funcionando"}
```

### `POST /register`
Crea un usuario nuevo. Parámetros (JSON): `username`, `password`.

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
Autentica un usuario existente. Parámetros (JSON): `username`, `password`.

```powershell
Invoke-RestMethod -Uri http://localhost:5000/login -Method POST -ContentType "application/json" -Body '{"username":"demo","password":"clave123"}'
```
Respuesta (200, credenciales correctas):
```json
{"status": "success", "message": "Login exitoso", "user_id": 1, "username": "demo"}
```
Respuesta (401, credenciales incorrectas):
```json
{"status": "error", "message": "Credenciales inválidas"}
```

## Notas técnicas

- La contraseña se guarda hasheada con Bcrypt (nunca en texto plano).
- La base de datos es SQLite (`site.db`), se crea sola al levantar el contenedor la
  primera vez — no requiere configuración externa.
- `Dockerfile`: se agregaron `--default-timeout=100 --retries 10` al `pip install` para
  tolerar cortes breves de red durante la construcción de la imagen.
