# Primer examen parcial — Pull Request con aseguramiento de calidad

| | |
|---|---|
| **Alumno** | Miguel Ángel Rodríguez Candelario (GitHub: [TheMike54](https://github.com/TheMike54)) |
| **Grupo** | 7CV4 |
| **Asignatura** | Desarrollo de Aplicaciones Móviles Nativas |
| **Profesor** | Gabriel Hurtado Avilés |
| **Equipo** | Miguel Ángel Rodríguez Candelario (TheMike54) · Víctor Moreno López ([VictorMoreno-Code](https://github.com/VictorMoreno-Code)) |
| **Fecha de entrega** | 1 de octubre de 2026 |

## Enlaces de la entrega

| Qué | Enlace |
|---|---|
| Pull request | [gabrielhuav/PolitecnicoOpenWorld#145](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145) |
| Issue | [TheMike54/PolitecnicoOpenWorld#1](https://github.com/TheMike54/PolitecnicoOpenWorld/issues/1) |
| Rama de trabajo | [`feature/police-special-siren-sfx`](https://github.com/TheMike54/PolitecnicoOpenWorld/tree/feature/police-special-siren-sfx) en mi fork |
| SHA base | `7ed32539` (`main` de `gabrielhuav/PolitecnicoOpenWorld`) |
| SHA probado | `15e90090` |
| SHA final entregado | `15e90090` (el mismo que se probó) |
| Matriz de pruebas | [docs/pruebas.md](docs/pruebas.md) |
| Evidencias | [docs/evidencias/](docs/evidencias/) |

## 1. Objetivo y alcance

En el modo de peleas **Titulación por Combate**, tres de los cuatro policías (la policía CDMX, el Granadero y la
Granadera) no tenían sonido propio para su poder especial: al no tener una línea `power` en su pack de voces ni un
archivo `special_<id>.m4a`, el juego usaba el `hadouken` genérico. El policía CDMX hombre sí tenía el suyo.

El cambio les da a los tres un efecto compartido de **sirena + impacto** (`special_power_siren.m4a`, 1.5 s), igual
que Escomboy y Escomgirl ya comparten `special_power_electricity`, y un subtítulo corto `(Sirena)` / `(Siren)` que
aparece solo con los subtítulos de voz activados (lo sugirió el profesor al revisar el PR en clase el 28 de
septiembre).

**Archivos del PR (6):** el audio nuevo, `StreetFighterViewModel.kt` (`power = sirenPower` en los packs `polM`, `grH`
y `grM`), `voice_phrases.json`, `SfArcadeCampaignAuditTest.kt`, `AUDIO_INVENTARIO_SF.md` y `README.md` del proyecto.


**Criterios de aceptación** (issue #1):
1. **Éxito:** cuando la policía, el Granadero o la Granadera lanzan su especial, suena `special_power_siren` en lugar
   de `hadouken`.
2. **Alterno / límite:** el policía hombre conserva su audio, los demás peleadores no cambian y el subtítulo solo
   aparece con los subtítulos de voz activados, en el idioma del dispositivo.

## 2. Versión de referencia y entorno

- Fork sincronizado con `main` del original; SHA base `7ed32539`. Antes de cambiar código se ejecutó esa versión y se
  grabó el especial de la policía sonando con el `hadouken` (video "Before" en el PR).
- Entorno: Windows 11, Android Studio con su JDK incluido, Android SDK 36, emulador `Pixel_8_API_35` (API 35). Se abrió
  la carpeta interna `PolitecnicoOpenWorld/` y se compiló la versión debug sin actualizar dependencias.
- Configuración local: `secrets.properties` con `MAPS_API_KEY=DEFAULT_API_KEY` (valor de ejemplo que documenta el
  propio proyecto). No se subió ninguna llave, `local.properties` ni credenciales.

## 3. Historial de commits

| Commit | Mensaje | Qué hace |
|---|---|---|
| `3670a0ba` | `feat(sf): give the police fighters' special move its own siren SFX` | Audio nuevo, packs de voces, test de auditoría y documentación |
| `15e90090` | `feat(sf): add a (Siren) subtitle to the police special SFX` | Subtítulo `(Sirena)` / `(Siren)` pedido en la revisión del profesor |

## 4. Pruebas (QA)

La matriz completa, con criterios, riesgos, pasos, resultados y evidencia de cada caso, está en
[docs/pruebas.md](docs/pruebas.md). Resumen: **7 casos ejecutados, 7 aprobados** sobre el SHA `15e90090`.

| ID | Tipo | Resultado | Evidencia |
|---|---|---|---|
| CP-01 | Ruta feliz | Policía mujer: sirena + `SIREN` | [video](docs/evidencias/cp01-siren-subtitle-on.mp4) |
| CP-02 | Alterno | Subtítulos desactivados: sirena sin texto | [video](docs/evidencias/cp02-siren-subtitle-off.mp4) |
| CP-03 | Regresión | El policía hombre conserva su audio | [video](docs/evidencias/cp03-policeman-own-special.mp4) |
| CP-04 | Ruta feliz (los tres) | Policía, Granadero y Granadera: `SIREN` | [video](docs/evidencias/cp04-granadera-siren.mp4) |
| CP-05 | Navegación y estado | Detener y volver al Showcase sin fallas | [captura](docs/evidencias/cp05-reenter-showcase.jpg) |
| CP-06 | Compatibilidad (idioma) | App en español: `SIRENA` | [video](docs/evidencias/cp06-policewoman-sirena-es.mp4) |
| CP-07 | Accesibilidad | Con fuente grande no falla y el subtítulo se lee | [video](docs/evidencias/cp07-font-1.3x-granadero-sirena.mp4) |

**Hallazgos:** ninguno en el cambio. Se registraron tres fallas preexistentes en pantallas que el PR no toca (selector
de peleador con la fuente al máximo, textos cortados y audios encimados en el Showcase); detalle en la sección 4 de
la matriz.

**Dictamen de calidad:** recomiendo integrar el cambio. Los riesgos que quedan están en la sección 5 de la matriz.

## 5. Verificaciones automáticas (checks)

El workflow **PR Quality Gate** (`.github/workflows/pr-quality-gate.yml` del SHA base) se activa en PR hacia `main`
con cambios en `PolitecnicoOpenWorld/`. Tiene dos trabajos:

- **unit-tests:** compila la app en debug (`:app:assembleDebug`), corre las pruebas unitarias de `app` y `shared` y la
  comprobación de nombres de pruebas para Kotlin/Native. Usa JDK 21 y crea `secrets.properties` con el secret
  `MAPS_API_KEY`; no aplica `google-services.json`.
- **detekt:** análisis estático con la configuración y el baseline del repositorio; falla ante cualquier problema
  nuevo.
- **Fuera de su cobertura:** el comportamiento real en un dispositivo (audio, subtítulos, navegación), los mapas y los
  servicios de Google. Por eso el QA manual de la sección 4 es necesario.

| Ejecución | SHA | Estado | Registro |
|---|---|---|---|
| PR Quality Gate | `3670a0ba` | `action_required`: espera autorización del mantenedor | [run 35978464518](https://github.com/gabrielhuav/PolitecnicoOpenWorld/actions/runs/35978464518) |
| PR Quality Gate | `15e90090` | `action_required`: espera autorización del mantenedor | [run 36678467477](https://github.com/gabrielhuav/PolitecnicoOpenWorld/actions/runs/36678467477) |

**Bloqueo:** como el PR viene de un fork, GitHub no ejecuta el workflow hasta que el mantenedor lo aprueba. No se
presenta como prueba aprobada. Además, en un PR desde un fork GitHub no entrega los secrets del repositorio: al
practicar el mismo flujo en una copia del proyecto, `unit-tests` falló porque `MAPS_API_KEY` llegó vacío y el
`BuildConfig` generado quedó `MAPS_API_KEY = ;`. Es probable que ocurra lo mismo aquí cuando se autorice; una posible
solución en el workflow es `MAPS_API_KEY=${{ secrets.MAPS_API_KEY || 'DEFAULT_API_KEY' }}`.

**Validación local posible:**
- **detekt:** ejecutado localmente con la CLI incluida en el repositorio, la configuración y el baseline del proyecto:
  **sin problemas nuevos**.
- **Compilación:** la versión debug de la rama compila e instala desde Android Studio (es la build usada en el QA).
- **Pruebas unitarias:** no se ejecutó el comando `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
  :shared:testAndroidHostTest` porque el repositorio no incluye `gradle-wrapper.jar`; generar ese archivo queda fuera
  de este cambio.

## 6. Revisión del PR

- **Revisión de mi PR:** [QA de Víctor](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145#issuecomment-5920967885) sobre el SHA `15e90090` en un Samsung Galaxy S25 Ultra físico (Android 16): 4 casos aprobados (policía hombre sin sirena como regresión; policía mujer, Granadero y Granadera con sirena y `SIREN`), con un video por caso y la recomendación de integrar.
- **Respuesta:** [mi respuesta](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145#issuecomment-5923375229): no se requirieron cambios; el SHA final se queda en `15e90090`.
- **Revisión que hice yo:** QA del PR de Víctor, [gabrielhuav/PolitecnicoOpenWorld#154](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154) — [mi revisión](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154#pullrequestreview-5374139314) sobre el SHA `dda3b54d`: 5 peleas IA contra IA grabadas con audio y 5 hallazgos, entre ellos un bug (la reacción deja de sonar después de la primera pelea porque `lastSuperHitReactionMs` no se reinicia).
- **Estado final del PR:** abierto y sin conflictos con `main`; el profesor lo revisó en clase el 28 de septiembre y le
  asignó la etiqueta `qa-pending`. La aprobación y el merge no son requisito del examen.

## 7. Bitácora

### Miguel Ángel Rodríguez Candelario (TheMike54)

| Fecha | Actividad |
|---|---|
| 24-sep-2026 | Fork, clon, compilación en el emulador y ejecución de la versión base; grabación del "antes" |
| 24-sep-2026 | Análisis del código: Lázaro y Paramédico no son seleccionables; los tres policías caen al `hadouken` |
| 24-sep-2026 | Commit `3670a0ba` y apertura del PR #145 |
| 28-sep-2026 | Revisión del profesor en clase (sugiere el subtítulo) |
| 30-sep-2026 | Issue #1, commit `15e90090`, casos CP-01 y CP-02, recorte de los videos de CP-03 a CP-07, descripción del PR con las secciones del examen, solicitud de QA a Víctor |
| 30-sep-2026 | QA del PR #154 de Víctor y respuesta a su revisión de mi PR |

**Commits:** `3670a0ba`, `15e90090`. **Casos ejecutados:** CP-01 a CP-07. **Revisión hecha:** PR #154.

### Víctor Moreno López (VictorMoreno-Code)

| Fecha | Actividad |
|---|---|
| 30-sep-2026 | QA del PR #145 en un Samsung Galaxy S25 Ultra: 4 casos aprobados ([revisión](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/145#issuecomment-5920967885)) |

**Su propio PR:** [gabrielhuav/PolitecnicoOpenWorld#154](https://github.com/gabrielhuav/PolitecnicoOpenWorld/pull/154).

## 8. Declaración de uso de IA

Usé **Claude Code** (Anthropic) como asistente:
- Para aprender el flujo de fork, rama, commit, push y pull request, y para entender el código del juego (cómo se
  elige el audio del especial).
- Para **generar el audio** `special_power_siren.m4a` con un script de Python/NumPy (sin samples de terceros) y
  normalizar el volumen con ffmpeg.


## 9. Conclusiones

Antes de este examen solo había hecho un pull request, y ese lo hice completamente con IA. Sabía qué era un PR y
para qué servía, pero nunca había hecho el proceso completo por mi cuenta, así que pensé que esa iba a ser la parte
difícil. Al final resultó que el proceso en sí no es complicado: hacer el fork, crear la rama, el commit, el push y
abrir el PR son pocos pasos y siempre van en el mismo orden. Lo que sí me costó fue todo lo que rodea al cambio.

Lo más pesado fue trabajar con el juego en el emulador. La interfaz no está pensada para usarse con mouse, así que
algo tan simple como lanzar el especial de un personaje me llevó muchos intentos, hasta que encontré la herramienta
Showcase del modo desarrollador. Y aun así, grabar los videos de evidencia fue la parte más complicada, porque en el
Showcase los audios se enciman y era difícil distinguir cuál sonido era el que yo había cambiado.

También me di cuenta de que un cambio pequeño no significa poco trabajo. Mi idea original era ponerle sonido a Lázaro,
pero al revisar el código vi que ni siquiera se puede elegir en el juego, y tuve que cambiar de plan. Si no hubiera
probado primero, habría entregado un cambio que nadie iba a poder escuchar.

Lo que me llevo es que el QA no es un trámite: revisando el PR de mi compañero encontramos un error que solo aparecía
en la segunda pelea, algo que no se ve leyendo el código por encima. Para la siguiente entrega empezaría por definir
las pruebas antes de programar, en lugar de armarlas al final.

## 10. Referencias

- GitHub Docs. (s. f.). *About pull requests*. https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests
- GitHub Docs. (s. f.). *Creating a pull request from a fork*. https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork
- GitHub Docs. (s. f.). *Approving workflow runs from forks*. https://docs.github.com/en/actions/how-tos/manage-workflow-runs/approve-runs-from-forks
- GitHub Docs. (s. f.). *Using secrets in GitHub Actions*. https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets
- Android Developers. (s. f.). *Android Debug Bridge (adb)*. https://developer.android.com/tools/adb
- Hurtado Avilés, G. (2026). *Politécnico Open World* [Repositorio de código]. GitHub. https://github.com/gabrielhuav/PolitecnicoOpenWorld
