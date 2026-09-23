package mx.escom.catalogoui.views.secciones.listas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.comun.configurar
import mx.escom.catalogoui.views.databinding.FragmentColeccionBinding
import mx.escom.catalogoui.views.databinding.ItemCuadriculaBinding

/** Pestaña «Cuadrícula» de la Sección 4: RecyclerView con GridLayoutManager. */
class CuadriculaFragment : Fragment() {

    private var _binding: FragmentColeccionBinding? = null
    private val binding get() = _binding!!

    private val total = 12
    private val seleccionadas = mutableSetOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentColeccionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.doc.configurar(R.string.s4_doc_cuadricula, abiertaAlInicio = true)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recycler.adapter = Adaptador()
        actualizarEstado()
    }

    private fun actualizarEstado() {
        binding.tvEstado.text = getString(R.string.s4_cuadricula_estado, seleccionadas.size, total)
    }

    private inner class Adaptador : RecyclerView.Adapter<Adaptador.Holder>() {
        inner class Holder(val b: ItemCuadriculaBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Holder(ItemCuadriculaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = total

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val numero = position + 1
            holder.b.tvNumero.text = numero.toString()
            holder.b.tvEtiqueta.text = getString(R.string.s4_cuadricula_etiqueta, numero)
            holder.b.tarjeta.isChecked = numero in seleccionadas
            holder.b.tarjeta.setOnClickListener {
                if (!seleccionadas.add(numero)) seleccionadas.remove(numero)
                holder.b.tarjeta.isChecked = numero in seleccionadas
                actualizarEstado()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
