import 'package:flutter/material.dart';

import '../comun/componentes.dart';
import '../comun/rutas.dart';
import '../comun/textos.dart';

/// Sección 6: contenedores y estructura de pantalla.
class SeccionContenedores extends StatelessWidget {
  const SeccionContenedores({super.key});

  @override
  Widget build(BuildContext context) {
    return const PantallaSeccion(
      ruta: Ruta.contenedores,
      hijos: [
        _Distribuciones(),
        _ContenedorDesplazable(),
        _BarraSuperiorDemo(),
        _BarraInferiorDemo(),
        _PesosYAlineacion(),
      ],
    );
  }
}

/// Marco punteado que muestra el área de un contenedor de ejemplo.
BoxDecoration _zona(ThemeData tema) => BoxDecoration(
      color: tema.colorScheme.surfaceContainerLow,
      border: Border.all(color: tema.colorScheme.outlineVariant),
      borderRadius: BorderRadius.circular(8),
    );

Widget _caja(String texto, Color fondo, Color colorTexto, {double lado = 36}) => Container(
      width: lado,
      height: lado,
      alignment: Alignment.center,
      decoration: BoxDecoration(color: fondo, borderRadius: BorderRadius.circular(8)),
      child: Text(texto, style: TextStyle(color: colorTexto, fontWeight: FontWeight.bold)),
    );

// S6-01 Fila, columna y superpuesta
class _Distribuciones extends StatefulWidget {
  const _Distribuciones();

  @override
  State<_Distribuciones> createState() => _DistribucionesState();
}

class _DistribucionesState extends State<_Distribuciones> {
  int _cajas = 3;
  // Orden de dibujo de las capas: en un Stack, el último hijo queda al frente.
  final _capas = ['A', 'B', 'C'];

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final c = tema.colorScheme;
    final colores = {'A': (c.primary, c.onPrimary), 'B': (c.tertiary, c.onTertiary), 'C': (c.secondary, c.onSecondary)};
    const posiciones = {'A': (16.0, 12.0), 'B': (56.0, 32.0), 'C': (96.0, 52.0)};
    return ElementoCard(
      titulo: T.s6DistribucionTitulo,
      descripcion: T.s6DistribucionDesc,
      demo: [
        const Subtitulo(T.s6Fila),
        Container(
          height: 48,
          padding: const EdgeInsets.all(4),
          decoration: _zona(tema),
          child: Row(children: [
            for (var i = 0; i < _cajas; i++)
              Padding(padding: const EdgeInsets.only(right: 8), child: _caja('${i + 1}', c.primary, c.onPrimary)),
          ]),
        ),
        const Subtitulo(T.s6Columna),
        Container(
          width: 120,
          padding: const EdgeInsets.all(4),
          decoration: _zona(tema),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            for (var i = 0; i < _cajas; i++)
              Padding(padding: const EdgeInsets.only(bottom: 8), child: _caja('${i + 1}', c.primary, c.onPrimary)),
          ]),
        ),
        const SizedBox(height: Espacios.chico),
        Wrap(spacing: Espacios.chico, children: [
          FilledButton.tonal(
            onPressed: () => setState(() => _cajas = (_cajas + 1).clamp(1, 6)),
            child: const Text(T.s6AgregarCaja),
          ),
          TextButton(
            onPressed: () => setState(() => _cajas = (_cajas - 1).clamp(1, 6)),
            child: const Text(T.s6QuitarCaja),
          ),
        ]),
        const Subtitulo(T.s6Superpuesta),
        Container(
          height: 130,
          width: double.infinity,
          decoration: _zona(tema),
          child: Stack(children: [
            for (final nombre in _capas)
              Positioned(
                left: posiciones[nombre]!.$1,
                top: posiciones[nombre]!.$2,
                child: _caja(nombre, colores[nombre]!.$1, colores[nombre]!.$2, lado: 56),
              ),
          ]),
        ),
        const SizedBox(height: Espacios.chico),
        FilledButton.tonal(
          onPressed: () => setState(() => _capas.add(_capas.removeAt(0))),
          child: const Text(T.s6RotarCapas),
        ),
        Resultado(f(T.s6DistribucionEstado, [_cajas, _capas.last])),
      ],
    );
  }
}

