import 'package:flutter/material.dart';

import 'rutas.dart';
import 'textos.dart';

/// Espaciado común para que todas las pantallas se vean igual.
class Espacios {
  static const pantalla = 16.0;
  static const tarjetas = 12.0;
  static const interno = 16.0;
  static const chico = 8.0;
}

/// Estructura de cada pantalla: barra superior con título y menú lateral con las secciones.
class EstructuraApp extends StatelessWidget {
  const EstructuraApp({super.key, required this.ruta, required this.cuerpo});

  final Ruta ruta;
  final Widget cuerpo;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(ruta.titulo),
            Text(T.drawerSubtitulo, style: tema.textTheme.titleSmall),
          ],
        ),
      ),
      drawer: NavigationDrawer(
        selectedIndex: ruta.index,
        onDestinationSelected: (i) {
          Navigator.pop(context); // cierra el menú
          if (Ruta.values[i] != ruta) irA(context, Ruta.values[i]);
        },
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(28, 16, 16, 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(T.appName, style: tema.textTheme.headlineSmall),
                Text(T.drawerSubtitulo,
                    style: tema.textTheme.bodyMedium?.copyWith(color: tema.colorScheme.onSurfaceVariant)),
              ],
            ),
          ),
          for (final r in Ruta.values)
            NavigationDrawerDestination(icon: Icon(r.icono), label: Text(r.titulo)),
        ],
      ),
      body: cuerpo,
    );
  }
}

/// Pantalla de sección: lista desplazable con las tarjetas separadas de forma uniforme.
class PantallaSeccion extends StatelessWidget {
  const PantallaSeccion({super.key, required this.ruta, required this.hijos});

  final Ruta ruta;
  final List<Widget> hijos;

  @override
  Widget build(BuildContext context) {
    return EstructuraApp(
      ruta: ruta,
      cuerpo: ListView.separated(
        // Se suma el alto de la barra de navegación del sistema para que nada quede debajo.
        padding: const EdgeInsets.all(Espacios.pantalla) +
            EdgeInsets.only(bottom: MediaQuery.paddingOf(context).bottom),
        itemCount: hijos.length,
        separatorBuilder: (_, _) => const SizedBox(height: Espacios.tarjetas),
        itemBuilder: (_, i) => hijos[i],
      ),
    );
  }
}

/// Tarjeta que documenta un elemento del catálogo: nombre, explicación corta y, debajo,
/// la demostración interactiva.
class ElementoCard extends StatelessWidget {
  const ElementoCard({super.key, required this.titulo, required this.descripcion, this.demo = const []});

  final String titulo;
  final String descripcion;
  final List<Widget> demo;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Card.outlined(
      margin: EdgeInsets.zero,
      child: Padding(
        padding: const EdgeInsets.all(Espacios.interno),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(titulo,
                style: tema.textTheme.titleMedium?.copyWith(color: tema.colorScheme.primary, fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Text(descripcion,
                style: tema.textTheme.bodyMedium?.copyWith(color: tema.colorScheme.onSurfaceVariant)),
            if (demo.isNotEmpty) const SizedBox(height: 12),
            ...demo,
          ],
        ),
      ),
    );
  }
}

/// Texto donde cada demostración muestra la respuesta a lo que hizo el usuario.
class Resultado extends StatelessWidget {
  const Resultado(this.texto, {super.key});

  final String texto;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.only(top: Espacios.chico),
        child: Text(texto, style: Theme.of(context).textTheme.bodyMedium),
      );
}

/// Subtítulo pequeño dentro de una demostración.
class Subtitulo extends StatelessWidget {
  const Subtitulo(this.texto, {super.key});

  final String texto;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.only(top: Espacios.chico, bottom: 4),
        child: Text(texto, style: Theme.of(context).textTheme.labelLarge),
      );
}

/// Convierte un texto con <b>negritas</b> y saltos <br> en un Text.rich.
class TextoConNegritas extends StatelessWidget {
  const TextoConNegritas(this.html, {super.key, this.estilo});

  final String html;
  final TextStyle? estilo;

  @override
  Widget build(BuildContext context) {
    final partes = <TextSpan>[];
    final texto = html.replaceAll('<br>', '\n');
    final patron = RegExp(r'<b>(.*?)</b>');
    var inicio = 0;
    for (final m in patron.allMatches(texto)) {
      partes.add(TextSpan(text: texto.substring(inicio, m.start)));
      partes.add(TextSpan(text: m.group(1), style: const TextStyle(fontWeight: FontWeight.bold)));
      inicio = m.end;
    }
    partes.add(TextSpan(text: texto.substring(inicio)));
    return Text.rich(TextSpan(children: partes), style: estilo);
  }
}

/// Mensaje emergente breve (toast). Flutter no trae toast propio, así que se dibuja
/// un aviso flotante encima de todo con un OverlayEntry y se quita solo a los 2 segundos.
void mostrarToast(BuildContext context, String mensaje) {
  final overlay = Overlay.of(context);
  final tema = Theme.of(context);
  final entrada = OverlayEntry(
    builder: (_) => Positioned(
      left: 24,
      right: 24,
      bottom: 96,
      child: IgnorePointer(
        child: Center(
          child: Material(
            color: tema.colorScheme.inverseSurface,
            borderRadius: BorderRadius.circular(24),
            elevation: 4,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              child: Text(mensaje, style: TextStyle(color: tema.colorScheme.onInverseSurface)),
            ),
          ),
        ),
      ),
    ),
  );
  overlay.insert(entrada);
  Future.delayed(const Duration(seconds: 2), entrada.remove);
}
