# 7. Casos de uso

MCP no es exclusivo de una aplicación ni de un servidor. Aquí van tres herramientas actuales que
lo implementan, empezando por las que yo he usado personalmente.

## Claude Code + Claude para Chrome (mi caso más útil)

**Claude Code** es un agente de programación que trabaja desde la terminal, y **Claude para
Chrome** es una extensión que se conecta como servidor MCP y le da al modelo herramientas para
manejar el navegador: abrir páginas, leer su contenido, hacer clic, llenar campos.

Cuando salió, lo primero que hice fue configurarlo y probarlo con algo tonto para ver si de
verdad funcionaba: le pedí que me pusiera un video de YouTube. Y lo hizo.

Lo verdaderamente útil vino después. En las vacaciones pasadas estaba planeando el viaje y me
apoyé en esta combinación para que me buscara **vuelos, hoteles y actividades** del lugar a
donde iba a ir. La petición era bastante pesada: **tres destinos** distintos y, para cada uno,
el precio del vuelo, el precio del hotel, qué ofrecía el hotel por ese precio, qué actividades
se podían hacer, cuánto costaban y qué reseñas tenían, todo para poderlos comparar.

Se tardó bastante, como era de esperarse por el tamaño de la petición, pero gracias al navegador
sacó **precios reales** de las páginas, no inventados ni de su memoria de entrenamiento. Con esa
comparación terminé eligiendo destino, vuelo y hotel, y quedé muy conforme tanto con el precio
como con el hotel y las actividades.

Esto es justo lo que hace distinto a MCP: yo nunca programé nada que dijera "busca en esta
página y compara precios". Solo pedí lo que quería, y el modelo fue eligiendo qué herramientas
del navegador usar en cada paso.

## Superhuman Mail (conector de correo)

También he usado un servidor MCP de correo, **Superhuman Mail**, que es un servidor remoto al
que se le da acceso con la cuenta. Lo conecté para hacer una limpieza que a mano me hubiera
llevado horas: **borrar y bloquear todo el spam** de mis correos principales, donde me llegaba
muchísimo. Funcionó muy bien.

Aquí sí hay que tener cuidado, y vale la pena decirlo: le estás dando acceso a varias cuentas de
correo personales, con todo lo que hay dentro. Hay que revisar lo que va a hacer antes de
aceptar, porque un borrado mal entendido se lleva correos que sí querías. Es exactamente el
riesgo del que hablo en [Seguridad](06-seguridad.md), pero con el correo en lugar de los
archivos.

## Claude Desktop (el cliente de esta tarea)

Es la aplicación de escritorio de Claude para Windows y macOS. Los servidores se declaran en
`claude_desktop_config.json` y cada acción se aprueba antes de ejecutarse (Model Context
Protocol, 2026g). Es el cliente donde instalé el servidor de sistema de archivos para esta
tarea.

## Otras herramientas del ecosistema

| Herramienta | Qué es | Cómo usa MCP |
|---|---|---|
| **Visual Studio Code** (con el modo agente de Copilot) | El editor de código de Microsoft | Los servidores se configuran en `.vscode/mcp.json` por proyecto, y VS Code pide confirmar que confías en el servidor antes de iniciarlo (Microsoft, 2026) |
| **Google Antigravity** | Plataforma de desarrollo agéntico de Google; su editor es un fork de VS Code | Lo usa como puente con el entorno: consultar esquemas de bases de datos, logs o APIs, y ejecutar acciones en servicios como GitHub o Linear. Se configura en `~/.gemini/config/mcp_config.json` o en `.agents/mcp_config.json` (Google, 2026) |



## No todo lo que conecta a la IA con tu computadora es MCP

Esto me quedó claro con **Dispatch**, otra función de Claude que probé. Dispatch permite
vincular el celular con la computadora (escaneando un código QR) para mandarle instrucciones a
la aplicación de escritorio aunque uno no esté frente al equipo: le pides algo desde el
teléfono, te vas a hacer otra cosa y luego encuentras el trabajo hecho en la computadora
(DiarioBitcoin, 2026).

Es el que menos he usado. Cuando lo probé sí funcionaba y sí tomaba el control de la
computadora, pero se tardaba bastante, consumía mucho uso de la suscripción y a veces
simplemente no lograba pasarme un archivo. Se entiende, porque Anthropic la lanzó como
*research preview*, o sea, como una función en fase de prueba.

Lo interesante para esta tarea es la distinción:

| | Dispatch | MCP |
|---|---|---|
| Qué conecta | Mi **celular** con mi **computadora** | Un **modelo** con **herramientas** |
| Qué es | Un canal para mandarle instrucciones a la aplicación de escritorio | Un protocolo abierto para publicar y usar herramientas |
| Quién lo define | Anthropic, para su propio producto | La especificación de MCP, y lo implementan varios clientes y servidores |

Aunque las dos cosas terminen en "la IA hace algo en mi computadora", no son lo mismo. Dispatch
es una función de un producto; MCP es el protocolo con el que cualquier cliente puede conectarse
a cualquier servidor de herramientas. Confundirlos sería como confundir el control remoto de la
tele con el cable HDMI.

## ¿Cómo editan repositorios completos sin subir archivos?



### El papel de Git

Git es el centro de todo esto. De hecho si no mal recuerdo, cuando yo instalé Claude
Code me lo pidió como requisito.

Hoy le puedo decir a Claude Code, codex, o a Antigravity CLI , que haga el `git commit` y el `git push origin main`, e incluso que
cree o elimine repositorios. Es muy cómodo, porque el ciclo completo (escribir el código,
revisarlo y subirlo) se queda en la misma conversación.

Y aquí va una aclaración importante para no confundir las cosas: **esos comandos de Git no pasan
por MCP.** El agente los ejecuta con su propia herramienta de comandos, igual que si yo los
escribiera en la terminal. Sería MCP si, en lugar de eso, se conectara a un servidor MCP de
GitHub que expusiera herramientas como "crear repositorio" o "abrir un pull request". Las dos
formas existen y sirven, pero solo la segunda es MCP.

Hay que ser precisos en una cosa: editores como Claude Code, VS Code o Antigravity también traen
**herramientas propias** para leer y editar archivos, que no pasan por MCP. MCP es el mecanismo
**estándar** para agregarles capacidades nuevas (un navegador, el correo, una base de datos, otra
carpeta) sin que cada herramienta tenga que programar su propia integración. Y como es estándar,
el mismo servidor sirve en todas.

## Fuentes

- Anthropic. (2026). *Connect Claude Code to tools via MCP*. Claude Code Docs. https://code.claude.com/docs/en/mcp
- DiarioBitcoin. (2026). *Claude ya puede usar tu computadora y recibir tareas desde el teléfono con Dispatch*. https://www.diariobitcoin.com/tecnologia/claude-ya-puede-usar-tu-computadora-y-recibir-tareas-desde-el-telefono-con-dispatch/
- Google. (2026). *MCP*. Google Antigravity Docs. https://antigravity.google/docs/mcp
- Microsoft. (2026). *Use MCP servers in VS Code*. Visual Studio Code Docs. https://code.visualstudio.com/docs/copilot/customization/mcp-servers
- Model Context Protocol. (2026g). *Connect to local MCP servers*. https://modelcontextprotocol.io/docs/develop/connect-local-servers