// S6-02 Contenedor con desplazamiento vertical
class _ContenedorDesplazable extends StatefulWidget {
  const _ContenedorDesplazable();

  @override
  State<_ContenedorDesplazable> createState() => _ContenedorDesplazableState();
}

class _ContenedorDesplazableState extends State<_ContenedorDesplazable> {
  static const _alturaRenglon = 28.0;
  final _controlador = ScrollController();

  @override
  void initState() {
    super.initState();
    _controlador.addListener(() => setState(() {}));
  }

  @override
  void dispose() {
    _controlador.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final desplazado = _controlador.hasClients ? _controlador.offset.clamp(0, double.infinity).round() : 0;
    return ElementoCard(
      titulo: T.s6ScrollTitulo,
      descripcion: T.s6ScrollDesc,
      demo: [
        Container(
          height: 160,
          decoration: _zona(Theme.of(context)),
          child: Scrollbar(
            controller: _controlador,
            thumbVisibility: true,
            child: SingleChildScrollView(
              controller: _controlador,
              padding: const EdgeInsets.all(Espacios.chico),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                for (var i = 1; i <= 30; i++)
                  SizedBox(
                    height: _alturaRenglon,
                    child: Align(alignment: Alignment.centerLeft, child: Text(f(T.s6Renglon, [i]))),
                  ),
              ]),
            ),
          ),
        ),
        Resultado(f(T.s6ScrollEstado, [desplazado, desplazado ~/ _alturaRenglon + 1])),
      ],
    );
  }
}

// S6-03 Barra superior con título y acciones
class _BarraSuperiorDemo extends StatefulWidget {
  const _BarraSuperiorDemo();

  @override
  State<_BarraSuperiorDemo> createState() => _BarraSuperiorDemoState();
}

class _BarraSuperiorDemoState extends State<_BarraSuperiorDemo> {
  bool _favorito = false;
  String? _accion;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return ElementoCard(
      titulo: T.s6ToolbarTitulo,
      descripcion: T.s6ToolbarDesc,
      demo: [
        AppBar(
          // primary: false → dentro de una tarjeta no reserva espacio para la barra de estado.
          primary: false,
          automaticallyImplyLeading: false,
          backgroundColor: tema.colorScheme.surfaceContainer,
          leading: IconButton(
            tooltip: T.s6AccionInicio,
            icon: const Icon(Icons.home),
            onPressed: () => setState(() => _accion = T.s6AccionInicio),
          ),
          title: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text(T.s6ToolbarNombre),
            Text(T.s6ToolbarSubtitulo, style: tema.textTheme.bodySmall),
          ]),
          actions: [
            IconButton(
              tooltip: T.s6AccionBuscar,
              icon: const Icon(Icons.search),
              onPressed: () => setState(() => _accion = T.s6AccionBuscar),
            ),
            IconButton(
              tooltip: T.s6AccionFavorito,
              icon: Icon(_favorito ? Icons.favorite : Icons.favorite_border),
              onPressed: () => setState(() {
                _favorito = !_favorito;
                _accion = T.s6AccionFavorito;
              }),
            ),
            PopupMenuButton<String>(
              onSelected: (opcion) => setState(() => _accion = opcion),
              itemBuilder: (_) => const [
                PopupMenuItem(value: T.s6AccionAjustes, child: Text(T.s6AccionAjustes)),
                PopupMenuItem(value: T.s6AccionAyuda, child: Text(T.s6AccionAyuda)),
              ],
            ),
          ],
        ),
        Resultado(_accion == null ? T.s6ToolbarInstruccion : f(T.s6ToolbarAccion, [_accion!])),
      ],
    );
  }
}

