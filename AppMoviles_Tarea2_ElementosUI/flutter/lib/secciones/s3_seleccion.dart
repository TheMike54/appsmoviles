import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../comun/componentes.dart';
import '../comun/rutas.dart';
import '../comun/textos.dart';

/// Sección 3: elementos para elegir entre opciones.
class SeccionSeleccion extends StatelessWidget {
  const SeccionSeleccion({super.key});

  @override
  Widget build(BuildContext context) {
    return const PantallaSeccion(
      ruta: Ruta.seleccion,
      hijos: [
        _Casillas(),
        _BotonesOpcion(),
        _Interruptor(),
        _Deslizadores(),
        _ListaDesplegable(),
        _SelectoresFechaHora(),
        _ChipsFiltro(),
      ],
    );
  }
}

// S3-01 Casillas con estado indeterminado
class _Casillas extends StatefulWidget {
  const _Casillas();

  @override
  State<_Casillas> createState() => _CasillasState();
}

class _CasillasState extends State<_Casillas> {
  static const _nombres = [T.s3Queso, T.s3Champinones, T.s3Pimiento];
  final _marcadas = [true, false, false];

  /// true = todas, false = ninguna, null = indeterminado (solo algunas).
  bool? get _estadoPadre {
    final n = _marcadas.where((m) => m).length;
    if (n == 0) return false;
    if (n == _marcadas.length) return true;
    return null;
  }

  @override
  Widget build(BuildContext context) {
    final estado = switch (_estadoPadre) {
      true => T.s3EstadoMarcado,
      false => T.s3EstadoDesmarcado,
      null => T.s3EstadoIndeterminado,
    };
    return ElementoCard(
      titulo: T.s3CheckboxTitulo,
      descripcion: T.s3CheckboxDesc,
      demo: [
        CheckboxListTile(
          contentPadding: EdgeInsets.zero,
          controlAffinity: ListTileControlAffinity.leading,
          tristate: true,
          title: const Text(T.s3Todos),
          value: _estadoPadre,
          // Tocar «Todos» marca o desmarca a todas las hijas.
          onChanged: (_) => setState(() {
            final marcar = _estadoPadre != true;
            for (var i = 0; i < _marcadas.length; i++) {
              _marcadas[i] = marcar;
            }
          }),
        ),
        for (var i = 0; i < _nombres.length; i++)
          CheckboxListTile(
            contentPadding: const EdgeInsets.only(left: 32),
            controlAffinity: ListTileControlAffinity.leading,
            title: Text(_nombres[i]),
            value: _marcadas[i],
            onChanged: (v) => setState(() => _marcadas[i] = v ?? false),
          ),
        Resultado(f(T.s3CheckboxEstado, [_marcadas.where((m) => m).length, estado])),
      ],
    );
  }
}

// S3-02 Botones de opción
class _BotonesOpcion extends StatefulWidget {
  const _BotonesOpcion();

  @override
  State<_BotonesOpcion> createState() => _BotonesOpcionState();
}

class _BotonesOpcionState extends State<_BotonesOpcion> {
  static const _opciones = [T.s3Chica, T.s3Mediana, T.s3Grande];
  int? _elegida;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s3RadioTitulo,
      descripcion: T.s3RadioDesc,
      demo: [
        // RadioGroup mantiene la opción elegida: al marcar una se desmarca la anterior.
        RadioGroup<int>(
          groupValue: _elegida,
          onChanged: (v) => setState(() => _elegida = v),
          child: Column(children: [
            for (final (i, o) in _opciones.indexed)
              RadioListTile<int>(contentPadding: EdgeInsets.zero, value: i, title: Text(o)),
          ]),
        ),
        Resultado(_elegida == null ? T.s3RadioNinguno : f(T.s3RadioElegido, [_opciones[_elegida!]])),
      ],
    );
  }
}

// S3-03 Interruptor
class _Interruptor extends StatefulWidget {
  const _Interruptor();

  @override
  State<_Interruptor> createState() => _InterruptorState();
}

class _InterruptorState extends State<_Interruptor> {
  bool _activo = false;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s3SwitchTitulo,
      descripcion: T.s3SwitchDesc,
      demo: [
        Row(children: [
          const Expanded(child: Text(T.s3Notificaciones)),
          Switch(value: _activo, onChanged: (v) => setState(() => _activo = v)),
        ]),
        Resultado(_activo ? T.s3SwitchOn : T.s3SwitchOff),
      ],
    );
  }
}

// S3-04 Deslizadores
class _Deslizadores extends StatefulWidget {
  const _Deslizadores();

  @override
  State<_Deslizadores> createState() => _DeslizadoresState();
}

class _DeslizadoresState extends State<_Deslizadores> {
  double _volumen = 40;
  RangeValues _rango = const RangeValues(200, 700);

