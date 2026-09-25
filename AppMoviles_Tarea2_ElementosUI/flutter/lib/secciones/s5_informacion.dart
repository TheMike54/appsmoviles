import 'package:flutter/material.dart';

import '../comun/componentes.dart';
import '../comun/rutas.dart';
import '../comun/textos.dart';

const _urlImagen = 'https://picsum.photos/id/1015/800/500';

/// Sección 5: elementos que informan o dan retroalimentación al usuario.
class SeccionInformacion extends StatelessWidget {
  const SeccionInformacion({super.key});

  @override
  Widget build(BuildContext context) {
    return const PantallaSeccion(
      ruta: Ruta.informacion,
      hijos: [
        _EstilosTexto(),
        _Imagenes(),
        _Progreso(),
        _Mensajes(),
        _DialogoConfirmacion(),
        _HojaInferior(),
        _TarjetaBadge(),
      ],
    );
  }
}

// S5-01 Estilos de texto
class _EstilosTexto extends StatefulWidget {
  const _EstilosTexto();

  @override
  State<_EstilosTexto> createState() => _EstilosTextoState();
}

class _EstilosTextoState extends State<_EstilosTexto> {
  double _tamano = 22;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final primario = tema.colorScheme.primary;
    return ElementoCard(
      titulo: T.s5TextosTitulo,
      descripcion: T.s5TextosDesc,
      demo: [
        Text(T.s5TextoTitular, style: TextStyle(fontSize: _tamano)),
        Text(T.s5TextoSubtitulo, style: tema.textTheme.titleMedium?.copyWith(color: primario)),
        const SizedBox(height: 4),
        // Un mismo Text con varios énfasis usando Text.rich.
        Text.rich(
          TextSpan(style: tema.textTheme.bodyMedium, children: [
            const TextSpan(text: 'Texto normal, '),
            const TextSpan(text: 'negrita', style: TextStyle(fontWeight: FontWeight.bold)),
            const TextSpan(text: ', '),
            const TextSpan(text: 'cursiva', style: TextStyle(fontStyle: FontStyle.italic)),
            const TextSpan(text: ', '),
            const TextSpan(text: 'subrayado', style: TextStyle(decoration: TextDecoration.underline)),
            const TextSpan(text: ' y '),
            TextSpan(text: 'color', style: TextStyle(color: primario, fontWeight: FontWeight.bold)),
            const TextSpan(text: '.'),
          ]),
        ),
        const SizedBox(height: 4),
        Text(T.s5TextoEtiqueta.toUpperCase(),
            style: tema.textTheme.labelSmall?.copyWith(color: tema.colorScheme.onSurfaceVariant)),
        Subtitulo(f(T.s5TamanoValor, [_tamano.round()])),
        Slider(
          value: _tamano,
          min: 12,
          max: 40,
          // Se redondea a números pares.
          onChanged: (v) => setState(() => _tamano = (v / 2).round() * 2.0),
        ),
      ],
    );
  }
}

// S5-02 Imagen local e imagen desde URL
class _Imagenes extends StatefulWidget {
  const _Imagenes();

  @override
  State<_Imagenes> createState() => _ImagenesState();
}

