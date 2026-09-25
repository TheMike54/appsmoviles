import 'dart:async';

import 'package:flutter/material.dart';

import '../comun/componentes.dart';
import '../comun/rutas.dart';
import '../comun/textos.dart';

/// Sección 2: botones y acciones. Cada botón produce una respuesta visible.
class SeccionBotones extends StatelessWidget {
  const SeccionBotones({super.key});

  @override
  Widget build(BuildContext context) {
    return const PantallaSeccion(
      ruta: Ruta.botones,
      hijos: [
        _BotonesBasicos(),
        _BotonesIcono(),
        _BotonesFlotantes(),
        _SelectorSegmentado(),
        _BotonDeshabilitado(),
        _BotonCarga(),
      ],
    );
  }
}

// S2-01 Relleno, contorno y texto
class _BotonesBasicos extends StatefulWidget {
  const _BotonesBasicos();

  @override
  State<_BotonesBasicos> createState() => _BotonesBasicosState();
}

class _BotonesBasicosState extends State<_BotonesBasicos> {
  int _relleno = 0, _contorno = 0, _texto = 0;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s2BasicosTitulo,
      descripcion: T.s2BasicosDesc,
      demo: [
        Wrap(spacing: Espacios.chico, children: [
          FilledButton(onPressed: () => setState(() => _relleno++), child: const Text(T.s2Relleno)),
          OutlinedButton(onPressed: () => setState(() => _contorno++), child: const Text(T.s2Contorno)),
          TextButton(onPressed: () => setState(() => _texto++), child: const Text(T.s2Texto)),
        ]),
        Resultado(f(T.s2BasicosContador, [_relleno, _contorno, _texto])),
      ],
    );
  }
}

// S2-02 Botones con ícono
class _BotonesIcono extends StatefulWidget {
  const _BotonesIcono();

  @override
  State<_BotonesIcono> createState() => _BotonesIconoState();
}

class _BotonesIconoState extends State<_BotonesIcono> {
  bool _favorito = false;
  int _compartido = 0;
  String _mensaje = T.tocaParaProbar;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s2IconoTitulo,
      descripcion: T.s2IconoDesc,
      demo: [
        Row(children: [
          IconButton(
            tooltip: T.s2Favorito,
            isSelected: _favorito,
            color: Theme.of(context).colorScheme.primary,
            icon: const Icon(Icons.favorite_border),
            selectedIcon: const Icon(Icons.favorite),
            onPressed: () => setState(() {
              _favorito = !_favorito;
              _mensaje = _favorito ? T.s2FavoritoSi : T.s2FavoritoNo;
            }),
          ),
          const SizedBox(width: Espacios.interno),
          FilledButton.tonalIcon(
            onPressed: () => setState(() => _mensaje = f(T.s2Compartido, [++_compartido])),
            icon: const Icon(Icons.share),
            label: const Text(T.s2Compartir),
          ),
        ]),
        Resultado(_mensaje),
      ],
    );
  }
}

// S2-03 Botones de acción flotantes
class _BotonesFlotantes extends StatefulWidget {
  const _BotonesFlotantes();

  @override
  State<_BotonesFlotantes> createState() => _BotonesFlotantesState();
}

class _BotonesFlotantesState extends State<_BotonesFlotantes> {
  int _toques = 0;
  bool _extendido = true;
  String _mensaje = T.tocaParaProbar;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s2FabTitulo,
      descripcion: T.s2FabDesc,
      demo: [
        Row(children: [
          FloatingActionButton(
            heroTag: 'fab_normal',
            tooltip: T.s2FabNormal,
            onPressed: () => setState(() => _mensaje = f(T.s2FabContador, [++_toques])),
            child: const Icon(Icons.add),
          ),
          const SizedBox(width: Espacios.interno),
          FloatingActionButton.extended(
            heroTag: 'fab_extendido',
            isExtended: _extendido,
            icon: const Icon(Icons.edit),
            label: const Text(T.s2FabExtendido),
            onPressed: () => setState(() {
              _extendido = !_extendido;
              _mensaje = _extendido ? T.s2FabExpandido : T.s2FabEncogido;
            }),
          ),
        ]),
        Resultado(_mensaje),
      ],
    );
  }
}

// S2-04 Selector segmentado
class _SelectorSegmentado extends StatefulWidget {
  const _SelectorSegmentado();

  @override
  State<_SelectorSegmentado> createState() => _SelectorSegmentadoState();
}

class _SelectorSegmentadoState extends State<_SelectorSegmentado> {
  static const _opciones = [T.s2Dia, T.s2Semana, T.s2Mes];
  int _elegido = 0;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s2ToggleTitulo,
      descripcion: T.s2ToggleDesc,
      demo: [
        SizedBox(
          width: double.infinity,
          child: SegmentedButton<int>(
            segments: [for (final (i, o) in _opciones.indexed) ButtonSegment(value: i, label: Text(o))],
            selected: {_elegido},
            onSelectionChanged: (s) => setState(() => _elegido = s.first),
          ),
        ),
        Resultado(f(T.s2ToggleElegido, [_opciones[_elegido]])),
      ],
    );
  }
}

// S2-05 Botón deshabilitado
class _BotonDeshabilitado extends StatefulWidget {
  const _BotonDeshabilitado();

  @override
  State<_BotonDeshabilitado> createState() => _BotonDeshabilitadoState();
}

class _BotonDeshabilitadoState extends State<_BotonDeshabilitado> {
  bool _aceptado = false, _continuo = false;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s2DeshabilitadoTitulo,
      descripcion: T.s2DeshabilitadoDesc,
      demo: [
        SwitchListTile(
          contentPadding: EdgeInsets.zero,
          title: const Text(T.s2AceptarTerminos),
          value: _aceptado,
          onChanged: (v) => setState(() {
            _aceptado = v;
            _continuo = false;
          }),
        ),
        // onPressed en null = botón deshabilitado.
        FilledButton(
          onPressed: _aceptado ? () => setState(() => _continuo = true) : null,
          child: const Text(T.s2Continuar),
        ),
        Resultado(_continuo
            ? T.s2Continuaste
            : _aceptado
                ? T.s2DeshabilitadoEstadoOn
                : T.s2DeshabilitadoEstadoOff),
      ],
    );
  }
}

// S2-06 Botón en estado de carga
class _BotonCarga extends StatefulWidget {
  const _BotonCarga();

  @override
  State<_BotonCarga> createState() => _BotonCargaState();
}

class _BotonCargaState extends State<_BotonCarga> {
  bool _cargando = false, _listo = false;
  Timer? _temporizador;

  @override
  void dispose() {
    _temporizador?.cancel();
    super.dispose();
  }

  void _descargar() {
    setState(() {
      _cargando = true;
      _listo = false;
    });
    _temporizador = Timer(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _cargando = false;
          _listo = true;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s2CargaTitulo,
      descripcion: T.s2CargaDesc,
      demo: [
        ConstrainedBox(
          constraints: const BoxConstraints(minWidth: 160),
          child: FilledButton(
            onPressed: _cargando ? null : _descargar,
            child: _cargando
                ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                : const Text(T.s2Descargar),
          ),
        ),
        Resultado(_cargando
            ? T.s2Cargando
            : _listo
                ? T.s2Listo
                : T.tocaParaProbar),
      ],
    );
  }
}
