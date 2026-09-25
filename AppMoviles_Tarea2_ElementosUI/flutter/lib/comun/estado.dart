import 'package:flutter/foundation.dart';

/// Un elemento de las listas de la Sección 4.
class ItemCatalogo {
  const ItemCatalogo(this.id, this.nombre, this.categoria, {this.desdeEntrada = false});

  final int id;
  final String nombre;
  final String categoria;
  final bool desdeEntrada;
}

/// Estado compartido entre secciones.
///
/// Es la "conexión entre secciones": lo que se captura en Entrada de texto (Sección 1)
/// se agrega a la lista vertical de Listas y colecciones (Sección 4). Como es un
/// ChangeNotifier, la lista se redibuja sola cuando cambia.
class CatalogoModelo extends ChangeNotifier {
  CatalogoModelo() {
    _items.addAll(_itemsIniciales());
  }

  static const categorias = ['Fruta', 'Verdura', 'Grano'];

  int _siguienteId = 1;
  final List<ItemCatalogo> _items = [];

  List<ItemCatalogo> get items => List.unmodifiable(_items);

  void agregarDesdeEntrada(String nombre) {
    _items.insert(0, ItemCatalogo(_siguienteId++, nombre, 'Capturado en Entrada de texto', desdeEntrada: true));
    notifyListeners();
  }

  void agregar(ItemCatalogo item, int posicion) {
    _items.insert(posicion.clamp(0, _items.length), item);
    notifyListeners();
  }

  ItemCatalogo agregarNuevo() {
    final nuevo = ItemCatalogo(_siguienteId, 'Elemento nuevo $_siguienteId', categorias[_siguienteId % categorias.length]);
    _siguienteId++;
    _items.insert(0, nuevo);
    notifyListeners();
    return nuevo;
  }

  int eliminar(ItemCatalogo item) {
    final posicion = _items.indexWhere((i) => i.id == item.id);
    if (posicion >= 0) _items.removeAt(posicion);
    notifyListeners();
    return posicion;
  }

  void vaciar() {
    _items.clear();
    notifyListeners();
  }

  void restaurar() {
    _items
      ..clear()
      ..addAll(_itemsIniciales());
    notifyListeners();
  }

  List<ItemCatalogo> _itemsIniciales() => [
        for (var i = 1; i <= 20; i++) ItemCatalogo(_siguienteId++, 'Elemento $i', categorias[i % categorias.length]),
      ];
}

/// Una sola instancia para toda la app (la comparten la Sección 1 y la Sección 4).
final catalogo = CatalogoModelo();