class _ImagenesState extends State<_Imagenes> {
  static const _modos = [(T.s5Recortar, BoxFit.cover), (T.s5Ajustar, BoxFit.contain), (T.s5Original, BoxFit.none)];
  int _modo = 0;
  int _intento = 0;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final escala = _modos[_modo].$2;
    final chico = tema.textTheme.bodySmall;
    // ClipRect recorta la imagen cuando el modo «Original» es más grande que el marco.
    Widget marco(Widget hijo) => ClipRect(
          child: Container(
            height: 120,
            width: double.infinity,
            color: tema.colorScheme.surfaceContainerHighest,
            child: hijo,
          ),
        );
    return ElementoCard(
      titulo: T.s5ImagenTitulo,
      descripcion: T.s5ImagenDesc,
      demo: [
        Wrap(spacing: Espacios.chico, children: [
          for (final (i, m) in _modos.indexed)
            ChoiceChip(label: Text(m.$1), selected: i == _modo, onSelected: (_) => setState(() => _modo = i)),
        ]),
        const SizedBox(height: Espacios.chico),
        Row(children: [
          Expanded(
            child: Column(children: [
              marco(Image.asset('assets/imagenes/paisaje_local.png', fit: escala)),
              Text(T.s5ImagenLocal, style: chico),
            ]),
          ),
          const SizedBox(width: Espacios.chico),
          Expanded(
            child: Column(children: [
              // Image.network descarga la imagen; muestra progreso y, sin internet, un ícono de error.
              marco(Image.network(
                '$_urlImagen?intento=$_intento',
                key: ValueKey(_intento),
                fit: escala,
                loadingBuilder: (_, hijo, progreso) =>
                    progreso == null ? hijo : const Center(child: CircularProgressIndicator()),
                errorBuilder: (_, _, _) => Center(child: Icon(Icons.info, color: tema.colorScheme.outline)),
              )),
              Text(T.s5ImagenUrl, style: chico),
            ]),
          ),
        ]),
        TextButton(onPressed: () => setState(() => _intento++), child: const Text(T.s5Recargar)),
      ],
    );
  }
}

// S5-03 Indicadores de progreso
class _Progreso extends StatefulWidget {
  const _Progreso();

  @override
  State<_Progreso> createState() => _ProgresoState();
}

class _ProgresoState extends State<_Progreso> {
  int _progreso = 30;

  @override
  Widget build(BuildContext context) {
    final etiqueta = Theme.of(context).textTheme.labelLarge;
    return ElementoCard(
      titulo: T.s5ProgresoTitulo,
      descripcion: T.s5ProgresoDesc,
      demo: [
        Text(f(T.s5ProgresoValor, [_progreso]), style: etiqueta),
        const SizedBox(height: Espacios.chico),
        LinearProgressIndicator(value: _progreso / 100),
        const SizedBox(height: Espacios.interno),
        Row(children: [
          CircularProgressIndicator(value: _progreso / 100),
          const SizedBox(width: Espacios.interno),
          FilledButton.tonal(
            onPressed: _progreso < 100 ? () => setState(() => _progreso = (_progreso + 10).clamp(0, 100)) : null,
            child: const Text(T.s5Avanzar),
          ),
          TextButton(onPressed: () => setState(() => _progreso = 0), child: const Text(T.s5Reiniciar)),
        ]),
        const SizedBox(height: Espacios.interno),
        Text(T.s5Indeterminado, style: etiqueta),
        const SizedBox(height: Espacios.chico),
        const LinearProgressIndicator(),
        const SizedBox(height: Espacios.chico),
        const CircularProgressIndicator(),
      ],
    );
  }
}

// S5-04 Toast y snackbar
class _Mensajes extends StatefulWidget {
  const _Mensajes();

  @override
  State<_Mensajes> createState() => _MensajesState();
}

class _MensajesState extends State<_Mensajes> {
  String _resultado = T.tocaParaProbar;

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s5MensajesTitulo,
      descripcion: T.s5MensajesDesc,
      demo: [
        Row(children: [
          Expanded(
            child: OutlinedButton(
              onPressed: () {
                mostrarToast(context, T.s5Toast);
                setState(() => _resultado = T.s5ToastMostrado);
              },
              child: const Text(T.s5MostrarToast),
            ),
          ),
          const SizedBox(width: Espacios.chico),
          Expanded(
            child: FilledButton(
              onPressed: () => ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                content: const Text(T.s5Snackbar),
                action: SnackBarAction(
                  label: T.s4Deshacer,
                  onPressed: () => setState(() => _resultado = T.s5SnackbarDeshecho),
                ),
              )),
              child: const Text(T.s5MostrarSnackbar),
            ),
          ),
        ]),
        Resultado(_resultado),
      ],
    );
  }
}

// S5-05 Diálogo de confirmación
class _DialogoConfirmacion extends StatefulWidget {
  const _DialogoConfirmacion();

