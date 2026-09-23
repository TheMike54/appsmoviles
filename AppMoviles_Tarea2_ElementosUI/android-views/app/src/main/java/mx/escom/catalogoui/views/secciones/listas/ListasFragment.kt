package mx.escom.catalogoui.views.secciones.listas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.FragmentListasBinding

/**
 * Sección 4: listas y colecciones. Un TabLayout enlazado a un ViewPager2 reparte los
 * elementos en tres pestañas; cada página es un Fragment hijo.
 */
class ListasFragment : Fragment() {

    private var _binding: FragmentListasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> ListaVerticalFragment()
                1 -> CuadriculaFragment()
                else -> SeccionesFragment()
            }
        }
        val titulos = listOf(R.string.s4_tab_lista, R.string.s4_tab_cuadricula, R.string.s4_tab_secciones)
        val iconos = listOf(R.drawable.ic_list, R.drawable.ic_dashboard, R.drawable.ic_info)
        TabLayoutMediator(binding.tabs, binding.pager) { tab, posicion ->
            tab.setText(titulos[posicion])
            tab.setIcon(iconos[posicion])
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
