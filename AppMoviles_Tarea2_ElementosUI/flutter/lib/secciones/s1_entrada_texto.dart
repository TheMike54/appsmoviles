import 'package:flutter/material.dart';

import '../comun/componentes.dart';
import '../comun/estado.dart';
import '../comun/rutas.dart';
import '../comun/textos.dart';

/// Sección 1: elementos para capturar texto.
class SeccionEntradaTexto extends StatelessWidget {
  const SeccionEntradaTexto({super.key});

  @override
  Widget build(BuildContext context) {
    return const PantallaSeccion(
      ruta: Ruta.entrada,
      hijos: [
        _CampoSimple(),
        _CampoValidacion(),
        _CampoPassword(),
        _CamposTeclado(),
        _CampoMultilinea(),
        _CampoSugerencias(),
        _BarraBusqueda(),
      ],
    );
  }
}

// S1-01 Campo de texto simple (conectado con la Sección 4)
class _CampoSimple extends StatefulWidget {
  const _CampoSimple();

  @override
  State<_CampoSimple> createState() => _CampoSimpleState();
}

class _CampoSimpleState extends State<_CampoSimple> {
  final _controlador = TextEditingController();
  bool _error = false;

  @override
  void dispose() {
    _controlador.dispose();
    super.dispose();
  }

  void _agregar() {
    final nombre = _controlador.text.trim();
    if (nombre.isEmpty) {
      setState(() => _error = true);
      return;
    }
    catalogo.agregarDesdeEntrada(nombre);
    _controlador.clear();
    setState(() => _error = false);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(f(T.s1SimpleAgregado, [nombre])),
        action: SnackBarAction(label: T.s1SimpleVerLista, onPressed: () => irA(context, Ruta.listas)),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s1SimpleTitulo,
      descripcion: T.s1SimpleDesc,
      demo: [
        TextField(
          controller: _controlador,
          textCapitalization: TextCapitalization.sentences,
          textInputAction: TextInputAction.done,
          onChanged: (_) => setState(() => _error = false),
          decoration: InputDecoration(
            labelText: T.s1SimpleHint,
            border: const OutlineInputBorder(),
            errorText: _error ? T.s1SimpleFalta : null,
          ),
        ),
        ListenableBuilder(
          listenable: _controlador,
          builder: (_, _) => Resultado(
            _controlador.text.trim().isEmpty ? T.s1SimpleVacio : f(T.s1SimpleEco, [_controlador.text]),
          ),
        ),
        const SizedBox(height: Espacios.chico),
        FilledButton.icon(onPressed: _agregar, icon: const Icon(Icons.add), label: const Text(T.s1SimpleAgregar)),
      ],
    );
  }
}

// S1-02 Campo con validación
class _CampoValidacion extends StatelessWidget {
  const _CampoValidacion();

  @override
  Widget build(BuildContext context) {
    final soloLetras = RegExp(r'^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]{3,}$');
    return ElementoCard(
      titulo: T.s1ValidacionTitulo,
      descripcion: T.s1ValidacionDesc,
      demo: [
        TextFormField(
          // Valida cada vez que el usuario escribe y muestra el error debajo del campo.
          autovalidateMode: AutovalidateMode.onUserInteraction,
          validator: (valor) {
            if (valor == null || valor.isEmpty) return null;
            return soloLetras.hasMatch(valor) ? null : T.s1ValidacionError;
          },
          decoration: const InputDecoration(labelText: T.s1ValidacionHint, border: OutlineInputBorder()),
        ),
      ],
    );
  }
}

// S1-03 Campo de contraseña
class _CampoPassword extends StatefulWidget {
  const _CampoPassword();

  @override
  State<_CampoPassword> createState() => _CampoPasswordState();
}

class _CampoPasswordState extends State<_CampoPassword> {
  bool _visible = false;
  String _clave = '';

  @override
  Widget build(BuildContext context) {
    final fuerza = _clave.length >= 10
        ? T.s1PasswordFuerte
        : _clave.length >= 6
            ? T.s1PasswordMedia
            : T.s1PasswordDebil;
    return ElementoCard(
      titulo: T.s1PasswordTitulo,
      descripcion: T.s1PasswordDesc,
      demo: [
        TextField(
          obscureText: !_visible,
          onChanged: (v) => setState(() => _clave = v),
          decoration: InputDecoration(
            labelText: T.s1PasswordHint,
            border: const OutlineInputBorder(),
            helperText: _clave.isEmpty ? null : f(T.s1PasswordFuerza, [_clave.length, fuerza]),
            suffixIcon: IconButton(
              tooltip: T.s1PasswordHint,
              icon: Icon(_visible ? Icons.visibility_off : Icons.visibility),
              onPressed: () => setState(() => _visible = !_visible),
            ),
          ),
        ),
      ],
    );
  }
}

// S1-04 Tipos de teclado
class _CamposTeclado extends StatefulWidget {
  const _CamposTeclado();