  @override
  State<_DialogoConfirmacion> createState() => _DialogoConfirmacionState();
}

class _DialogoConfirmacionState extends State<_DialogoConfirmacion> {
  String _resultado = T.tocaParaProbar;

  Future<void> _preguntar() async {
    final confirmado = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        icon: const Icon(Icons.delete),
        title: const Text(T.s5DialogoPregunta),
        content: const Text(T.s5DialogoMensaje),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text(T.s5Cancelar)),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text(T.s5Eliminar)),
        ],
      ),
    );
    setState(() => _resultado = confirmado == true ? T.s5DialogoEliminado : T.s5DialogoCancelado);
  }

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s5DialogoTitulo,
      descripcion: T.s5DialogoDesc,
      demo: [
        FilledButton.icon(onPressed: _preguntar, icon: const Icon(Icons.delete), label: const Text(T.s5EliminarArchivo)),
        Resultado(_resultado),
      ],
    );
  }
}

// S5-06 Hoja inferior
class _HojaInferior extends StatefulWidget {
  const _HojaInferior();

  @override
  State<_HojaInferior> createState() => _HojaInferiorState();
}

class _HojaInferiorState extends State<_HojaInferior> {
  String _resultado = T.tocaParaProbar;

  Future<void> _abrir() async {
    const opciones = [
      (T.s5OpcionCompartir, Icons.share),
      (T.s5OpcionCopiar, Icons.content_copy),
      (T.s5OpcionEditar, Icons.edit),
    ];
    final elegida = await showModalBottomSheet<String>(
      context: context,
      showDragHandle: true,
      builder: (context) => SafeArea(
        child: Column(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.start, children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Espacios.pantalla, vertical: Espacios.chico),
            child: Text(T.s5HojaEncabezado, style: Theme.of(context).textTheme.titleMedium),
          ),
          for (final (texto, icono) in opciones)
            ListTile(leading: Icon(icono), title: Text(texto), onTap: () => Navigator.pop(context, texto)),
        ]),
      ),
    );
    setState(() => _resultado = elegida == null ? T.s5HojaCerrada : f(T.s5HojaElegido, [elegida]));
  }

  @override
  Widget build(BuildContext context) {
    return ElementoCard(
      titulo: T.s5HojaTitulo,
      descripcion: T.s5HojaDesc,
      demo: [
        FilledButton.tonal(onPressed: _abrir, child: const Text(T.s5AbrirHoja)),
        Resultado(_resultado),
      ],
    );
  }
}

// S5-07 Tarjeta, separador y badge
class _TarjetaBadge extends StatefulWidget {
  const _TarjetaBadge();

  @override
  State<_TarjetaBadge> createState() => _TarjetaBadgeState();
}

class _TarjetaBadgeState extends State<_TarjetaBadge> {
  int _sinLeer = 3;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return ElementoCard(
      titulo: T.s5TarjetaTitulo,
      descripcion: T.s5TarjetaDesc,
      demo: [
        Card(
          margin: EdgeInsets.zero,
          child: Padding(
            padding: const EdgeInsets.all(Espacios.interno),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Row(children: [
                Padding(
                  padding: const EdgeInsets.all(6),
                  child: Badge(
                    label: Text('$_sinLeer'),
                    isLabelVisible: _sinLeer > 0,
                    child: Icon(Icons.mail, size: 32, color: tema.colorScheme.primary, semanticLabel: T.s5Bandeja),
                  ),
                ),
                const SizedBox(width: Espacios.interno),
                Text(f(T.s5BandejaEstado, [_sinLeer]), style: tema.textTheme.titleMedium),
              ]),
              const Divider(height: 24),
              Wrap(spacing: Espacios.chico, children: [
                FilledButton.tonal(onPressed: () => setState(() => _sinLeer++), child: const Text(T.s5NuevoCorreo)),
                TextButton(onPressed: () => setState(() => _sinLeer = 0), child: const Text(T.s5LeerTodo)),
              ]),
            ]),
          ),
        ),
      ],
    );
  }
}
