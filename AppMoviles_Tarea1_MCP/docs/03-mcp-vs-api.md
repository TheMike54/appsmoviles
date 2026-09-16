# 3. MCP frente a una API

Este es el punto central de la tarea. La idea que quiero dejar clara desde el inicio:
**MCP no es "la nueva versión" de las APIs ni las reemplaza. Son cosas distintas que trabajan en
capas distintas.**

## Primero: ¿qué es una interfaz?

Antes de hablar de APIs hay que entender qué es una **interfaz**, que básicamente es una
abstracción: una forma de usar algo sin tener que saber cómo funciona por dentro.

El ejemplo que a mí me sirve es el **volante de un carro**. Yo muevo el volante a la derecha y
el carro va a la derecha; lo muevo a la izquierda y va a la izquierda. No necesito entender cómo
funciona la dirección hidráulica para manejar. El volante es la interfaz: me esconde toda esa
complejidad y me deja una forma sencilla de usarla.

## ¿Qué es una API?

Una **API** (Application Programming Interface) es exactamente eso, pero entre programas: una
interfaz que permite que dos aplicaciones se comuniquen entre sí sin que una tenga que saber
cómo está hecha la otra por dentro. Para esta parte me apoyé en la explicación de EDteam (s.f.)
sobre qué es una API.

También se le describe como un **contrato**: define qué operaciones ofrece un servicio, qué
datos recibe y qué datos devuelve.

Lo importante es **quién decide** cómo se usa. Con una API, la persona que desarrolla:

1. Lee la documentación.
2. Decide qué endpoint llamar.
3. Arma la petición (método, ruta, datos).
4. Escribe el código que interpreta la respuesta.

Todo eso queda **escrito de antemano en el código**.

### Ejemplo con mi Práctica 2

En la Práctica 2 hice una API en Flask con endpoints como `POST /login` y `GET /tareas`. En la
app Android programé que, al presionar "Iniciar sesión", se llame a `POST /login` con el usuario
y la contraseña, y que después, con el token que regresa, se llame a `GET /tareas` para mostrar
la lista:

```kotlin
val respuesta = api.login(LoginRequest(usuario, contrasena))   // POST /login
val tareas = api.obtenerTareas("Bearer ${respuesta.token}")     // GET /tareas
```

La app no decide nada: siempre llama esos dos endpoints en ese orden porque yo lo escribí así.
Si mañana la API agrega un endpoint nuevo, mi app no se entera hasta que yo lea la
documentación, cambie el código y publique otra versión.

## ¿Qué es MCP?

MCP (Model Context Protocol) es un **protocolo abierto** que permite comunicar a una IA con
herramientas externas. Es lo que le da **"manos"** a un modelo, que por sí solo
únicamente recibe y devuelve texto.

Nate Gentile (s.f.-b) lo explica de una forma que me pareció muy clara: Anthropic, la empresa
detrás de Claude, creó el Model Context Protocol, que es básicamente **un protocolo para decirle
a un modelo cómo usar una herramienta**. Esa es la idea de fondo: no es una forma de que la IA
"sepa más", sino una forma estándar de decirle qué herramientas existen y cómo se ocupan.

Vale la pena aclarar algo aquí mismo: sí, lo creó Anthropic, pero **no es una tecnología de un
solo proveedor**. Es un protocolo abierto que en diciembre de 2025 se donó a la Agentic AI
Foundation, dentro de la Linux Foundation, y que hoy implementan Google, Microsoft y OpenAI,
entre otros (Linux Foundation, 2025).

Técnicamente se basa en **JSON-RPC 2.0**, y funciona así: un servidor publica un **catálogo de
herramientas**, donde cada herramienta tiene