  @override
  State<_CamposTeclado> createState() => _CamposTecladoState();
}

class _CamposTecladoState extends State<_CamposTeclado> {
  String _edad = '', _correo = '', _telefono = '';

  Widget _campo(String etiqueta, TextInputType teclado, ValueChanged<String> alCambiar, {int? maximo}) => Padding(
        padding: const EdgeInsets.only(bottom: Espacios.chico),
        child: TextField(
          keyboardType: teclado,
          maxLength: maximo,
          onChanged: (v) => setState(() => alCambiar(v)),
          decoration: InputDecoration(labelText: etiqueta, border: const OutlineInputBorder(), counterText: ''),
        ),
      );

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s1TecladosTitulo,
      descripcion: T.s1TecladosDesc,
      demo: [
        _campo(T.s1TecladoNumero, TextInputType.number, (v) => _edad = v, maximo: 3),
        _campo(T.s1TecladoCorreo, TextInputType.emailAddress, (v) => _correo = v),
        _campo(T.s1TecladoTelefono, TextInputType.phone, (v) => _telefono = v),
        Resultado(f(T.s1TecladosResumen, [
          _edad.isEmpty ? '—' : _edad,
          _correo.isEmpty ? '—' : _correo,
          _telefono.isEmpty ? '—' : _telefono,
        ])),
      ],
    );
  }
}

// S1-05 Campo multilínea
class _CampoMultilinea extends StatefulWidget {
  const _CampoMultilinea();

  @override
  State<_CampoMultilinea> createState() => _CampoMultilineaState();
}

class _CampoMultilineaState extends State<_CampoMultilinea> {
  String _texto = '';

  @override
  Widget build(BuildContext context) {
    final lineas = _texto.isEmpty ? 0 : '\n'.allMatches(_texto).length + 1;
    return ElementoCard(
      titulo: T.s1MultilineaTitulo,
      descripcion: T.s1MultilineaDesc,
      demo: [
        TextField(
          minLines: 3,
          maxLines: null,
          maxLength: 200,
          keyboardType: TextInputType.multiline,
          textCapitalization: TextCapitalization.sentences,
          onChanged: (v) => setState(() => _texto = v),
          decoration: const InputDecoration(
            labelText: T.s1MultilineaHint,
            border: OutlineInputBorder(),
            alignLabelWithHint: true,
          ),
        ),
        Resultado(f(T.s1MultilineaLineas, [lineas])),
      ],
    );
  }
}

// S1-06 Sugerencias automáticas
class _CampoSugerencias extends StatefulWidget {
  const _CampoSugerencias();

  @override
  State<_CampoSugerencias> createState() => _CampoSugerenciasState();
}

class _CampoSugerenciasState extends State<_CampoSugerencias> {
  String? _elegido;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s1SugerenciasTitulo,
      descripcion: T.s1SugerenciasDesc,
      demo: [
        Autocomplete<String>(
          optionsBuilder: (valor) => valor.text.isEmpty
              ? const Iterable<String>.empty()
              : T.estadosMexico.where((e) => e.toLowerCase().contains(valor.text.toLowerCase())),
          onSelected: (estado) => setState(() => _elegido = estado),
          fieldViewBuilder: (context, controlador, foco, alEnviar) => TextField(
            controller: controlador,
            focusNode: foco,
            textCapitalization: TextCapitalization.words,
            onSubmitted: (_) => alEnviar(),
            decoration: const InputDecoration(labelText: T.s1SugerenciasHint, border: OutlineInputBorder()),
          ),
        ),
        if (_elegido != null) Resultado(f(T.s1SugerenciasElegido, [_elegido!])),
      ],
    );
  }
}

// S1-07 Barra de búsqueda
class _BarraBusqueda extends StatefulWidget {
  const _BarraBusqueda();

  @override
  State<_BarraBusqueda> createState() => _BarraBusquedaState();
}

class _BarraBusquedaState extends State<_BarraBusqueda> {
  final _controlador = TextEditingController();

  @override
  void dispose() {
    _controlador.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final consulta = _controlador.text.trim();
    final resultados = T.lenguajes.where((l) => l.toLowerCase().contains(consulta.toLowerCase())).toList();
    return ElementoCard(
      titulo: T.s1BusquedaTitulo,
      descripcion: T.s1BusquedaDesc,
      demo: [
        SearchBar(
          controller: _controlador,
          hintText: T.s1BusquedaHint,
          leading: const Icon(Icons.search),
          elevation: const WidgetStatePropertyAll(0),
          onChanged: (_) => setState(() {}),
          trailing: [
            if (_controlador.text.isNotEmpty)
              IconButton(
                icon: const Icon(Icons.close),
                onPressed: () => setState(_controlador.clear),
              ),
          ],
        ),
        Resultado(resultados.isEmpty
            ? f(T.s1BusquedaSinResultados, [consulta])
            : f(T.s1BusquedaResultados, [resultados.length, resultados.join(', ')])),
      ],
    );
  }
}
