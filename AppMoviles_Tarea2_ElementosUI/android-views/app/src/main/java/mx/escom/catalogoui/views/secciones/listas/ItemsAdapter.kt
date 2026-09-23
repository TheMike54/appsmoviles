package mx.escom.catalogoui.views.secciones.listas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.comun.ItemCatalogo
import mx.escom.catalogoui.views.databinding.ItemEncabezadoBinding
import mx.escom.catalogoui.views.databinding.ItemListaBinding

/** Lo que muestra una fila: un encabezado de grupo o un elemento. */
sealed interface Fila {
    data class Encabezado(val titulo: String) : Fila
    data class Elemento(val item: ItemCatalogo) : Fila
}

/**
 * Adaptador del RecyclerView. Maneja dos tipos de vista (viewType) para la lista con
 * encabezados; la lista vertical simple solo usa filas de tipo Elemento.
 */
class ItemsAdapter(private val alTocar: (ItemCatalogo) -> Unit) :
    ListAdapter<Fila, RecyclerView.ViewHolder>(Comparador) {

    class ElementoHolder(val binding: ItemListaBinding) : RecyclerView.ViewHolder(binding.root)
    class EncabezadoHolder(val binding: ItemEncabezadoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is Fila.Encabezado -> TIPO_ENCABEZADO
        is Fila.Elemento -> TIPO_ELEMENTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPO_ENCABEZADO) {
            EncabezadoHolder(ItemEncabezadoBinding.inflate(inflater, parent, false))
        } else {
            ElementoHolder(ItemListaBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val fila = getItem(position)) {
            is Fila.Encabezado -> (holder as EncabezadoHolder).binding.tvEncabezado.text = fila.titulo
            is Fila.Elemento -> with((holder as ElementoHolder).binding) {
                val item = fila.item
                tvInicial.text = if (item.desdeEntrada) {
                    item.nombre.first().uppercase()
                } else {
                    item.nombre.substringAfterLast(' ')
                }
                tvNombre.text = item.nombre
                tvCategoria.text = if (item.desdeEntrada) {
                    root.context.getString(R.string.s4_desde_entrada, "S1 → S4")
                } else {
                    item.categoria
                }
                root.setOnClickListener { alTocar(item) }
            }
        }
    }

    /** Elemento en la posición indicada, o null si es un encabezado. */
    fun itemEn(posicion: Int): ItemCatalogo? = (getItem(posicion) as? Fila.Elemento)?.item

    private object Comparador : DiffUtil.ItemCallback<Fila>() {
        override fun areItemsTheSame(a: Fila, b: Fila) = when {
            a is Fila.Elemento && b is Fila.Elemento -> a.item.id == b.item.id
            a is Fila.Encabezado && b is Fila.Encabezado -> a.titulo == b.titulo
            else -> false
        }

        override fun areContentsTheSame(a: Fila, b: Fila) = a == b
    }

    companion object {
        const val TIPO_ENCABEZADO = 0
        const val TIPO_ELEMENTO = 1
    }
}
