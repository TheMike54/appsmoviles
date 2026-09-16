# Tarea 1 — MCP y sistema de archivos: investigación e implementación

- **Nombre:** Miguel Ángel Rodríguez Candelario
- **Número de boleta:** 2024630606
- **Grupo:** 7CV4
- **Asignatura:** Desarrollo de Aplicaciones Móviles Nativas
- **Profesor:** Gabriel Hurtado Avilés
- **Fecha de entrega:** lunes 21 de septiembre de 2026

## Resumen de la actividad

Esta tarea tiene dos partes. La primera es investigar cómo un modelo de lenguaje pasó de
solo recibir y devolver texto a poder operar sobre archivos locales, y entender con
precisión qué es el Model Context Protocol (MCP) y en qué se distingue de consumir una
API tradicional. La segunda es la parte práctica: instalar de verdad un servidor MCP de
sistema de archivos en un cliente (elegí Claude Desktop), verificar que funciona, hacer
las operaciones básicas sobre una carpeta autorizada y comprobar que el servidor
**no** deja salir de esa carpeta.

Especificación de MCP consultada en todo el trabajo: versión **2026-07-28**.

### Estructura del repositorio

| Carpeta | Contenido |
|---|---|
| `docs/` | Investigación (Parte 1), un archivo `.md` por punto |
| `config/` | Archivo de configuración utilizado, sin credenciales |
| `img/` | Capturas de pantalla (evidencias) |
| `presentacion/` | Diapositivas de la exposición |


### Índice de la investigación

1. [Evolución de los modelos](docs/01-evolucion-de-los-modelos.md) — de modelo de lenguaje a LLM, y qué es el razonamiento explícito.
2. [El problema del aislamiento](docs/02-problema-del-aislamiento.md) — por qué un LLM no puede tocar archivos por sí solo.
3. [MCP frente a una API](docs/03-mcp-vs-api.md) — el punto central de la tarea, con la tabla comparativa.
4. [Arquitectura de MCP](docs/04-arquitectura-mcp.md) — host/cliente/servidor, primitivas y transportes.
5. [El servidor de sistema de archivos](docs/05-servidor-filesystem.md) — qué herramientas expone y cómo delimita su alcance.
6. [Seguridad](docs/06-seguridad.md) — riesgos, mitigaciones y los dos "candados".
7. [Casos de uso](docs/07-casos-de-uso.md) — herramientas reales que usan MCP hoy.

## Tabla comparativa: MCP frente a una API

(Desarrollada a fondo, con ejemplos, en [`docs/03-mcp-vs-api.md`](docs/03-mcp-vs-api.md).)

| | API tradicional | MCP |
|---|---|---|
| **¿Quién decide qué se llama?** | La persona que programa, y queda fijo en el código | El modelo, en el momento, según lo que le pidió el usuario |
| **¿Cómo se entera de lo que puede hacer?** | Hay que leer la documentación antes de programar | El servidor le manda su catálogo de herramientas mientras corre |
| **¿Qué tan pegado está el cliente al servicio?** | Mucho: si la API cambia, hay que cambiar el programa | Poco: si el servidor agrega herramientas, el modelo las ve solas |
| **¿Cómo son los mensajes?** | Cada API tiene su propio formato (rutas, métodos, JSON o XML) | Siempre igual: JSON-RPC 2.0 con métodos ya definidos (`tools/list`, `tools/call`) |
| **Permisos y consentimiento** | Cada API pide su propia autenticación (token, API key); avisarle al usuario es cosa de cada app | El usuario configura el servidor en su máquina y aprueba cada acción antes de que se ejecute |
| **¿Sirve en otras aplicaciones?** | Cada app tiene que escribir su propia integración | El mismo servidor funciona en Claude Desktop, VS Code, Cursor o Antigravity sin cambiarlo |

**MCP no sustituye a las APIs.** Un servidor MCP casi siempre envuelve una API o un
recurso que ya existía; es una capa encima que lo hace descubrible y usable por un
modelo. El detalle completo, con el ejemplo de por qué el login de mi Práctica 2 tiene
que seguir siendo una API fija y no un servidor MCP, está en el documento 3.

## Instalación y verificación (reproducible)

### Lo que usé

- **Sistema operativo:** Windows 11 Home, build 10.0.26200.
- **Cliente MCP:** Claude Desktop — versión de la app **2.110.0.0**, instalada desde
  Microsoft Store (paquete MSIX). Lo elegí porque es la herramienta con la que estoy más
  familiarizado (ya la uso a diario) y porque cuento con una suscripción activa.
- **Node.js:** v24.15.0 (trae `npx` y `npm` 11.12.1).
- **Servidor MCP:** `@modelcontextprotocol/server-filesystem`, versión **2026.8.31**
  (la más reciente publicada en npm al momento de hacer la tarea), ejecutado bajo demanda
  con `npx`, sin instalarlo global.

