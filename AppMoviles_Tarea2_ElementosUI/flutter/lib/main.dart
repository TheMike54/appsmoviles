import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'comun/rutas.dart';
import 'comun/textos.dart';

void main() {
  runApp(const CatalogoApp());
}

/// Raíz de la app: tema claro/oscuro, idioma español y las rutas de las pantallas.
class CatalogoApp extends StatelessWidget {
  const CatalogoApp({super.key});

  @override
  Widget build(BuildContext context) {
    // Mismo color base que las versiones de Android (morado de Material 3).
    const semilla = Color(0xFF6750A4);
    return MaterialApp(
      title: T.appName,
      debugShowCheckedModeBanner: false,
      theme: ThemeData(colorScheme: ColorScheme.fromSeed(seedColor: semilla)),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: semilla, brightness: Brightness.dark),
      ),
      // Sigue el modo claro u oscuro del sistema.
      themeMode: ThemeMode.system,
      // Toda la interfaz en español, incluidos el calendario y el reloj.
      locale: const Locale('es', 'MX'),
      supportedLocales: const [Locale('es', 'MX'), Locale('es')],
      localizationsDelegates: GlobalMaterialLocalizations.delegates,
      initialRoute: Ruta.inicio.nombre,
      routes: {for (final ruta in Ruta.values) ruta.nombre: (_) => ruta.pantalla()},
    );
  }
}
