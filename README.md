# Practica 1 — Instalacion y Funcionamiento de los Entornos Moviles

## 1. Descripcion de las herramientas instaladas

| Herramienta | Versión | Sistema Operativo |
|---|---|---|
| Android Studio | AI-261.26222.65.2613.16025427 | Windows 11 |
| JDK (Amazon Corretto) | 17.0.20.1 LTS | Windows 11 |
| Maven | 3.9.16 | Windows 11 |
| Git | 2.54.0.windows.1 | Windows 11 |
| GitHub | Cuenta `TheMike54` · Repositorio público | Windows 11 |
| Flutter (SDK) | 3.47.2 (stable) | Windows 11 |
| Node.js | v24.15.0 | Windows 11 |
| Docker | 28.3.3 | Windows 11 |
| Android SDK | 36.0.0 | Windows 11 |

## 2. Los tres proyectos

- `hola_mundo_xml` — Android nativo con Views (XML). Proyecto creado con la plantilla Empty Views Activity en Kotlin.
- `hola_mundo_compose` — Android nativo con Jetpack Compose. Proyecto creado con la plantilla Empty Activity (Compose).
- `hola_mundo_flutter` — Flutter.

## 3. Estructura del repositorio

```
hola_mundo_xml/
hola_mundo_compose/
hola_mundo_flutter/
evidencias/
README.md
```

## 4. Instrucciones de instalacion y ejecucion

### 4.1 Instalacion de las herramientas

1. Android Studio: descargar e instalar desde developer.android.com. Al abrirlo, instalar el SDK de Android y crear el emulador.
2. JDK (Amazon Corretto): descargar la version mas reciente desde aws.amazon.com/corretto e instalarla. Verificar con `java -version`.
3. Maven: descargar el binario desde maven.apache.org, descomprimirlo y agregarlo a la variable de entorno PATH. Verificar con `mvn -v`.
4. Git: descargar e instalar desde git-scm.com. Verificar con `git --version`.
5. GitHub: crear una cuenta en github.com (si no se tiene) y un repositorio publico donde se almacenaran los proyectos del curso.
6. Flutter: descargar el SDK desde flutter.dev, agregarlo a la variable de entorno PATH y ejecutar `flutter doctor` para confirmar que las dependencias esten resueltas.
7. Node.js: descargar e instalar desde nodejs.org. Verificar con `node -v`.
8. Docker: instalar Docker Desktop. Verificar con `docker --version`.

## 5. Evidencias

Capturas guardadas en la carpeta `evidencias/`:

- `evidencias/Android XML.png` — aplicacion "Hola Mundo" en XML ejecutandose.
- `evidencias/ArduinoJetpackCompouse.png` — aplicacion "Hola Mundo" en Jetpack Compose ejecutandose.
- `evidencias/Flutter.png` — aplicacion "Hola Mundo" en Flutter ejecutandose.
- `evidencias/ArduinoIDEEmulador.png` — emulador ejecutando la aplicacion "Hello Android".
- `evidencias/Version de herramientas.png` — verificacion de versiones en terminal.
- `evidencias/Version de herramientas 2.0.png` — verificacion de versiones en terminal.

## 6. Dificultades encontradas

No tuve ninguna dificultad, la mayoria de las herramientas ya las tenia instaladas, por lo que fue bastante facil la instalacion de todo. El unico problema que tengo es que cuando quiero ejecutar el emulador con un proyecto de Flutter no funciona.

## 7. Comparacion de los tres enfoques

En cuanto a facilidad, el que me resulto mas facil fue Flutter; despues yo pondria XML, ya que lo habia usado anteriormente en la Voca, por lo que recorde que tenia que arrastrar los elementos y despues mandarlos a llamar en el codigo. En cuanto a Jetpack Compose, no sabria decir si es mas facil o dificil, ya que fue la primera vez que lo use.

Ahora, en cantidad de codigo, claramente Flutter es el ganador, ya que es el que menos codigo necesito. En XML la diferencia es que hay que hacer mas codigo para mandar a llamar a los elementos, y Jetpack Compose es como el punto medio.

En cuanto a diseno de UI, el mas intuitivo es XML, ya que puedes arrastrar y agregar elementos de forma sencilla, y los otros no tienen esa posibilidad.

## 8. Conclusiones

En general, muy interesante la practica, me trajo recuerdos de la prepa. En lo personal probe a Gemini dentro de Android Studio y funciona muy bien, me fue guiando en el paso a paso de como hacer las cosas y al estar dentro de Android Studio fue muy comodo y sencillo. Tambien estuve jugando con el emulador de Android y tambien me funciona excelente, a excepcion de cuando lo quiero abrir en una aplicacion de Flutter. Otra cosa que probe, aunque no tiene nada que ver con la practica, fue Antigravity, ya que el profesor lo menciono y si habia escuchado de Antigravity, pero nunca lo habia probado.
