# 5. El servidor de sistema de archivos

## ¿Qué es el sistema de archivos (FS)?

El sistema de archivos, o *file system* (FS), es la forma en que el sistema operativo organiza y
guarda la información en el disco: carpetas, archivos y sus nombres, tamaños y permisos. Es lo
que uno ve al abrir el explorador de Windows.

Entonces, el "servidor MCP de sistema de archivos" es simplemente un servidor que le da a la IA
herramientas para trabajar con eso: listar carpetas, leer archivos, crearlos, modificarlos y
buscarlos.

## "FS" no es parte del protocolo

Es común escuchar "el MCP de FS" como si fuera una pieza de MCP. No lo es. **La especificación
de MCP no define ninguna herramienta de archivos.** Lo que define es cómo un servidor anuncia
sus herramientas y cómo se invocan.

El servidor de sistema de archivos (*Filesystem MCP Server*, paquete npm
`@modelcontextprotocol/server-filesystem`) es uno de los **servidores de referencia** que
mantiene el proyecto MCP en el repositorio `modelcontextprotocol/servers`, como ejemplo de
implementación. Es uno entre muchos servidores posibles: hay servidores para Git, bases de
datos, navegadores, GitHub, etc., y cualquiera puede escribir el suyo.

Versión que instalé: **2026.8.31**.

## Herramientas que expone

Este servidor solo expone **herramientas** (tools); no ofrece recursos ni plantillas de prompt
(Model Context Protocol, 2026j). Cada herramienta trae anotaciones que indican si es de solo
lectura o si puede modificar/destruir datos:

| Operación | Herramienta | Qué hace | Tipo |
|---|---|---|---|
| Listar un directorio | `list_directory` | Lista archivos y carpetas marcándolos como `[FILE]` o `[DIR]` | Solo lectura |
| | `list_directory_with_sizes` | Igual, pero con tamaños | Solo lectura |
| | `directory_tree` | Estructura recursiva del directorio en JSON | Solo lectura |
| Leer | `read_text_file` | Lee un archivo completo como texto UTF-8 | Solo lectura |
| | `read_media_file` | Lee una imagen o audio y lo regresa en base64 con su tipo MIME | Solo lectura |
| | `read_multiple_files` | Lee varios archivos a la vez | Solo lectura |
| Escribir / crear | `write_file` | Crea un archivo nuevo o **sobrescribe** uno existente | Escritura (destructiva) |
| | `create_directory` | Crea una carpeta (si ya existe, no hace nada) | Escritura |
| Modificar | `edit_file` | Hace ediciones selectivas buscando y reemplazando texto | Escritura (destructiva) |
| Mover | `move_file` | Mueve o renombra archivos y carpetas | Escritura (destructiva) |
| Buscar | `search_files` | Busca de forma recursiva con patrones glob | Solo lectura |
| Información | `get_file_info` | Metadatos: tamaño, fechas, permisos | Solo lectura |
| | `list_allowed_directories` | Muestra qué directorios tiene permitidos el servidor | Solo lectura |

## ¿Cómo se delimita su alcance?

El servidor **solo puede operar dentro de los directorios permitidos**. Se definen de dos formas
(Model Context Protocol, 2026j):

1. **Argumentos de línea de comandos.** Las rutas que se ponen al final del comando son las
   carpetas permitidas. Es lo que usé:

   ```json
   "args": ["-y", "@modelcontextprotocol/server-filesystem", "C:\\ruta\\espacio-trabajo"]
   ```

2. **Roots.** Si el cliente soporta Roots, le manda al servidor la lista de directorios, y esa
   lista **reemplaza** a la de los argumentos. (Recordar que Roots quedó *deprecated* en la
   especificación 2026-07-28; ver [Arquitectura](04-arquitectura-mcp.md).)

Si el servidor arranca **sin** argumentos y el cliente **no** soporta Roots, el servidor da error
y no inicia: no existe un modo "acceso a todo" por defecto.

Cada vez que llega una petición, el servidor revisa que la ruta pedida esté dentro de uno de
los directorios permitidos. Si no, rechaza la operación y
regresa un error. **Esa validación la hace el servidor**, no el modelo; aunque el modelo "quiera"
leer otra ruta, el servidor no lo deja. Esto se demuestra en la prueba del límite de seguridad
del README.

## ¿Por qué existe ese límite y qué pasaría sin él?

El servidor corre como un proceso normal **con los permisos de mi usuario de Windows**. La guía
oficial lo advierte: puede hacer cualquier operación sobre archivos que yo podría hacer
manualmente (Model Context Protocol, 2026g). El límite de directorios es lo único que lo
restringe.

Sin ese límite:

- El modelo podría **leer archivos sensibles** fuera del proyecto: documentos personales,
  llaves SSH, archivos `.env` con contraseñas, configuraciones del navegador.
- Un error del modelo, o una **inyección de instrucciones** escondida en un archivo, podría
  terminar en **sobrescribir o mover archivos importantes** en cualquier parte del disco.
- Esa información leída viaja como texto al modelo en la nube, así que también sería una fuga de
  datos.



## Fuentes


- Model Context Protocol. (2026g). *Connect to local MCP servers*. https://modelcontextprotocol.io/docs/develop/connect-local-servers
- Model Context Protocol. (2026j). *Filesystem MCP Server* [README, versión 2026.8.31]. GitHub. https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
