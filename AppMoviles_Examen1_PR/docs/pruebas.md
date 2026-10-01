# Plan y matriz de pruebas — PR #145 (sirena del especial de los policías)

**PR:** [gabrielhuav/PolitecnicoOpenWorld#145](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145) ·
**Issue:** [TheMike54/PolitecnicoOpenWorld#1](https://github.com/TheMike54/PolitecnicoOpenWorld/issues/1) ·
**Rama:** `feature/police-special-siren-sfx`

| Dato | Valor |
|---|---|
| SHA base (`main` del repositorio original) | `7ed32539` |
| SHA probado | `15e90090` |
| Versión de la app | 1.0.0.18 (build debug de la rama, instalada desde Android Studio) |
| Dispositivo | Emulador Android `Pixel_8_API_35` (API 35, 1080 × 2400, orientación horizontal fija del juego) |
| Equipo | Windows 11, Android Studio (JBR incluido), Android SDK 36 |
| Datos de prueba | No se usan cuentas ni datos personales: modo local sin sesión |

## 1. Criterios de aceptación y riesgos

| Criterio (issue #1) | Casos que lo cubren |
|---|---|
| **CA-1 (éxito):** cuando la policía CDMX, el Granadero o la Granadera lanzan su especial suena `special_power_siren` en lugar de `hadouken` | CP-01, CP-04 |
| **CA-2 (alterno/límite):** el policía CDMX hombre conserva su audio, los demás peleadores no cambian y el subtítulo solo aparece con los subtítulos de voz activados, en el idioma del dispositivo | CP-02, CP-03, CP-06 |

| Riesgo | Impacto | Caso que lo cubre |
|---|---|---|
| R-1: el pack compartido de los policías afecta también al policía hombre y pierde su audio propio | Regresión audible en un peleador jugable | CP-03 |
| R-2: el subtítulo aparece aunque el jugador tenga los subtítulos apagados | Texto no deseado en pantalla | CP-02 |
| R-3: el subtítulo no respeta el idioma de la app | Texto en el idioma incorrecto | CP-06 |
| R-4: con texto ampliado el subtítulo se corta o la app falla al recrearse | Problema de accesibilidad o cierre inesperado | CP-07 |
| R-5: salir y volver a entrar al modo deja el audio en mal estado | El especial deja de sonar | CP-05 |

## 2. Cómo se ejecutaron

Todas las pruebas usan la herramienta de desarrollador del juego: **Ajustes → Interfaz → Modo Desarrollador** y
**Subtítulos de voz**, luego **Titulación por Combate → Otros modos → Herramientas → Showcase: animaciones y sonidos**.
El Showcase recorre solo las animaciones de cada peleador (≈ 22 s) y avanza al siguiente; el contador de arriba dice
`AUTOJUEGO n/18` (10 = policía hombre, 11 = policía mujer, 12 = Granadero, 13 = Granadera).

- CP-01 y CP-02 se grabaron con audio. En CP-03 a CP-07 el emulador se controló por ADB (`adb shell input`,
  `adb shell screenrecord`); `screenrecord` no graba audio, así que en esos casos la evidencia es el **subtítulo**: el
  juego muestra la frase del clip que se reproduce (`voice_phrases.json`), por lo que `SIREN` / `SIRENA` identifica
  `special_power_siren` y la frase del policía hombre identifica su clip propio.
- La fuente del HUD (`sfHudSanitize`) pasa el texto a mayúsculas y quita signos: `(Siren)` se ve como `SIREN`.


## 3. Casos ejecutados

Fecha de ejecución de todos los casos: 30-sep-2026. SHA `15e90090`. Resultado: **7 de 7 aprobados**.

| ID | Tipo · criterio/riesgo | Autor de la ejecución | Precondiciones | Pasos | Resultado esperado | Resultado real | Estado | Evidencia |
|---|---|---|---|---|---|---|---|---|
| CP-01 | Ruta feliz · CA-1 | TheMike54 | Subtítulos de voz **activados**, idioma inglés | 1. Showcase → `11/18` (policía mujer). 2. Esperar el especial | Suena la sirena y aparece `SIREN` | Sonó la sirena y apareció `SIREN` abajo al centro durante el proyectil | Aprobado | [video con audio](evidencias/cp01-siren-subtitle-on.mp4) · [ajustes](evidencias/cp01-settings-subtitles-on.png) |
| CP-02 | Alterno · CA-2, R-2 | TheMike54 | Subtítulos de voz **desactivados** | Igual que CP-01 | Suena la sirena y **no** aparece texto | Sonó la sirena y no apareció texto | Aprobado | [video con audio](evidencias/cp02-siren-subtitle-off.mp4) · [ajustes](evidencias/cp02-settings-subtitles-off.png) |
| CP-03 | Regresión · CA-2, R-1 | TheMike54 | Subtítulos activados | 1. Showcase → `10/18` (policía hombre). 2. Esperar su especial | Aparece su frase propia; nunca `SIREN` | Apareció *"WE WORK AROUND THE CLOCK / ON ROAD SAFETY STRATEGIES"* (clip `special_policia_cdmx_hombre_power`); no apareció `SIREN` | Aprobado | [video](evidencias/cp03-policeman-own-special.mp4) · [captura](evidencias/cp03-policeman-own-special.jpg) |
| CP-04 | Ruta feliz (los 3) · CA-1 | TheMike54 | Subtítulos activados | Dejar correr el Showcase por `11/18`, `12/18` y `13/18` | `SIREN` en los tres especiales | `SIREN` en policía mujer, Granadero y Granadera | Aprobado | [policía mujer](evidencias/cp04-policewoman-siren.mp4) · [Granadero](evidencias/cp04-granadero-siren.mp4) · [Granadera](evidencias/cp04-granadera-siren.mp4) · capturas [1](evidencias/cp04-policewoman-siren.jpg) [2](evidencias/cp04-granadero-siren.jpg) [3](evidencias/cp04-granadera-siren.jpg) |
| CP-05 | Navegación y estado · R-5 | TheMike54 | Showcase en curso | 1. **Detener** (Stop). 2. Volver a entrar al Showcase. 3. Avanzar a `10/18` y dejar correr hasta `13/18` | Regresa al menú sin cierre; al reentrar todo sigue funcionando | Regresó al menú de Titulación; al reentrar, la corrida de CP-03 y CP-04 se hizo con normalidad | Aprobado | [menú](evidencias/cp05-stop-back-to-menu.png) · [reentrada](evidencias/cp05-reenter-showcase.jpg) |
| CP-06 | Compatibilidad (idioma) · CA-2, R-3 | TheMike54 | Idioma de la app: **Español** | Showcase → `11/18` | Aparece `SIRENA` | Apareció `SIRENA` | Aprobado | [ajuste de idioma](evidencias/cp06-settings-espanol.png) · [video](evidencias/cp06-policewoman-sirena-es.mp4) · [captura](evidencias/cp06-policewoman-sirena-es.jpg) |
| CP-07 | Accesibilidad y recreación · R-4 | TheMike54 | `font_scale` del sistema en 2.0 y luego 1.3 | 1. Cambiar el tamaño de fuente con el juego abierto. 2. Continuar. 3. Showcase → especial de un policía | La app no falla al recrearse y el subtítulo se sigue leyendo | Con 2.0 el cambio de configuración pausó el juego (PAUSA / Continuar) sin cerrarse; el subtítulo del HUD usa su propia fuente pixel y conserva su tamaño. Con 1.3 el Granadero mostró `SIRENA` legible | Aprobado | [pausa](evidencias/cp07-font-2x-pause.jpg) · [video 1.3×](evidencias/cp07-font-1.3x-granadero-sirena.mp4) · [captura](evidencias/cp07-font-1.3x-granadero-sirena.jpg) |

## 4. Hallazgos

Ninguno de los casos del cambio falló. Durante CP-07 aparecieron fallas en pantallas que este PR no modifica, por lo
que se registran como **preexistentes** y fuera del alcance del PR (no se volvieron a reproducir sobre `7ed32539`):

| ID | Hallazgo | Pasos | Esperado | Observado | Severidad | Evidencia | Estado |
|---|---|---|---|---|---|---|---|
| H-1 | El selector de peleador no se puede usar con la fuente del sistema al máximo | Ajustes de Android → tamaño de fuente 2.0 → Titulación por Combate | Se pueden ver y tocar **Confirmar** y **Otros modos** | La pantalla no se desplaza y esos dos controles quedan fuera de la vista | Media: bloquea la navegación a otros modos para quien usa texto grande | [captura 2.0](evidencias/cp07-font-2x-selector-wrap.jpg) · [con 1.3 sí se ven](evidencias/cp07-font-1.3x-selector-ok.png) | Preexistente |
| H-2 | Textos cortados con la fuente al máximo | Igual que H-1; abrir el Showcase | Etiquetas completas | Nombres partidos a media palabra en el selector y etiquetas de los botones del Showcase cortadas ("Anterio", "Siguien", "Velocid") | Baja: afecta legibilidad; el Showcase es solo para desarrollo | [captura](evidencias/cp07-font-2x-buttons-truncated.jpg) | Preexistente |
| H-3 | En el Showcase los audios de un peleador se enciman al cambiar de animación | Showcase → avanzar rápido entre animaciones | Un audio a la vez | Los clips de especial y victoria siguen sonando sobre el siguiente | Baja: herramienta de desarrollo; viene de la regla intencional de `SfVocesReglasTest` (los clips `power`/`win` no se interrumpen) | Lo noté al grabar CP-01 | Preexistente |

## 5. Cierre del QA

**Recomendación: integrar.** Los 7 casos pasaron sobre el SHA `15e90090`: los tres policías sin audio propio ahora
tienen la sirena, el policía hombre conserva el suyo (sin regresión), el subtítulo respeta el ajuste de subtítulos y el
idioma, y la app no falla al recrearse por un cambio de fuente.