### Paso 1 — Crear la carpeta de trabajo autorizada

Se necesita una carpeta creada específicamente para esta tarea (no la raíz del disco ni
la carpeta de usuario completa). Usé:

```
AppMoviles_Tarea1_MCP/espacio-trabajo/
```

Con 3 archivos de ejemplo dentro: `bienvenida.txt`, `lista-pendientes.md` y
`notas-clase.md`.

### Paso 2 — Configurar el servidor en Claude Desktop

En este paso le tuve que pedir ayuda al mismísimo Claude, porque nos dimos cuenta de que
hay una pequeña diferencia entre la versión de Claude Desktop instalada desde la
Microsoft Store y la instalada desde el sitio web de Claude: **la ruta del archivo de
configuración depende de cuál de las dos tengas.**

- Si se instaló con el instalador clásico (`.exe`), la ruta es:
  `%APPDATA%\Claude\claude_desktop_config.json`
- Si se instaló desde **Microsoft Store** (como en mi caso — se ve porque Claude Desktop
  corre como app empaquetada MSIX), esa ruta normal **no existe y Claude Desktop no la
  usa**. Windows redirige el `%APPDATA%` de las apps empaquetadas a una carpeta
  "sandboxeada" propia:
  `%LOCALAPPDATA%\Packages\Claude_<id del paquete>\LocalCache\Roaming\Claude\claude_desktop_config.json`

  El `<id del paquete>` cambia por instalación; se puede confirmar así en PowerShell:

  ```powershell
  Get-AppxPackage -Name "*Claude*" | Select-Object Name, PackageFullName
  ```

  O revisando qué carpeta abre el botón **"Edit config"** dentro de Claude Desktop en
  **Settings → Developer** — ese botón siempre abre la ruta correcta para tu instalación.

Dentro de ese archivo (que Claude Desktop ya trae con otras preferencias propias de la
app, como el emparejamiento con la extensión de Chrome — **no hay que borrar nada de
eso**), se agrega la clave `mcpServers` como hermana de lo que ya exista:

```json
{
  "mcpServers": {
    "filesystem": {
      "command": "cmd",
      "args": [
        "/c",
        "npx",
        "-y",
        "@modelcontextprotocol/server-filesystem@2026.8.31",
        "RUTA\\A\\TU\\espacio-trabajo"
      ]
    }
  }
}
```

En Windows hace falta envolver el comando con `cmd /c` porque, si Claude Desktop intenta
ejecutar `npx` directamente sin pasar por una shell, normalmente no lo encuentra aunque
sí esté en el `PATH`.

La copia de este archivo tal como quedó en mi máquina (con la ruta real, sin
credenciales) está en [`config/claude_desktop_config.json`](config/claude_desktop_config.json).

### Paso 3 — Reiniciar y verificar

Hay que cerrar Claude Desktop **por completo** (desde el ícono de la bandeja del
sistema, no solo la ventana) y volver a abrirlo para que lea el archivo nuevo.

Para confirmar que lo reconoció: **Settings → Developer → Local MCP servers** debe
mostrar `filesystem` en estado `Running`. También aparece como conector activo (con su
propio toggle) en el menú `+` del cuadro de mensaje, junto a los demás conectores.

![Servidor filesystem reconocido, estado Running](img/01-servidor-reconocido.png)

![Conector filesystem activo en el chat](img/02-conector-activo.png)

## Evidencias

El profesor pidió capturas de todo el proceso, así que además del resultado de cada
operación incluyo el diálogo de confirmación que Claude Desktop pide antes de usar cada
herramienta (el "candado" del lado del cliente, ver `docs/06-seguridad.md`). Las dejo en
tamaño reducido para que la tabla no se haga kilométrica; se puede abrir cada una en
grande dándole clic.

| # | Operación | Petición | Confirmación(es) | Resultado |
|---|---|---|---|---|
| 1 | Listar directorio | *"Lista el contenido de la carpeta espacio-trabajo"* | <a href="img/03a-listar-confirmacion1.png"><img src="img/03a-listar-confirmacion1.png" width="160"></a> <a href="img/03b-listar-confirmacion2.png"><img src="img/03b-listar-confirmacion2.png" width="160"></a> | ![Listar](img/03c-listar-resultado.png) |
| 2 | Leer archivo | *"Lee el archivo notas-clase.md"* | <a href="img/04a-leer-confirmacion.png"><img src="img/04a-leer-confirmacion.png" width="160"></a> | ![Leer](img/04b-leer-resultado.png) |
| 3 | Crear archivo | *"Crea un archivo llamado prueba-mcp.txt con el texto: Este archivo fue creado por Claude Desktop a través del servidor MCP."* | <a href="img/05a-crear-confirmacion.png"><img src="img/05a-crear-confirmacion.png" width="160"></a> | ![Crear](img/05b-crear-resultado.png) |
| 4 | Modificar archivo | *"En lista-pendientes.md, marca como completadas las tareas de listar y leer"* | <a href="img/06a-modificar-confirmacion-lectura.png"><img src="img/06a-modificar-confirmacion-lectura.png" width="160"></a> <a href="img/06b-modificar-confirmacion-edicion.png"><img src="img/06b-modificar-confirmacion-edicion.png" width="160"></a> | ![Modificar](img/06c-modificar-resultado.png) |
| 5 | Buscar archivo | *"Busca qué archivo contiene la palabra JSON-RPC"* | <a href="img/07a-buscar-confirmacion.png"><img src="img/07a-buscar-confirmacion.png" width="160"></a> | ![Buscar](img/07b-buscar-resultado.png) |

