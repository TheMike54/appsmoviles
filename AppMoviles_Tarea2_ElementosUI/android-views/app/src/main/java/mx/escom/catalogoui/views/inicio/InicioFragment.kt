package mx.escom.catalogoui.views.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.FragmentInicioBinding
import mx.escom.catalogoui.views.databinding.ItemSeccionBinding

/** Pantalla principal: explica la app y muestra una tarjeta por sección. */
class InicioFragment : Fragment() {

    private data class Seccion(
        @IdRes val destino: Int,
        @StringRes val titulo: Int,
        @StringRes val descripcion: Int,
        @DrawableRes val icono: Int,
    )

    private val secciones = listOf(
        Seccion(R.id.entradaTextoFragment, R.string.titulo_s1, R.string.inicio_desc_s1, R.drawable.ic_edit),
        Seccion(R.id.botonesFragment, R.string.titulo_s2, R.string.inicio_desc_s2, R.drawable.ic_touch),
        Seccion(R.id.seleccionFragment, R.string.titulo_s3, R.string.inicio_desc_s3, R.drawable.ic_check_box),
        Seccion(R.id.listasFragment, R.string.titulo_s4, R.string.inicio_desc_s4, R.drawable.ic_list),
        Seccion(R.id.informacionFragment, R.string.titulo_s5, R.string.inicio_desc_s5, R.drawable.ic_info),
        Seccion(R.id.contenedoresFragment, R.string.titulo_s6, R.string.inicio_desc_s6, R.drawable.ic_dashboard),
    )

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        secciones.forEachIndexed { indice, seccion ->
            val tarjeta = ItemSeccionBinding.inflate(layoutInflater, binding.listaSecciones, true)
            tarjeta.tvTitulo.text = getString(R.string.inicio_formato_seccion, indice + 1, getString(seccion.titulo))
            tarjeta.tvDescripcion.setText(seccion.descripcion)
            tarjeta.ivIcono.setImageResource(seccion.icono)
            tarjeta.root.setOnClickListener { findNavController().navigate(seccion.destino) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