- un **nombre** (`read_text_file`),
- una **descripción** en lenguaje natural ("Lee el contenido completo de un archivo como
  texto"),
- y un **esquema de parámetros** (qué argumentos recibe y de qué tipo).

El modelo **descubre ese catálogo mientras corre** (con `tools/list`) y **decide cuál usar**
según lo que el usuario pidió (con `tools/call`) (Model Context Protocol, 2026h).

Siguiendo la analogía del carro: con una API yo dejo escrito en el código qué botones se aprietan
y en qué orden; con MCP le entrego a la IA el tablero con los botones etiquetados y ella decide
cuál apretar según lo que le pedí.

Por ejemplo, si le escribo a Claude *"¿qué archivos hay en mi carpeta de trabajo?"*, nadie
programó que esa frase significa `list_directory`. El modelo leyó las descripciones del catálogo
y eligió esa herramienta.

Aquí hay que ser precisos con una cosa: **no es que con MCP nadie programe nada**. Alguien
programó el servidor y sus herramientas. Lo que ya no hay que programar es *la integración*: yo,
como usuario, no escribo código que diga "cuando pase X, llama a Y"; solo le digo a la IA lo que
quiero.

## Tabla comparativa

| | API tradicional | MCP |
|---|---|---|
| **¿Quién decide qué se llama?** | La persona que programa, y queda fijo en el código | El modelo, en el momento, según lo que le pidió el usuario |
| **¿Cómo se entera de lo que puede hacer?** | Hay que leer la documentación antes de programar | El servidor le manda su catálogo de herramientas mientras corre |
| **¿Qué tan pegado está el cliente al servicio?** | Mucho: si la API cambia, hay que cambiar el programa | Poco: si el servidor agrega herramientas, el modelo las ve solas |
| **¿Cómo son los mensajes?** | Cada API tiene su propio formato (rutas, métodos, JSON o XML) | Siempre igual: JSON-RPC 2.0 con métodos ya definidos (`tools/list`, `tools/call`) |
| **Permisos y consentimiento** | Cada API pide su propia autenticación (token, API key); avisarle al usuario es cosa de cada app | El usuario configura el servidor en su máquina y aprueba cada acción antes de que se ejecute |
| **¿Sirve en otras aplicaciones?** | Cada app tiene que escribir su propia integración | El mismo servidor funciona en Claude Desktop, VS Code, Cursor o Antigravity sin cambiarlo |

## MCP no sustituye a las APIs

Esto hay que decirlo explícitamente: **un servidor MCP casi siempre envuelve una API o un
recurso que ya existe.** MCP es una capa encima que lo hace descubrible y usable por un modelo.


Y hay otra razón, más práctica: **usar MCP en lugar de una API sería muy ineficiente.** Mi app
de la Práctica 2 necesita que el login funcione igual siempre y en milisegundos. Si eso pasara
por un MCP, cada vez que un usuario inicia sesión habría que llamar a un modelo de IA para que
decidiera qué hacer: más lento, más caro y sin garantía de que haga siempre lo mismo. Un flujo
fijo se programa con una API.

MCP tiene sentido cuando quien consume **es un modelo** y hace falta que descubra y decida:
"revisa mi correo", "busca en qué archivos de mi proyecto se usa esta función".

## Fuentes

- EDteam [@EDteam]. (s.f.). *¿Qué es una API? - La mejor explicación en español* [Video]. YouTube. https://www.youtube.com/watch?v=u2Ms34GE14U
- Linux Foundation. (2025, 9 de diciembre). *Linux Foundation announces the formation of the Agentic AI Foundation (AAIF), anchored by new project contributions including Model Context Protocol (MCP), goose and AGENTS.md*. https://www.linuxfoundation.org/press/linux-foundation-announces-the-formation-of-the-agentic-ai-foundation
- Nate Gentile [@NateGentile7]. (s.f.-b). *La IA tomó el control de mi ordenador (y no pude pararlo)* [Video]. YouTube. https://www.youtube.com/watch?v=_wAHiARD9GI
- Model Context Protocol. (2026a). *Specification* (versión 2026-07-28). https://modelcontextprotocol.io/specification/2026-07-28
- Model Context Protocol. (2026h). *Tools* (versión 2026-07-28). https://modelcontextprotocol.io/specification/2026-07-28/server/tools
- JSON-RPC Working Group. (2013). *JSON-RPC 2.0 specification*. https://www.jsonrpc.org/specification
