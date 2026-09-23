package mx.escom.catalogoui.views.comun

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/** Un elemento de las listas de la Sección 4. */
data class ItemCatalogo(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val desdeEntrada: Boolean = false,
)

/**
 * Estado compartido entre secciones. Vive mientras vive la Activity, así que cualquier
 * Fragment lo obtiene con `activityViewModels()` y ve los mismos datos.
 *
 * Es la "conexión entre secciones": lo que se captura en Entrada de texto (Sección 1)
 * se agrega a la lista vertical de Listas y colecciones (Sección 4).
 */
class CatalogoViewModel : ViewModel() {

    private var siguienteId = 1
    private val _items = MutableLiveData(itemsIniciales())
    val items: LiveData<List<ItemCatalogo>> = _items

    fun agregarDesdeEntrada(nombre: String) {
        val nuevo = ItemCatalogo(siguienteId++, nombre, "Capturado en Entrada de texto", desdeEntrada = true)
        _items.value = listOf(nuevo) + _items.value.orEmpty()
    }

    fun agregar(item: ItemCatalogo, posicion: Int) {
        val lista = _items.value.orEmpty().toMutableList()
        lista.add(posicion.coerceIn(0, lista.size), item)
        _items.value = lista
    }

    fun agregarNuevo(): ItemCatalogo {
        val nuevo = ItemCatalogo(siguienteId, "Elemento nuevo $siguienteId", CATEGORIAS[siguienteId % CATEGORIAS.size])
        siguienteId++
        _items.value = listOf(nuevo) + _items.value.orEmpty()
        return nuevo
    }

    fun eliminar(item: ItemCatalogo) {
        _items.value = _items.value.orEmpty().filterNot { it.id == item.id }
    }

    fun vaciar() {
        _items.value = emptyList()
    }

    fun restaurar() {
        _items.value = itemsIniciales()
    }

    private fun itemsIniciales(): List<ItemCatalogo> =
        (1..20).map { ItemCatalogo(siguienteId++, "Elemento $it", CATEGORIAS[it % CATEGORIAS.size]) }

    companion object {
        val CATEGORIAS = listOf("Fruta", "Verdura", "Grano")
    }
}
