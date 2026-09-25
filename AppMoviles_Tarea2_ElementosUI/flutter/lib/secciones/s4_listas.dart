import 'package:flutter/material.dart';

import '../comun/componentes.dart';
import '../comun/estado.dart';
import '../comun/rutas.dart';
import '../comun/textos.dart';

/// Sección 4: listas y colecciones. Un TabBar enlazado a un TabBarView reparte los
/// elementos en tres pestañas.
class SeccionListas extends StatefulWidget {
  const SeccionListas({super.key});

  @override
  State<SeccionListas> createState() => _SeccionListasState();
}

class _SeccionListasState extends State<SeccionListas> with SingleTickerProviderStateMixin {
  late final TabController _pestanas = TabController(length: 3, vsync: this)..addListener(() => setState(() {}));

  @override
  void dispose() {
    _pestanas.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return EstructuraApp(
      ruta: Ruta.listas,
      cuerpo: Column(children: [
        // S4-01 Pestañas con contenido deslizable
        const Padding(
          padding: EdgeInsets.fromLTRB(Espacios.pantalla, Espacios.chico, Espacios.pantalla, 0),
          child: ElementoCard(titulo: T.s4PestanasTitulo, descripcion: T.s4PestanasDesc),
        ),
        TabBar(controller: _pestanas, tabs: const [
          Tab(text: T.s4TabLista, icon: Icon(Icons.list)),
          Tab(text: T.s4TabCuadricula, icon: Icon(Icons.dashboard)),
          Tab(text: T.s4TabSecciones, icon: Icon(Icons.info)),
        ]),
        Expanded(
          child: TabBarView(
            controller: _pestanas,
            // En la pestaña Lista el gesto lateral es para borrar filas, así que ahí las
            // pestañas no se deslizan con el dedo; se cambia tocando el título.
            physics: _pestanas.index == 0 ? const NeverScrollableScrollPhysics() : null,
            children: const [_PaginaLista(), _PaginaCuadricula(), _PaginaSecciones()],
          ),
        ),
      ]),
    );
  }
}

/// Tarjeta de documentación que se pliega para dejar espacio a la colección.
class _DocPlegable extends StatefulWidget {
  const _DocPlegable(this.cuerpo, {this.abiertaAlInicio = false});

  final String cuerpo;
  final bool abiertaAlInicio;

  @override
  State<_DocPlegable> createState() => _DocPlegableState();
}

class _DocPlegableState extends State<_DocPlegable> {
  late bool _abierta = widget.abiertaAlInicio;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Card(
      margin: const EdgeInsets.all(Espacios.chico),
      color: tema.colorScheme.surfaceContainerHighest,
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => setState(() => _abierta = !_abierta),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(children: [
              Icon(Icons.info, color: tema.colorScheme.primary),
              const SizedBox(width: Espacios.chico),
              Expanded(
                child: Text(T.s4DocEncabezado,
                    style: tema.textTheme.titleSmall?.copyWith(color: tema.colorScheme.primary)),
              ),
            ]),
            if (_abierta)
              Padding(
                padding: const EdgeInsets.only(top: Espacios.chico),
                child: TextoConNegritas(widget.cuerpo, estilo: tema.textTheme.bodySmall),
              ),
          ]),
        ),
      ),
    );
  }
}

/// Fila de lista con el círculo inicial, nombre y categoría.
class _FilaElemento extends StatelessWidget {
  const _FilaElemento(this.item, {required this.alTocar});

  final ItemCatalogo item;
  final VoidCallback alTocar;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return ListTile(
      onTap: alTocar,
      leading: CircleAvatar(
        backgroundColor: tema.colorScheme.primaryContainer,
        foregroundColor: tema.colorScheme.onPrimaryContainer,
        child: Text(item.desdeEntrada ? item.nombre[0].toUpperCase() : item.nombre.split(' ').last),
      ),
      title: Text(item.nombre),
      subtitle: Text(item.desdeEntrada ? f(T.s4DesdeEntrada, ['S1 → S4']) : item.categoria),
    );
  }
}

// Pestaña «Lista»: S4-02 a S4-06
class _PaginaLista extends StatefulWidget {
  const _PaginaLista();

