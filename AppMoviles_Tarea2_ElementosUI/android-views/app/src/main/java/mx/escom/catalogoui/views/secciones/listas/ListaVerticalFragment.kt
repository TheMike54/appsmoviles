package mx.escom.catalogoui.views.secciones.listas

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.comun.CatalogoViewModel
import mx.escom.catalogoui.views.comun.ItemCatalogo
import mx.escom.catalogoui.views.comun.configurar
import mx.escom.catalogoui.views.databinding.FragmentListaVerticalBinding

/** Pestaña «Lista» de la Sección 4. */
class ListaVerticalFragment : Fragment() {

    private var _binding: FragmentListaVerticalBinding? = null
    private val binding get() = _binding!!

    // Compartido con la Sección 1: lo que se agrega allá aparece aquí.
    private val catalogo: CatalogoViewModel by activityViewModels()
    private val manejador = Handler(Looper.getMainLooper())
    private val adaptador = ItemsAdapter { mostrarDetalle(it) }
    private var subirAlActualizar = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListaVerticalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.doc.configurar(R.string.s4_doc_lista)

        // S4-02 Lista vertical
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adaptador
        binding.recycler.addItemDecoration(
            MaterialDividerItemDecoration(requireContext(), MaterialDividerItemDecoration.VERTICAL).apply { isLastItemDecorated = false },
        )

        catalogo.items.observe(viewLifecycleOwner) { items ->
            adaptador.submitList(items.map { Fila.Elemento(it) }) {
                // Cuando la lista ya se actualizó, se sube para mostrar el elemento nuevo.
                if (subirAlActualizar) _binding?.recycler?.scrollToPosition(0)
                subirAlActualizar = false
            }
            binding.tvTotal.text = if (items.isEmpty()) getString(R.string.s4_total_vacio) else getString(R.string.s4_total, items.size)
            // S4-06 Estado vacío
            binding.estadoVacio.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            binding.swipeRefresh.visibility = if (items.isEmpty()) View.INVISIBLE else View.VISIBLE
            binding.btnVaciar.isEnabled = items.isNotEmpty()
        }

        binding.btnVaciar.setOnClickListener { catalogo.vaciar() }
        binding.btnRestaurar.setOnClickListener { catalogo.restaurar() }

        // S4-04 Deslizar para eliminar (con opción de deshacer)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, destino: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, direccion: Int) {
                val posicion = vh.bindingAdapterPosition
                val item = adaptador.itemEn(posicion) ?: return
                catalogo.eliminar(item)
                Snackbar.make(binding.root, getString(R.string.s4_eliminado, item.nombre), Snackbar.LENGTH_LONG)
                    .setAction(R.string.s4_deshacer) { catalogo.agregar(item, posicion) }
                    .show()
            }
        }).attachToRecyclerView(binding.recycler)

        // Sobre las filas, el gesto lateral es para borrar: se le pide al ViewPager2 (pestañas)
        // que no lo tome. Para cambiar de pestaña se toca el título o se desliza arriba de la lista.
        binding.recycler.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.actionMasked == MotionEvent.ACTION_DOWN) rv.parent.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })

        // S4-05 Arrastrar para actualizar
        binding.swipeRefresh.setOnRefreshListener {
            manejador.postDelayed({
                val b = _binding ?: return@postDelayed
                subirAlActualizar = true
                val nuevo = catalogo.agregarNuevo()
                b.swipeRefresh.isRefreshing = false
                Snackbar.make(b.root, getString(R.string.s4_actualizado, nuevo.nombre), Snackbar.LENGTH_SHORT).show()
            }, 1500)
        }
    }

    // S4-03 Detalle al seleccionar
    private fun mostrarDetalle(item: ItemCatalogo) {
        val nota = getString(if (item.desdeEntrada) R.string.s4_detalle_entrada else R.string.s4_detalle_normal)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(item.nombre)
            .setIcon(R.drawable.ic_info)
            .setMessage(getString(R.string.s4_detalle_mensaje, item.categoria, item.id, nota))
            .setPositiveButton(R.string.s4_cerrar, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        manejador.removeCallbacksAndMessages(null)
        _binding = null
    }
}
