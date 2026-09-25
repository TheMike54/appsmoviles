import 'package:flutter/material.dart';

import 'comun/componentes.dart';
import 'comun/rutas.dart';
import 'comun/textos.dart';

/// Pantalla principal: explica la app y muestra una tarjeta por sección.
class PantallaInicio extends StatelessWidget {
  const PantallaInicio({super.key});

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final secciones = Ruta.values.skip(1).toList();
    return PantallaSeccion(
      ruta: Ruta.inicio,
      hijos: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(T.inicioTitulo, style: tema.textTheme.headlineSmall),
            const SizedBox(height: Espacios.chico),
            Text(T.inicioDescripcion,
                style: tema.textTheme.bodyMedium?.copyWith(color: tema.colorScheme.onSurfaceVariant)),
          ],
        ),
        for (final (i, ruta) in secciones.indexed)
          Card(
            margin: EdgeInsets.zero,
            clipBehavior: Clip.antiAlias,
            child: InkWell(
              onTap: () => irA(context, ruta),
              child: Padding(
                padding: const EdgeInsets.all(Espacios.interno),
                child: Row(
                  children: [
                    Icon(ruta.icono, size: 40, color: tema.colorScheme.primary),
                    const SizedBox(width: Espacios.interno),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(f(T.inicioFormatoSeccion, [i + 1, ruta.titulo]), style: tema.textTheme.titleMedium),
                          Text(ruta.descripcion,
                              style: tema.textTheme.bodySmall?.copyWith(color: tema.colorScheme.onSurfaceVariant)),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
      ],
    );
  }
}