  @override
  State<_PaginaLista> createState() => _PaginaListaState();
}

class _PaginaListaState extends State<_PaginaLista> {
  final _desplazamiento = ScrollController();

  @override
  void dispose() {
    _desplazamiento.dispose();
    super.dispose();
  }

  // S4-05 Arrastrar para actualizar: espera 1.5 s y agrega un elemento nuevo.
  Future<void> _actualizar() async {
    await Future.delayed(const Duration(milliseconds: 1500));
    final nuevo = catalogo.agregarNuevo();
    if (!mounted) return;
    _desplazamiento.animateTo(0, duration: const Duration(milliseconds: 300), curve: Curves.easeOut);
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(f(T.s4Actualizado, [nuevo.nombre]))));
  }

  // S4-03 Detalle al seleccionar
  void _mostrarDetalle(ItemCatalogo item) {
    showDialog<void>(
      context: context,
      builder: (context) => AlertDialog(
        icon: const Icon(Icons.info),
        title: Text(item.nombre),
        content: Text(f(T.s4DetalleMensaje,
            [item.categoria, item.id, item.desdeEntrada ? T.s4DetalleEntrada : T.s4DetalleNormal])),
        actions: [TextButton(onPressed: () => Navigator.pop(context), child: const Text(T.s4Cerrar))],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    // ListenableBuilder redibuja la lista cada vez que cambia el estado compartido.
    return ListenableBuilder(
      listenable: catalogo,
      builder: (context, _) {
        final items = catalogo.items;
        return Column(children: [
          const _DocPlegable(T.s4DocLista),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Espacios.chico),
            child: Row(children: [
              Expanded(
                child: Text(items.isEmpty ? T.s4TotalVacio : f(T.s4Total, [items.length]),
                    style: tema.textTheme.labelLarge),
              ),
              TextButton.icon(
                onPressed: items.isEmpty ? null : catalogo.vaciar,
                icon: const Icon(Icons.delete),
                label: const Text(T.s4Vaciar),
              ),
            ]),
          ),
          Expanded(
            child: items.isEmpty
                // S4-06 Estado vacío
                ? const _EstadoVacio()
                : RefreshIndicator(
                    onRefresh: _actualizar,
                    // S4-02 Lista vertical
                    child: ListView.separated(
                      controller: _desplazamiento,
                      physics: const AlwaysScrollableScrollPhysics(),
                      itemCount: items.length,
                      separatorBuilder: (_, _) => const Divider(height: 1),
                      itemBuilder: (context, i) {
                        final item = items[i];
                        // S4-04 Deslizar para eliminar
                        return Dismissible(
                          key: ValueKey(item.id),
                          background: Container(
                            color: tema.colorScheme.errorContainer,
                            alignment: Alignment.centerRight,
                            padding: const EdgeInsets.symmetric(horizontal: Espacios.pantalla),
                            child: Icon(Icons.delete, color: tema.colorScheme.onErrorContainer),
                          ),
                          onDismissed: (_) {
                            final posicion = catalogo.eliminar(item);
                            ScaffoldMessenger.of(context)
                              ..hideCurrentSnackBar()
                              ..showSnackBar(SnackBar(
                                content: Text(f(T.s4Eliminado, [item.nombre])),
                                action: SnackBarAction(
                                  label: T.s4Deshacer,
                                  onPressed: () => catalogo.agregar(item, posicion),
                                ),
                              ));
                          },
                          child: _FilaElemento(item, alTocar: () => _mostrarDetalle(item)),
                        );
                      },
                    ),
                  ),
          ),
        ]);
      },
    );
  }
}

class _EstadoVacio extends StatelessWidget {
  const _EstadoVacio();

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Center(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(Espacios.pantalla),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Icon(Icons.inbox, size: 120, color: tema.colorScheme.outline),
          const SizedBox(height: Espacios.interno),
          Text(T.s4VacioTitulo, style: tema.textTheme.titleMedium),
          const SizedBox(height: 4),
          Text(T.s4VacioMensaje,
              textAlign: TextAlign.center,
              style: tema.textTheme.bodyMedium?.copyWith(color: tema.colorScheme.onSurfaceVariant)),
          const SizedBox(height: Espacios.interno),
          FilledButton(onPressed: catalogo.restaurar, child: const Text(T.s4Restaurar)),
        ]),
      ),
    );
  }
}

