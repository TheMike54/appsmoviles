package mx.escom.catalogoui.views.secciones

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.FragmentBotonesBinding

/** Sección 2: botones y acciones. Cada botón produce una respuesta visible. */
class BotonesFragment : Fragment() {

    private var _binding: FragmentBotonesBinding? = null
    private val binding get() = _binding!!

    private val manejador = Handler(Looper.getMainLooper())
    private var toquesRelleno = 0
    private var toquesContorno = 0
    private var toquesTexto = 0
    private var toquesFab = 0
    private var vecesCompartido = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBotonesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // S2-01: tres niveles de énfasis, cada uno con su contador.
        val actualizarBasicos = {
            binding.tvBasicos.text = getString(R.string.s2_basicos_contador, toquesRelleno, toquesContorno, toquesTexto)
        }
        binding.btnRelleno.setOnClickListener { toquesRelleno++; actualizarBasicos() }
        binding.btnContorno.setOnClickListener { toquesContorno++; actualizarBasicos() }
        binding.btnTexto.setOnClickListener { toquesTexto++; actualizarBasicos() }
        actualizarBasicos()

        // S2-02: botón de solo ícono (se marca/desmarca) y botón con ícono + texto.
        binding.btnFavorito.addOnCheckedChangeListener { boton, marcado ->
            boton.setIconResource(if (marcado) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
            binding.tvIcono.setText(if (marcado) R.string.s2_favorito_si else R.string.s2_favorito_no)
        }
        binding.btnCompartir.setOnClickListener {
            vecesCompartido++
            binding.tvIcono.text = getString(R.string.s2_compartido, vecesCompartido)
        }

        // S2-03: FAB normal suma; el extendido se encoge o se expande.
        binding.fabNormal.setOnClickListener {
            toquesFab++
            binding.tvFab.text = getString(R.string.s2_fab_contador, toquesFab)
        }
        binding.fabExtendido.setOnClickListener {
            if (binding.fabExtendido.isExtended) {
                binding.fabExtendido.shrink()
                binding.tvFab.setText(R.string.s2_fab_encogido)
            } else {
                binding.fabExtendido.extend()
                binding.tvFab.setText(R.string.s2_fab_expandido)
            }
        }

        // S2-04: selector segmentado de una sola opción.
        val mostrarVista = { id: Int ->
            val vista = when (id) {
                R.id.toggle_semana -> getString(R.string.s2_semana)
                R.id.toggle_mes -> getString(R.string.s2_mes)
                else -> getString(R.string.s2_dia)
            }
            binding.tvToggle.text = getString(R.string.s2_toggle_elegido, vista)
        }
        binding.grupoToggle.addOnButtonCheckedListener { _, id, marcado -> if (marcado) mostrarVista(id) }
        mostrarVista(binding.grupoToggle.checkedButtonId)

        // S2-05: el botón solo se habilita al aceptar los términos.
        binding.switchTerminos.setOnCheckedChangeListener { _, aceptado ->
            binding.btnContinuar.isEnabled = aceptado
            binding.tvDeshabilitado.setText(if (aceptado) R.string.s2_deshabilitado_estado_on else R.string.s2_deshabilitado_estado_off)
        }
        binding.btnContinuar.setOnClickListener { binding.tvDeshabilitado.setText(R.string.s2_continuaste) }

        // S2-06: botón en estado de carga durante 2 segundos.
        binding.btnCarga.setOnClickListener {
            binding.btnCarga.isEnabled = false
            binding.btnCarga.text = ""
            binding.progresoCarga.visibility = View.VISIBLE
            binding.tvCarga.setText(R.string.s2_cargando)
            manejador.postDelayed({
                _binding?.let {
                    it.btnCarga.isEnabled = true
                    it.btnCarga.setText(R.string.s2_descargar)
                    it.progresoCarga.visibility = View.GONE
                    it.tvCarga.setText(R.string.s2_listo)
                }
            }, 2000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        manejador.removeCallbacksAndMessages(null)
        _binding = null
    }
}
