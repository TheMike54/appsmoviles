# Tarea 2 — Elementos básicos de interfaz de usuario

| | |
|---|---|
| **Alumno** | Miguel Ángel Rodríguez Candelario |
| **Boleta** | 2024630606 |
| **Grupo** | 7CV4 |
| **Asignatura** | Desarrollo de Aplicaciones Móviles Nativas |
| **Profesor** | Gabriel Hurtado Avilés |
| **Fecha de entrega** | 24 de septiembre de 2026 |

---

## Índice

1. [Introducción](#1-introducción)
2. [Estructura del repositorio](#2-estructura-del-repositorio)
3. [Compilación y ejecución](#3-compilación-y-ejecución)
4. [Cómo está construida cada versión](#4-cómo-está-construida-cada-versión)
5. [Tabla de equivalencias](#5-tabla-de-equivalencias)
6. [Capturas de pantalla](#6-capturas-de-pantalla)
7. [Reflexión final](#7-reflexión-final)
8. [Referencias](#8-referencias)

---

## 1. Introducción

**Catálogo de UI** es una aplicación que reúne los elementos básicos de una interfaz móvil
y los muestra funcionando. La misma aplicación se construyó tres veces, una por tecnología:

| Versión | Carpeta | Lenguaje | Cómo se arma la interfaz |
|---|---|---|---|
| Android nativo con Views y XML | [`android-views/`](android-views) | Kotlin | Layouts XML inflados en Fragments dentro de una sola Activity |
| Android nativo con Jetpack Compose | [`android-compose/`](android-compose) | Kotlin | Funciones `@Composable` dentro de una sola Activity |
| Flutter | [`flutter/`](flutter) | Dart | Árbol de widgets, una ruta por pantalla |

Las tres versiones tienen la misma estructura:

- **Pantalla principal (Inicio)** con una tarjeta por sección y un **menú lateral** para ir
  a cualquiera de las seis secciones. El botón *Atrás* siempre regresa a Inicio.
- **Seis secciones** con 40 elementos en total: Entrada de texto (7), Botones y acciones (6),
  Elementos de selección (7), Listas y colecciones (8), Información y retroalimentación (7)
  y Contenedores y estructura (5).
- **Documentación en pantalla:** cada elemento aparece en una tarjeta con su nombre, una
  explicación de dos o tres líneas y, debajo, la demostración con la que se interactúa. Cada
  demostración responde al usuario (contadores, mensajes, cambios de estado, diálogos, etc.).
- **Conexión entre secciones:** lo que se escribe en el *Campo de texto simple* de la
  Sección 1 y se envía con **Agregar a la lista** aparece al inicio de la lista vertical de la
  Sección 4, marcado como «Desde Entrada de texto · S1 → S4». El aviso que sale al agregarlo
  trae la acción **Ver lista**, que lleva directo a esa lista.
- **Tema claro y oscuro:** la aplicación sigue el modo del sistema. Las tres usan los colores
  base de Material 3 (morado `#6750A4`) para que se vean iguales.
- **Todo en español**, incluidos el calendario, el reloj y los botones de los diálogos del
  sistema. Las versiones de Android fijan el idioma de la app en español de México aunque el
  teléfono esté en otro idioma, y la de Flutter usa `flutter_localizations`.

Los textos que se ven en pantalla son idénticos en las tres versiones: Compose usa los
mismos archivos `strings.xml` que Views, y en Flutter se generaron a partir de ellos
([`flutter/lib/comun/textos.dart`](flutter/lib/comun/textos.dart)). Solo cambian los que
nombran un componente propio de cada tecnología (por ejemplo «Fila (Row)»).

---

## 2. Estructura del repositorio

```
AppMoviles_Tarea2_ElementosUI/
├── android-views/     Versión con Views y XML (proyecto de Android Studio)
├── android-compose/   Versión con Jetpack Compose (proyecto de Android Studio)
├── flutter/           Versión con Flutter
├── apk/               APK listos para instalar de las tres versiones
├── docs/              Capturas de pantalla (views/, compose/, flutter/)
└── README.md          Este documento
```

---

## 3. Compilación y ejecución

### Versiones de las herramientas

| Herramienta | Versión |
|---|---|
| Android Gradle Plugin | 9.3.2 (Kotlin integrado) |
| Gradle (wrapper incluido) | 9.5.0 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 24 |
| Compose BOM | 2026.02.01 (Material 3 1.4) |
| Flutter / Dart | 3.47.2 / 3.13.2 |
| Emulador de pruebas | Pixel 8, Android 15 (API 35) |

Requisito para las dos versiones de Android: **Android Studio** reciente con el SDK de
Android 37 instalado. Gradle usa un JDK 25 para su proceso (lo indica
`gradle/gradle-daemon-jvm.properties`); si no está instalado, Gradle lo descarga solo la
primera vez. No hace falta configurar ninguna llave ni archivo extra.

### Android — Views y XML

Desde Android Studio: **File › Open**, elegir la carpeta `android-views` y presionar ▶ Run.

Desde la terminal (con un emulador abierto o un teléfono conectado):

```bash
cd AppMoviles_Tarea2_ElementosUI/android-views
./gradlew installDebug        # en Windows: gradlew.bat installDebug
```

### Android — Jetpack Compose

Igual que la anterior, pero con la carpeta `android-compose`:

```bash
cd AppMoviles_Tarea2_ElementosUI/android-compose
./gradlew installDebug        # en Windows: gradlew.bat installDebug
```

### Flutter

Requiere el SDK de Flutter (`flutter doctor` sin errores en la sección de Android):

```bash
cd AppMoviles_Tarea2_ElementosUI/flutter
flutter pub get
flutter run                   # o: flutter build apk --release
```

> En Windows conviene clonar el repositorio en una ruta corta (por ejemplo `C:\repos\`):
> el compilador de shaders de Flutter falla si la ruta completa de un archivo de `build/`
> pasa de 260 caracteres.

### APK ya compilados

En la carpeta [`apk/`](apk) están las tres versiones en modo *release*, firmadas con la
llave de depuración para que se instalen directo:

| Archivo | Versión | Id. de la app |
|---|---|---|
| `catalogo-ui-views.apk` | Views y XML | `mx.escom.catalogoui.views` |
| `catalogo-ui-compose.apk` | Jetpack Compose | `mx.escom.catalogoui.compose` |
| `catalogo-ui-flutter.apk` | Flutter (arm64 y x86_64) | `mx.escom.catalogo_ui` |

Cada una tiene un identificador distinto, así que se pueden instalar las tres al mismo
tiempo para compararlas (`adb install apk/catalogo-ui-views.apk`, etc.).

---

## 4. Cómo está construida cada versión

| Aspecto | Views y XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Pantallas | 1 `Activity` + 1 `Fragment` por sección (`nav_graph.xml`) | 1 `Activity` + 1 composable de destino por sección (`NavHost`) | 1 ruta por pantalla (`MaterialApp.routes`) |
| Menú lateral | `DrawerLayout` + `NavigationView` enlazados con `NavigationUI` | `ModalNavigationDrawer` + `NavigationDrawerItem` | `NavigationDrawer` en `Scaffold.drawer` |
| Barra superior | `MaterialToolbar` como `ActionBar` | `TopAppBar` dentro de `Scaffold` | `AppBar` dentro de `Scaffold` |
| Tarjeta de cada elemento | Vista propia `ElementoCard` (hereda de `MaterialCardView`) | Composable `ElementoCard` (`OutlinedCard`) | Widget `ElementoCard` (`Card.outlined`) |
| Estado compartido S1 → S4 | `ViewModel` + `LiveData` con `activityViewModels()` | `ViewModel` con `mutableStateListOf` | `ChangeNotifier` + `ListenableBuilder` |
| Tema claro/oscuro | `Theme.Material3.DayNight` | `MaterialTheme` con `lightColorScheme` / `darkColorScheme` según `isSystemInDarkTheme()` | `theme` + `darkTheme` con `ThemeMode.system` |
| Idioma español | `AppCompatDelegate.setApplicationLocales("es-MX")` | Igual que Views | `locale: es_MX` + `GlobalMaterialLocalizations` |
| Imagen desde URL | Coil 3 (`ImageView.load`) | Coil 3 (`AsyncImage`) | `Image.network` (incluido en Flutter) |

---

## 5. Tabla de equivalencias

Componente que se usó para cada elemento del catálogo en las tres tecnologías. Cuando no
existe un equivalente directo se indica con **(sin equivalente directo)** y debajo de cada
sección se explica cómo se resolvió.

### Sección 1 — Entrada de texto

| Elemento | Views / XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Campo de texto simple | `TextInputLayout` + `TextInputEditText` | `OutlinedTextField` (`label`) | `TextField` (`InputDecoration.labelText`) |
| Campo con validación y error | `TextInputLayout.error` (se valida en `doAfterTextChanged`) | `OutlinedTextField` (`isError` + `supportingText`) | `TextFormField` (`validator` + `autovalidateMode`) |
| Contraseña con mostrar/ocultar | `TextInputLayout` con `endIconMode="password_toggle"` | `OutlinedTextField` + `PasswordVisualTransformation` + `IconButton` | `TextField(obscureText)` + `IconButton` en `suffixIcon` |
| Teclado numérico, de correo y de teléfono | `android:inputType` = `number` / `textEmailAddress` / `phone` | `KeyboardOptions(keyboardType = Number / Email / Phone)` | `keyboardType: TextInputType.number / emailAddress / phone` |
| Campo multilínea | `inputType="textMultiLine"` + `minLines` + `counterEnabled` | `OutlinedTextField(minLines = 3)` + contador en `supportingText` | `TextField(minLines: 3, maxLines: null, maxLength: 200)` |
| Sugerencias automáticas | `MaterialAutoCompleteTextView` + `ArrayAdapter` | `ExposedDropdownMenuBox` editable con la lista filtrada **(sin equivalente directo)** | `Autocomplete<String>` |
| Barra de búsqueda | `SearchView` (AppCompat) | `DockedSearchBar` + `SearchBarDefaults.InputField` | `SearchBar` |

- **Sugerencias en Compose:** Material 3 para Compose no trae un campo de autocompletado. Se
  armó con un `ExposedDropdownMenuBox` cuyo campo es editable y cuyo menú muestra solo las
  opciones que contienen lo escrito.

### Sección 2 — Botones y acciones

| Elemento | Views / XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Botón relleno, con contorno y de texto | `MaterialButton` con estilos `Button` / `OutlinedButton` / `TextButton` | `Button` / `OutlinedButton` / `TextButton` | `FilledButton` / `OutlinedButton` / `TextButton` |
| Botón solo con ícono | `MaterialButton` con estilo `materialIconButtonStyle` y `checkable` | `IconToggleButton` | `IconButton(isSelected, selectedIcon)` |
| Botón con ícono y texto | `MaterialButton` con `app:icon` | `FilledTonalButton` con `Icon` + `Text` | `FilledButton.tonalIcon` |
| Botón de acción flotante | `FloatingActionButton` | `FloatingActionButton` | `FloatingActionButton` |
| Botón flotante extendido | `ExtendedFloatingActionButton` (`shrink()` / `extend()`) | `ExtendedFloatingActionButton(expanded)` | `FloatingActionButton.extended(isExtended)` |
| Selector segmentado | `MaterialButtonToggleGroup` (`singleSelection`) | `SingleChoiceSegmentedButtonRow` + `SegmentedButton` | `SegmentedButton<int>` |
| Botón deshabilitado | `isEnabled = false` | `enabled = false` | `onPressed: null` |
| Botón en estado de carga | `MaterialButton` + `CircularProgressIndicator` en un `FrameLayout` **(sin equivalente directo)** | `Button(enabled = false)` con `CircularProgressIndicator` como contenido **(sin equivalente directo)** | `FilledButton(onPressed: null)` con `CircularProgressIndicator` como hijo **(sin equivalente directo)** |

- **Botón en carga:** ninguna de las tres tecnologías lo trae como componente. En las tres se
  resolvió igual: al tocarlo se deshabilita y su texto se cambia por un indicador circular
  durante 2 segundos.

### Sección 3 — Elementos de selección

| Elemento | Views / XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Casilla de verificación con estado indeterminado | `MaterialCheckBox` (`checkedState = STATE_INDETERMINATE`) | `TriStateCheckbox` + `Checkbox` | `CheckboxListTile(tristate: true)` |
| Botones de opción excluyentes | `RadioGroup` + `MaterialRadioButton` | `RadioButton` + `Modifier.selectable` dentro de `selectableGroup` | `RadioGroup` + `RadioListTile` |
| Interruptor | `MaterialSwitch` | `Switch` | `Switch` |
| Deslizador de valor único | `Slider` (Material) | `Slider` | `Slider` |
| Deslizador de rango | `RangeSlider` (Material) | `RangeSlider` | `RangeSlider` |
| Lista desplegable | `Spinner` + `ArrayAdapter` | `ExposedDropdownMenuBox` de solo lectura | `DropdownMenu<String>` |
| Selector de fecha | `MaterialDatePicker` | `DatePickerDialog` + `DatePicker` | `showDatePicker` |
| Selector de hora | `MaterialTimePicker` | `TimePicker` dentro de un `AlertDialog` **(sin equivalente directo)** | `showTimePicker` |
| Chips de filtro | `ChipGroup` + `Chip` (estilo `Filter`) | `FilterChip` dentro de `FlowRow` | `FilterChip` dentro de `Wrap` |

- **Selector de hora en Compose:** `TimePicker` es solo el reloj, no abre un diálogo propio.
  Se colocó dentro de un `AlertDialog` con los botones Cancelar y Aceptar.

### Sección 4 — Listas y colecciones

| Elemento | Views / XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Pestañas con contenido deslizable | `TabLayout` + `ViewPager2` (`TabLayoutMediator`) | `PrimaryTabRow` + `HorizontalPager` | `TabBar` + `TabBarView` |
| Lista vertical (20 elementos o más) | `RecyclerView` + `LinearLayoutManager` + `ListAdapter` | `LazyColumn` | `ListView.separated` |
| Detalle al seleccionar | Clic en el `ViewHolder` → `MaterialAlertDialogBuilder` | `Modifier.clickable` → `AlertDialog` | `ListTile.onTap` → `showDialog` |
| Deslizar para eliminar | `ItemTouchHelper.SimpleCallback` | `SwipeToDismissBox` | `Dismissible` |
| Arrastrar para actualizar | `SwipeRefreshLayout` | `PullToRefreshBox` | `RefreshIndicator` |
| Estado vacío | Vista alterna (`ImageView` + textos + botón) con `visibility` **(sin equivalente directo)** | Composable alterno con `if` **(sin equivalente directo)** | Widget alterno con `if` **(sin equivalente directo)** |
| Cuadrícula | `RecyclerView` + `GridLayoutManager` | `LazyVerticalGrid` | `GridView.builder` |
| Lista con encabezados (2 tipos de fila) | `RecyclerView` con dos `viewType` | `LazyColumn` + `stickyHeader` | `CustomScrollView` + `SliverMainAxisGroup` + `PinnedHeaderSliver` |

- **Estado vacío:** no es un componente en ninguna tecnología. Se muestra otra vista (ícono de
  bandeja, mensaje y botón *Restaurar lista*) cuando la lista no tiene elementos.
- **Encabezados:** en Compose y Flutter el encabezado del grupo se queda fijo arriba al
  desplazar. En Views eso requiere una librería externa, así que ahí los encabezados se
  desplazan junto con la lista.
- **Deslizar para eliminar dentro de pestañas:** en las tres, el mismo gesto lateral sirve para
  cambiar de pestaña y para borrar una fila. En la pestaña *Lista* se le dio prioridad a borrar:
  ahí se cambia de pestaña tocando su título. Cada tecnología lo resolvió a su modo: en Views
  la lista le pide al `ViewPager2` que no intercepte el gesto; en Compose se desactiva
  `userScrollEnabled` del pager en esa página; en Flutter se usa
  `NeverScrollableScrollPhysics` en el `TabBarView` mientras esa pestaña está activa.

### Sección 5 — Información y retroalimentación

| Elemento | Views / XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Textos con distintos estilos y énfasis | `TextView` + `textAppearance` de Material 3 + `SpannableStringBuilder` | `Text` + `MaterialTheme.typography` + `buildAnnotatedString` | `Text` + `TextTheme` + `Text.rich` |
| Imagen local | `ImageView` con recurso en `drawable-nodpi` | `Image(painterResource)` | `Image.asset` |
| Imagen desde URL | `ImageView` + Coil (`load`) | `AsyncImage` (Coil) | `Image.network` |
| Modos de escalado | `scaleType` = `centerCrop` / `fitCenter` / `center` | `ContentScale.Crop` / `Fit` / `None` | `BoxFit.cover` / `contain` / `none` |
| Progreso lineal y circular (determinado e indeterminado) | `LinearProgressIndicator` y `CircularProgressIndicator` (Material) | `LinearProgressIndicator` y `CircularProgressIndicator` | `LinearProgressIndicator` y `CircularProgressIndicator` |
| Mensaje emergente breve (toast) | `Toast` | `Toast` de Android **(sin equivalente directo)** | Aviso propio con `OverlayEntry` **(sin equivalente directo)** |
| Mensaje con acción (snackbar) | `Snackbar.setAction` | `SnackbarHostState.showSnackbar(actionLabel)` | `ScaffoldMessenger.showSnackBar` + `SnackBarAction` |
| Diálogo de confirmación | `MaterialAlertDialogBuilder` | `AlertDialog` | `showDialog` + `AlertDialog` |
| Hoja inferior | `BottomSheetDialog` | `ModalBottomSheet` | `showModalBottomSheet` |
| Tarjeta | `MaterialCardView` | `ElevatedCard` | `Card` |
| Separador | `MaterialDivider` | `HorizontalDivider` | `Divider` |
| Distintivo numérico (badge) | `BadgeDrawable` + `BadgeUtils` | `BadgedBox` + `Badge` | `Badge` |

- **Toast en Compose:** Compose no tiene toast propio; se llama al `Toast` de Android desde
  el composable.
- **Toast en Flutter:** Flutter no trae toast. Para no depender de un paquete externo se dibuja
  un aviso flotante con un `OverlayEntry` que desaparece solo a los 2 segundos.

### Sección 6 — Contenedores y estructura

| Elemento | Views / XML | Jetpack Compose | Flutter |
|---|---|---|---|
| Distribución en fila | `LinearLayout` horizontal | `Row` | `Row` |
| Distribución en columna | `LinearLayout` vertical | `Column` | `Column` |
| Distribución superpuesta | `FrameLayout` (`bringToFront`) | `Box` (`zIndex`) | `Stack` + `Positioned` |
| Contenedor con desplazamiento vertical | `NestedScrollView` | `Column` + `Modifier.verticalScroll` | `SingleChildScrollView` + `Scrollbar` |
| Barra superior con título y acciones | `MaterialToolbar` + menú XML | `TopAppBar(actions)` + `DropdownMenu` | `AppBar(actions)` + `PopupMenuButton` |
| Barra de navegación inferior | `BottomNavigationView` | `NavigationBar` + `NavigationBarItem` | `NavigationBar` + `NavigationDestination` |
| Menú lateral (navegación de la app) | `DrawerLayout` + `NavigationView` | `ModalNavigationDrawer` | `NavigationDrawer` |
| Distribución con pesos proporcionales | `LinearLayout` con `layout_weight` | `Modifier.weight` | `Expanded(flex)` |
| Distribución con restricciones | `ConstraintLayout` (`layout_constraintHorizontal_bias`) | `Box` + `BiasAlignment` **(sin equivalente directo)** | `Align(alignment: Alignment(x, 0))` **(sin equivalente directo)** |

- **Restricciones:** en Compose, `ConstraintLayout` es una librería aparte; para no agregar
  otra dependencia, la caja se ubica con una alineación con sesgo (`BiasAlignment`) y la
  etiqueta va en la misma fila, de modo que la sigue al moverse. Flutter no tiene
  `ConstraintLayout`; se hizo lo mismo con `Align`. El deslizador mueve la caja de izquierda
  a derecha en las tres versiones.

---

## 6. Capturas de pantalla

Tomadas en el emulador Pixel 8 (Android 15) con el IDE abierto. En cada fila, de izquierda a
derecha: Views, Compose y Flutter.

### Pantalla principal y menú lateral

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Inicio en Views](docs/views/00-inicio.png) | ![Inicio en Compose](docs/compose/00-inicio.png) | ![Inicio en Flutter](docs/flutter/00-inicio.png) |
| ![Menú en Views](docs/views/00-menu.png) | ![Menú en Compose](docs/compose/00-menu.png) | ![Menú en Flutter](docs/flutter/00-menu.png) |

### Sección 1 — Entrada de texto

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Sección 1 en Views](docs/views/01-entrada-texto.png) | ![Sección 1 en Compose](docs/compose/01-entrada-texto.png) | ![Sección 1 en Flutter](docs/flutter/01-entrada-texto.png) |

### Sección 2 — Botones y acciones

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Sección 2 en Views](docs/views/02-botones.png) | ![Sección 2 en Compose](docs/compose/02-botones.png) | ![Sección 2 en Flutter](docs/flutter/02-botones.png) |

### Sección 3 — Elementos de selección

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Sección 3 en Views](docs/views/03-seleccion.png) | ![Sección 3 en Compose](docs/compose/03-seleccion.png) | ![Sección 3 en Flutter](docs/flutter/03-seleccion.png) |

### Sección 4 — Listas y colecciones

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Sección 4 en Views](docs/views/04-listas.png) | ![Sección 4 en Compose](docs/compose/04-listas.png) | ![Sección 4 en Flutter](docs/flutter/04-listas.png) |

### Sección 5 — Información y retroalimentación

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Sección 5 en Views](docs/views/05-informacion.png) | ![Sección 5 en Compose](docs/compose/05-informacion.png) | ![Sección 5 en Flutter](docs/flutter/05-informacion.png) |

### Sección 6 — Contenedores y estructura

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Sección 6 en Views](docs/views/06-contenedores.png) | ![Sección 6 en Compose](docs/compose/06-contenedores.png) | ![Sección 6 en Flutter](docs/flutter/06-contenedores.png) |

### Conexión entre secciones (Sección 1 → Sección 4)

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Conexión en Views](docs/views/07-conexion.png) | ![Conexión en Compose](docs/compose/07-conexion.png) | ![Conexión en Flutter](docs/flutter/07-conexion.png) |

### Tema oscuro

| Views y XML | Jetpack Compose | Flutter |
|---|---|---|
| ![Tema oscuro en Views](docs/views/08-oscuro.png) | ![Tema oscuro en Compose](docs/compose/08-oscuro.png) | ![Tema oscuro en Flutter](docs/flutter/08-oscuro.png) |

---

## 7. Reflexión final

**¿En cuál fue más rápido construir la interfaz?** En Jetpack Compose. Como ya tenía hecha la
versión de Views, en Compose fue casi traducir: cada tarjeta es una función, el estado vive
junto al componente que lo usa y no tuve que ir y venir entre un XML y una clase de Kotlin.
De hecho, las seis secciones de Compose compilaron a la primera. Flutter también fue rápido,
aunque ahí sí me topé con un error que no avisó al compilar: le puse a un `Container` un
`color` y una `decoration` al mismo tiempo, `flutter analyze` no marcó nada y la sección
salió con la pantalla roja de error hasta que lo corregí.

**¿Cuál generó código más legible?** Compose y Flutter se leen muy parecido: el código tiene
la misma forma que la pantalla, así que uno lee la función de arriba abajo y ve lo mismo que
en el teléfono. En Views cada elemento está repartido en tres lugares (el XML del layout, el
`strings.xml` y el código del Fragment que le conecta los eventos), y para seguir una sola
demostración hay que abrir los tres. Para mí la más clara fue Compose, porque además de lo
visual me dejó tener los textos en `strings.xml` como en Views.

**Dificultades en cada una.**

- *Views y XML:* fue la que más detalles escondidos tuvo. El calendario salía en inglés porque
  el emulador está en inglés; lo arreglé fijando el idioma de la app, pero la primera vez no
  funcionó porque lo llamaba antes de `super.onCreate()` y simplemente no hacía nada. También
  descubrí que con el menú lateral abierto el botón *Atrás* cerraba la app en vez del menú, y
  que al meter la lista dentro de pestañas el gesto de deslizar para borrar se lo quedaba el
  `ViewPager2`.
- *Jetpack Compose:* lo más engañoso fue el snackbar de «Deshacer». Al principio lo lanzaba
  desde la misma fila que se estaba borrando, y como esa fila desaparece de la pantalla, su
  corrutina se cancela con ella y la acción no alcanzaría a ejecutarse. Lo cambié para
  lanzarlo desde la lista, que sí sigue existiendo. Aquí también apareció el conflicto entre
  deslizar para borrar y deslizar entre pestañas.
- *Flutter:* además del error del `Container`, lo que más me costó fue entender que Flutter
  no tiene toast y que para el encabezado fijo había que combinar varios *slivers*. Aparte, en
  Windows falló una compilación porque la ruta de la carpeta era demasiado larga.

**¿Con cuál preferiría trabajar?** Con Jetpack Compose para una app que solo va a correr en
Android: se escribe menos, se entiende mejor y sigue usando las herramientas de Android que
ya conozco. Si el proyecto tuviera que salir también en iOS, elegiría Flutter, porque la
experiencia fue muy parecida a Compose y con un solo código se cubren las dos plataformas.
Views lo sigo viendo útil para entender cómo funcionan por dentro los Fragments y el ciclo de
vida, pero no lo escogería para empezar algo nuevo.

---

## 8. Referencias

Coil Contributors. (s. f.). *Coil: Image loading for Android and Compose Multiplatform*.
Recuperado el 22 de septiembre de 2026, de https://coil-kt.github.io/coil/

Google. (s. f.). *Material Design 3: Components*. Recuperado el 22 de septiembre de 2026, de
https://m3.material.io/components

Google. (s. f.). *Jetpack Compose documentation*. Android Developers. Recuperado el 22 de
septiembre de 2026, de https://developer.android.com/develop/ui/compose/documentation

Google. (s. f.). *Layouts in views*. Android Developers. Recuperado el 22 de septiembre de
2026, de https://developer.android.com/develop/ui/views/layout/declaring-layout

Google. (s. f.). *Navigation*. Android Developers. Recuperado el 22 de septiembre de 2026, de
https://developer.android.com/guide/navigation

Google. (s. f.). *Per-app language preferences*. Android Developers. Recuperado el 22 de
septiembre de 2026, de https://developer.android.com/guide/topics/resources/app-languages

Google. (s. f.). *Flutter documentation: Widget catalog*. Flutter. Recuperado el 22 de
septiembre de 2026, de https://docs.flutter.dev/ui/widgets

Google. (s. f.). *Internationalizing Flutter apps*. Flutter. Recuperado el 22 de septiembre de
2026, de https://docs.flutter.dev/ui/accessibility-and-internationalization/internationalization

Google. (s. f.). *material library*. Flutter API. Recuperado el 22 de septiembre de 2026, de
https://api.flutter.dev/flutter/material/

JetBrains. (s. f.). *Kotlin documentation*. Recuperado el 22 de septiembre de 2026, de
https://kotlinlang.org/docs/home.html
