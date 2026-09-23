package mx.escom.catalogoui.views.secciones

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.FragmentContenedoresBinding

/** Sección 6: contenedores y estructura de pantalla. */
class ContenedoresFragment : Fragment() {

    private var _binding: FragmentContenedoresBinding? = null
    private val binding get() = _binding!!

    private var cajas = 3
    private var favorito = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContenedoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configurarDistribucion()
        configurarScroll()
        configurarToolbar()
        configurarBarraInferior()
        configurarPesosYRestricciones()
    }

    // S6-01 Fila, columna y superpuesta
    private fun configurarDistribucion() {
        binding.btnAgregarCaja.setOnClickListener { if (cajas < 6) cajas++; dibujarCajas() }
        binding.btnQuitarCaja.setOnClickListener { if (cajas > 1) cajas--; dibujarCajas() }
        binding.btnRotarCapas.setOnClickListener {
            // En un FrameLayout el último hijo se dibuja encima; bringToFront lo manda al final.
            binding.contenedorSuperpuesto.getChildAt(0).bringToFront()
            actualizarEstadoDistribucion()
        }
        dibujarCajas()
    }

    private fun dibujarCajas() {
        val densidad = resources.displayMetrics.density
        val color = MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorPrimary)
        val colorTexto = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnPrimary)
        listOf(binding.contenedorFila, binding.contenedorColumna).forEach { contenedor ->
            contenedor.removeAllViews()
            repeat(cajas) { i ->
                contenedor.addView(TextView(requireContext()).apply {
                    text = (i + 1).toString()
                    gravity = Gravity.CENTER
                    setTextColor(colorTexto)
                    setBackgroundResource(R.drawable.fondo_caja)
                    backgroundTintList = ColorStateList.valueOf(color)
                    val lado = (36 * densidad).toInt()
                    layoutParams = LinearLayout.LayoutParams(lado, lado).apply {
                        val margen = (4 * densidad).toInt()
                        setMargins(margen, margen, margen, margen)
                    }
                })
            }
        }
        actualizarEstadoDistribucion()
    }

    private fun actualizarEstadoDistribucion() {
        val frente = binding.contenedorSuperpuesto.getChildAt(binding.contenedorSuperpuesto.childCount - 1) as TextView
        binding.tvDistribucion.text = getString(R.string.s6_distribucion_estado, cajas, frente.text)
    }

    // S6-02 Contenedor con desplazamiento
    private fun configurarScroll() {
        val alturaRenglon = (28 * resources.displayMetrics.density).toInt()
        repeat(30) { i ->
            binding.renglones.addView(TextView(requireContext()).apply {
                text = getString(R.string.s6_renglon, i + 1)
                height = alturaRenglon
                gravity = Gravity.CENTER_VERTICAL
            })
        }
        val mostrar = { y: Int ->
            binding.tvScroll.text = getString(R.string.s6_scroll_estado, y, y / alturaRenglon + 1)
        }
        binding.scrollInterno.setOnScrollChangeListener { _: View, _: Int, y: Int, _: Int, _: Int -> mostrar(y) }
        mostrar(0)
    }

    // S6-03 Barra superior con acciones
    private fun configurarToolbar() {
        binding.toolbarDemo.setNavigationOnClickListener {
            binding.tvToolbar.text = getString(R.string.s6_toolbar_accion, getString(R.string.s6_accion_inicio))
        }
        binding.toolbarDemo.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.accion_favorito) {
                favorito = !favorito
                item.setIcon(if (favorito) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
            }
            binding.tvToolbar.text = getString(R.string.s6_toolbar_accion, item.title)
            true
        }
    }

    // S6-04 Barra de navegación inferior
    private fun configurarBarraInferior() {
        val mostrar = { id: Int ->
            val item = binding.bottomNav.menu.findItem(id)
            binding.tvBottomnavContenido.text = getString(R.string.s6_bottomnav_contenido, item.title)
            binding.tvBottomnavContenido.setCompoundDrawablesRelativeWithIntrinsicBounds(0, when (id) {
                R.id.destino_buscar -> R.drawable.ic_search
                R.id.destino_perfil -> R.drawable.ic_person
                else -> R.drawable.ic_home
            }, 0, 0)
        }
        binding.bottomNav.setOnItemSelectedListener { mostrar(it.itemId); true }
        // Ejemplo de badge en la barra inferior.
        binding.bottomNav.getOrCreateBadge(R.id.destino_perfil).number = 2
        mostrar(binding.bottomNav.selectedItemId)
    }

    // S6-05 Pesos proporcionales y restricciones
    private fun configurarPesosYRestricciones() {
        val aplicarPeso = { peso: Float ->
            binding.cajaPesoA.updateLayoutParams<LinearLayout.LayoutParams> { weight = 1f }
            binding.cajaPesoB.updateLayoutParams<LinearLayout.LayoutParams> { weight = peso }
            binding.cajaPesoA.text = "A · 1"
            binding.cajaPesoB.text = "B · ${peso.toInt()}"
            binding.tvPesos.text = getString(R.string.s6_pesos_estado, peso.toInt())
        }
        binding.sliderPeso.addOnChangeListener { _, valor, _ -> aplicarPeso(valor) }
        aplicarPeso(binding.sliderPeso.value)

        val aplicarBias = { bias: Float ->
            binding.cajaRestringida.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = bias }
            binding.tvRestricciones.text = getString(R.string.s6_restricciones_estado, bias)
        }
        binding.sliderBias.addOnChangeListener { _, valor, _ -> aplicarBias(valor) }
        aplicarBias(binding.sliderBias.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