Nota sobre la operación de "listar": Claude Desktop pidió **dos** confirmaciones porque
usó dos herramientas para responder — primero `list_allowed_directories` (para saber a
qué carpeta tiene acceso) y luego sí `list_directory` sobre esa carpeta. Lo mismo pasa en
"modificar": primero tiene que leer el archivo (`read_text_file`) antes de poder editarlo
(`edit_file`), así que pide permiso para las dos herramientas por separado.

En la operación de búsqueda salió un detalle que vale la pena dejar anotado: la
herramienta `search_files` de este servidor busca **por nombre o patrón de archivo, no
por contenido** (así lo documenté también en `docs/05-servidor-filesystem.md`). Cuando le
pedí buscar una palabra dentro del contenido, Claude Desktop no pudo usar esa herramienta
para eso — en vez de fallar o inventar una respuesta, leyó los 4 archivos uno por uno con
`read_multiple_files` y así encontró la coincidencia. Es un buen ejemplo real de la
diferencia entre "qué herramientas existen" y "qué puede lograr el modelo combinándolas".

### Prueba del límite de seguridad

Le pedí a Claude Desktop leer un archivo real que existe en mi máquina pero está fuera de
`espacio-trabajo`: el propio `claude_desktop_config.json` de la app.

![Confirmación pedida antes de intentar el acceso fuera de la carpeta](img/08-limite-seguridad-confirmacion.png)

![El servidor rechaza la lectura: Access denied - path outside allowed directories](img/09-limite-seguridad-resultado.png)

El rechazo no vino de que Claude Desktop "decidiera no hacerlo": el mensaje de error es
del **servidor** (`Access denied - path outside allowed directories`), que valida cada
ruta contra la lista de directorios permitidos antes de tocar el disco. Es justo el
segundo "candado" que documenté en `docs/06-seguridad.md`: aunque yo hubiera aprobado la
herramienta sin pensarlo, el servidor igual la habría bloqueado, porque ese candado no
depende de mí.

## Conclusiones

Esta era mi primera vez usando a Claude de esta forma. Ya estaba acostumbrado a trabajar
con Claude Code y, la verdad, casi siempre corriéndolo con el modo que se salta las
confirmaciones de permisos (`--dangerously-skip-permissions`). Desde mi experiencia se
siente considerablemente más rápido trabajar así, aunque hacer eso puede ocasionar que la IA borre algún archivo o algo parecido. Hacer esta tarea con
Claude Desktop, donde cada herramienta pide confirmación una por una, es un proceso "lento" paso a paso, con el diálogo de "Allow once / Always allow" en
cada operación — y entendí mejor por qué existe: es justo el candado que documenté en
`docs/06-seguridad.md`, el que depende de que un humano esté prestando atención.

Lo más interesante de toda la parte práctica fue la prueba del límite de seguridad. No
solo el servidor rechazó el acceso al archivo fuera de la carpeta — eso ya lo esperaba,
para eso es la prueba — sino que Claude Desktop, después del rechazo, se dio cuenta por
su cuenta de que eso era justamente la prueba de seguridad que me faltaba de mi propia
lista de pendientes (`lista-pendientes.md`) y hasta me lo hizo notar, incluyendo que el
archivo que intenté leer era, de casualidad, la configuración de la propia Claude
Desktop. Ver en vivo los dos candados funcionando juntos (uno me pide confirmar, el otro
bloquea aunque yo confirme) hizo mucho más concreto algo que en la investigación se queda
en teoría.

## Bibliografía

Fuentes citadas directamente en este README. El resto de las fuentes consultadas para la
investigación está en la sección "Fuentes" de cada documento dentro de `docs/`.

- Model Context Protocol. (2026a). *Specification* (versión 2026-07-28). https://modelcontextprotocol.io/specification/2026-07-28
- Model Context Protocol. (2026j). *Filesystem MCP Server* [README, versión 2026.8.31]. GitHub. https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