  @override
  Widget build(BuildContext context) {
    final etiqueta = Theme.of(context).textTheme.labelLarge;
    return ElementoCard(
      titulo: T.s3SliderTitulo,
      descripcion: T.s3SliderDesc,
      demo: [
        Text(f(T.s3VolumenValor, [_volumen.round()]), style: etiqueta),
        Slider(value: _volumen, max: 100, onChanged: (v) => setState(() => _volumen = v.roundToDouble())),
        Text(f(T.s3RangoValor, [_rango.start.round(), _rango.end.round()]), style: etiqueta),
        RangeSlider(
          values: _rango,
          max: 1000,
          // Se redondea de 50 en 50.
          onChanged: (r) => setState(() => _rango = RangeValues(
                (r.start / 50).round() * 50.0,
                (r.end / 50).round() * 50.0,
              )),
        ),
      ],
    );
  }
}

// S3-05 Lista desplegable
class _ListaDesplegable extends StatefulWidget {
  const _ListaDesplegable();

  @override
  State<_ListaDesplegable> createState() => _ListaDesplegableState();
}

class _ListaDesplegableState extends State<_ListaDesplegable> {
  String _pais = T.paises.first;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s3SpinnerTitulo,
      descripcion: T.s3SpinnerDesc,
      demo: [
        DropdownMenu<String>(
          initialSelection: _pais,
          expandedInsets: EdgeInsets.zero,
          requestFocusOnTap: false,
          dropdownMenuEntries: [for (final p in T.paises) DropdownMenuEntry(value: p, label: p)],
          onSelected: (p) => setState(() => _pais = p ?? _pais),
        ),
        Resultado(f(T.s3SpinnerElegido, [_pais])),
      ],
    );
  }
}

// S3-06 Selectores de fecha y hora
class _SelectoresFechaHora extends StatefulWidget {
  const _SelectoresFechaHora();

  @override
  State<_SelectoresFechaHora> createState() => _SelectoresFechaHoraState();
}

class _SelectoresFechaHoraState extends State<_SelectoresFechaHora> {
  String? _fecha, _hora;

  Future<void> _elegirFecha() async {
    final hoy = DateTime.now();
    final elegida = await showDatePicker(
      context: context,
      initialDate: hoy,
      firstDate: DateTime(hoy.year - 50),
      lastDate: DateTime(hoy.year + 50),
      helpText: T.s3ElegirFecha,
    );
    if (elegida != null) {
      setState(() => _fecha = DateFormat("EEEE d 'de' MMMM 'de' yyyy", 'es').format(elegida));
    }
  }

  Future<void> _elegirHora() async {
    final elegida = await showTimePicker(
      context: context,
      initialTime: const TimeOfDay(hour: 12, minute: 0),
      helpText: T.s3ElegirHora,
      builder: (context, hijo) =>
          MediaQuery(data: MediaQuery.of(context).copyWith(alwaysUse24HourFormat: true), child: hijo!),
    );
    if (elegida != null) {
      setState(() => _hora =
          '${elegida.hour.toString().padLeft(2, '0')}:${elegida.minute.toString().padLeft(2, '0')}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s3FechaTitulo,
      descripcion: T.s3FechaDesc,
      demo: [
        Row(children: [
          Expanded(child: FilledButton.tonal(onPressed: _elegirFecha, child: const Text(T.s3ElegirFecha))),
          const SizedBox(width: Espacios.chico),
          Expanded(child: FilledButton.tonal(onPressed: _elegirHora, child: const Text(T.s3ElegirHora))),
        ]),
        Resultado(f(T.s3FechaHora, [_fecha ?? T.s3SinElegir, _hora ?? T.s3SinElegir])),
      ],
    );
  }
}

// S3-07 Chips de filtro
class _ChipsFiltro extends StatefulWidget {
  const _ChipsFiltro();

  @override
  State<_ChipsFiltro> createState() => _ChipsFiltroState();
}

class _ChipsFiltroState extends State<_ChipsFiltro> {
  static const _categorias = [T.categoriaFruta, T.categoriaVerdura, T.categoriaGrano];
  static const _comidas = [
    ('Manzana', 0), ('Plátano', 0), ('Mango', 0),
    ('Zanahoria', 1), ('Brócoli', 1), ('Espinaca', 1),
    ('Arroz', 2), ('Maíz', 2), ('Avena', 2),
  ];
  final Set<int> _activos = {};

  @override
  Widget build(BuildContext context) {
    final visibles = _activos.isEmpty ? _comidas : _comidas.where((c) => _activos.contains(c.$2)).toList();
    return ElementoCard(
      titulo: T.s3ChipsTitulo,
      descripcion: T.s3ChipsDesc,
      demo: [
        Wrap(spacing: Espacios.chico, children: [
          for (final (i, c) in _categorias.indexed)
            FilterChip(
              label: Text(c),
              selected: _activos.contains(i),
              onSelected: (s) => setState(() => s ? _activos.add(i) : _activos.remove(i)),
            ),
        ]),
        Resultado(f(T.s3ChipsResultado, [visibles.length, visibles.map((c) => c.$1).join(', ')])),
      ],
    );
  }
}