// Pestaña «Cuadrícula»: S4-07
class _PaginaCuadricula extends StatefulWidget {
  const _PaginaCuadricula();

  @override
  State<_PaginaCuadricula> createState() => _PaginaCuadriculaState();
}

class _PaginaCuadriculaState extends State<_PaginaCuadricula> {
  static const _total = 12;
  final Set<int> _seleccionadas = {};

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Column(children: [
      const _DocPlegable(T.s4DocCuadricula, abiertaAlInicio: true),
      Padding(
        padding: const EdgeInsets.symmetric(horizontal: Espacios.pantalla, vertical: 4),
        child: Align(
          alignment: Alignment.centerLeft,
          child: Text(f(T.s4CuadriculaEstado, [_seleccionadas.length, _total]), style: tema.textTheme.labelLarge),
        ),
      ),
      Expanded(
        child: GridView.builder(
          padding: const EdgeInsets.all(Espacios.chico),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 3,
            mainAxisSpacing: Espacios.chico,
            crossAxisSpacing: Espacios.chico,
            childAspectRatio: 1.3,
          ),
          itemCount: _total,
          itemBuilder: (context, i) {
            final numero = i + 1;
            final elegida = _seleccionadas.contains(numero);
            return Card.outlined(
              margin: EdgeInsets.zero,
              color: elegida ? tema.colorScheme.secondaryContainer : null,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
                side: BorderSide(
                  color: elegida ? tema.colorScheme.primary : tema.colorScheme.outlineVariant,
                  width: elegida ? 2 : 1,
                ),
              ),
              clipBehavior: Clip.antiAlias,
              child: InkWell(
                onTap: () => setState(() => elegida ? _seleccionadas.remove(numero) : _seleccionadas.add(numero)),
                child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                  Text('$numero', style: tema.textTheme.headlineMedium?.copyWith(color: tema.colorScheme.primary)),
                  Text(f(T.s4CuadriculaEtiqueta, [numero]), style: tema.textTheme.bodySmall),
                ]),
              ),
            );
          },
        ),
      ),
    ]);
  }
}

// Pestaña «Secciones»: S4-08 (encabezados fijos + elementos)
class _PaginaSecciones extends StatelessWidget {
  const _PaginaSecciones();

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return ListenableBuilder(
      listenable: catalogo,
      builder: (context, _) {
        final grupos = <String, List<ItemCatalogo>>{};
        for (final item in catalogo.items) {
          grupos.putIfAbsent(item.categoria, () => []).add(item);
        }
        final categorias = grupos.keys.toList()..sort();
        return Column(children: [
          const _DocPlegable(T.s4DocSecciones, abiertaAlInicio: true),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Espacios.pantalla, vertical: 4),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Text(f(T.s4SeccionesEstado, [grupos.length, catalogo.items.length]),
                  style: tema.textTheme.labelLarge),
            ),
          ),
          Expanded(
            child: CustomScrollView(slivers: [
              for (final categoria in categorias)
                // Cada grupo tiene su encabezado fijo mientras se recorren sus elementos.
                SliverMainAxisGroup(slivers: [
                  PinnedHeaderSliver(
                    child: Container(
                      width: double.infinity,
                      color: tema.colorScheme.secondaryContainer,
                      padding: const EdgeInsets.symmetric(horizontal: Espacios.pantalla, vertical: Espacios.chico),
                      child: Text('$categoria (${grupos[categoria]!.length})',
                          style: tema.textTheme.titleSmall?.copyWith(color: tema.colorScheme.onSecondaryContainer)),
                    ),
                  ),
                  SliverList.list(children: [
                    for (final item in grupos[categoria]!)
                      _FilaElemento(
                        item,
                        alTocar: () => showDialog<void>(
                          context: context,
                          builder: (context) => AlertDialog(
                            title: Text(item.nombre),
                            content: Text(item.categoria),
                            actions: [
                              TextButton(onPressed: () => Navigator.pop(context), child: const Text(T.s4Cerrar)),
                            ],
                          ),
                        ),
                      ),
                  ]),
                ]),
            ]),
          ),
        ]);
      },
    );
  }
}
