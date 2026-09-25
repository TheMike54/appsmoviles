package mx.escom.catalogoui.compose.comun

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

/** Un elemento de las listas de la Sección 4. */
data class ItemCatalogo(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val desdeEntrada: Boolean = false,
)

/**
 * Estado compartido entre secciones. Se crea una sola vez a nivel de la Activity y se pasa
 * a las pantallas que lo necesitan.
 *
 * Es la "conexión entre secciones": lo que se captura en Entrada de texto (Sección 1)
 * se agrega a la lista vertical de Listas y colecciones (Sección 4). Como `items` es una
 * lista observable, Compose redibuja la lista sola cuando cambia.
 */
class CatalogoViewModel : ViewModel() {

    private var siguienteId = 1
    val items = mutableStateListOf<ItemCatalogo>().apply { addAll(itemsIniciales()) }

    fun agregarDesdeEntrada(nombre: String) {
        items.add(0, ItemCatalogo(siguienteId++, nombre, "Capturado en Entrada de texto", desdeEntrada = true))
    }

    fun agregar(item: ItemCatalogo, posicion: Int) {
        items.add(posicion.coerceIn(0, items.size), item)
    }

    fun agregarNuevo(): ItemCatalogo {
        val nuevo = ItemCatalogo(siguienteId, "Elemento nuevo $siguienteId", CATEGORIAS[siguienteId % CATEGORIAS.size])
        siguienteId++
        items.add(0, nuevo)
        return nuevo
    }

    fun eliminar(item: ItemCatalogo) {
        items.removeAll { it.id == item.id }
    }

    fun vaciar() = items.clear()

    fun restaurar() {
        items.clear()
        items.addAll(itemsIniciales())
    }

    private fun itemsIniciales(): List<ItemCatalogo> =
        (1..20).map { ItemCatalogo(siguienteId++, "Elemento $it", CATEGORIAS[it % CATEGORIAS.size]) }

    companion object {
        val CATEGORIAS = listOf("Fruta", "Verdura", "Grano")
    }
}
