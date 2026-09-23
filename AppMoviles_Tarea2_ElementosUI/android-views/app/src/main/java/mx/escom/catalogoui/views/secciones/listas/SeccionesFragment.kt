package mx.escom.catalogoui.views.secciones.listas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.comun.CatalogoViewModel
import mx.escom.catalogoui.views.comun.configurar
import mx.escom.catalogoui.views.databinding.FragmentColeccionBinding

/** Pestaña «Secciones» de la Sección 4: lista con encabezados (dos tipos de fila). */
class SeccionesFragment : Fragment() {

    private var _binding: FragmentColeccionBinding? = null
    private val binding get() = _binding!!

    private val catalogo: CatalogoViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentColeccionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.doc.configurar(R.string.s4_doc_secciones, abiertaAlInicio = true)
        val adaptador = ItemsAdapter { item ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(item.nombre)
                .setMessage(item.categoria)
                .setPositiveButton(R.string.s4_cerrar, null)
                .show()
        }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adaptador

        catalogo.items.observe(viewLifecycleOwner) { items ->
            val grupos = items.groupBy { it.categoria }.toSortedMap()
            val filas = grupos.flatMap { (categoria, elementos) ->
                listOf<Fila>(Fila.Encabezado("$categoria (${elementos.size})")) + elementos.map { Fila.Elemento(it) }
            }
            adaptador.submitList(filas)
            binding.tvEstado.text = getString(R.string.s4_secciones_estado, grupos.size, items.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