// S6-04 Barra de navegación inferior
class _BarraInferiorDemo extends StatefulWidget {
  const _BarraInferiorDemo();

  @override
  State<_BarraInferiorDemo> createState() => _BarraInferiorDemoState();
}

class _BarraInferiorDemoState extends State<_BarraInferiorDemo> {
  static const _destinos = [
    (T.s6DestinoInicio, Icons.home),
    (T.s6DestinoBuscar, Icons.search),
    (T.s6DestinoPerfil, Icons.person),
  ];
  int _elegido = 0;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return ElementoCard(
      titulo: T.s6BottomnavTitulo,
      descripcion: T.s6BottomnavDesc,
      demo: [
        Container(
          height: 100,
          width: double.infinity,
          decoration: _zona(tema),
          child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
            Icon(_destinos[_elegido].$2, color: tema.colorScheme.primary),
            Text(f(T.s6BottomnavContenido, [_destinos[_elegido].$1]), style: tema.textTheme.titleMedium),
          ]),
        ),
        MediaQuery.removePadding(
          context: context,
          removeBottom: true,
          child: NavigationBar(
            selectedIndex: _elegido,
            onDestinationSelected: (i) => setState(() => _elegido = i),
            destinations: [
              for (final (i, (texto, icono)) in _destinos.indexed)
                NavigationDestination(
                  // Ejemplo de badge en la barra inferior.
                  icon: i == 2 ? Badge(label: const Text('2'), child: Icon(icono)) : Icon(icono),
                  label: texto,
                ),
            ],
          ),
        ),
      ],
    );
  }
}

// S6-05 Pesos proporcionales y alineación relativa
class _PesosYAlineacion extends StatefulWidget {
  const _PesosYAlineacion();

  @override
  State<_PesosYAlineacion> createState() => _PesosYAlineacionState();
}

class _PesosYAlineacionState extends State<_PesosYAlineacion> {
  int _peso = 1;
  double _sesgo = 0.5;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final c = tema.colorScheme;
    return ElementoCard(
      titulo: T.s6PesosTitulo,
      descripcion: T.s6PesosDesc,
      demo: [
        Subtitulo(f(T.s6PesosEstado, [_peso])),
        SizedBox(
          height: 48,
          child: Row(children: [
            Expanded(
              child: Container(
                color: c.primaryContainer,
                alignment: Alignment.center,
                child: Text('A · 1', style: TextStyle(color: c.onPrimaryContainer)),
              ),
            ),
            const SizedBox(width: 4),
            Expanded(
              flex: _peso,
              child: Container(
                color: c.tertiaryContainer,
                alignment: Alignment.center,
                child: Text('B · $_peso', style: TextStyle(color: c.onTertiaryContainer)),
              ),
            ),
          ]),
        ),
        Slider(
          value: _peso.toDouble(),
          min: 1,
          max: 4,
          divisions: 3,
          onChanged: (v) => setState(() => _peso = v.round()),
        ),
        Subtitulo(f(T.s6RestriccionesEstado, [_sesgo])),
        Container(
          height: 120,
          width: double.infinity,
          decoration: _zona(tema),
          // Alignment(x, y): -1 = izquierda, 1 = derecha. El slider va de 0 a 1 como en Views.
          child: Align(
            alignment: Alignment(_sesgo * 2 - 1, 0),
            child: Row(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.end, children: [
              Container(
                width: 64,
                height: 64,
                color: c.primary,
                alignment: Alignment.center,
                child: Text(T.s6Caja, style: TextStyle(color: c.onPrimary)),
              ),
              const SizedBox(width: Espacios.chico),
              Text(T.s6EtiquetaAtada, style: tema.textTheme.bodySmall),
            ]),
          ),
        ),
        Slider(
          value: _sesgo,
          onChanged: (v) => setState(() => _sesgo = (v * 10).round() / 10),
        ),
      ],
    );
  }
}
