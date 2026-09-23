package mx.escom.catalogoui.views.secciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.comun.CatalogoViewModel
import mx.escom.catalogoui.views.databinding.FragmentEntradaTextoBinding

/** Sección 1: elementos para capturar texto. */
class EntradaTextoFragment : Fragment() {

    private var _binding: FragmentEntradaTextoBinding? = null
    private val binding get() = _binding!!

    // Mismo ViewModel que usa la Sección 4 (ámbito de la Activity).
    private val catalogo: CatalogoViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEntradaTextoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configurarCampoSimple()
        configurarValidacion()
        configurarPassword()
        configurarTeclados()
        configurarMultilinea()
        configurarSugerencias()
        configurarBusqueda()
    }

    private fun configurarCampoSimple() {
        binding.etSimple.doAfterTextChanged { texto ->
            binding.tvSimpleEco.text = if (texto.isNullOrBlank()) {
                getString(R.string.s1_simple_vacio)
            } else {
                getString(R.string.s1_simple_eco, texto)
            }
        }
        binding.btnAgregarLista.setOnClickListener {
            val nombre = binding.etSimple.text?.toString()?.trim().orEmpty()
            if (nombre.isEmpty()) {
                binding.tilSimple.error = getString(R.string.s1_simple_falta)
                return@setOnClickListener
            }
            binding.tilSimple.error = null
            catalogo.agregarDesdeEntrada(nombre)
            binding.etSimple.text = null
            Snackbar.make(binding.root, getString(R.string.s1_simple_agregado, nombre), Snackbar.LENGTH_LONG)
                .setAction(R.string.s1_simple_ver_lista) { findNavController().navigate(R.id.listasFragment) }
                .show()
        }
    }

    private fun configurarValidacion() {
        val soloLetras = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]{3,}$")
        binding.etValidacion.doAfterTextChanged { texto ->
            val valor = texto?.toString().orEmpty()
            when {
                valor.isEmpty() -> {
                    binding.tilValidacion.error = null
                    binding.tilValidacion.helperText = null
                }
                soloLetras.matches(valor) -> {
                    binding.tilValidacion.error = null
                    binding.tilValidacion.helperText = getString(R.string.s1_validacion_ok)
                }
                else -> binding.tilValidacion.error = getString(R.string.s1_validacion_error)
            }
        }
    }

    private fun configurarPassword() {
        binding.etPassword.doAfterTextChanged { texto ->
            val largo = texto?.length ?: 0
            val fuerza = when {
                largo >= 10 -> getString(R.string.s1_password_fuerte)
                largo >= 6 -> getString(R.string.s1_password_media)
                else -> getString(R.string.s1_password_debil)
            }
            binding.tilPassword.helperText = if (largo == 0) null else getString(R.string.s1_password_fuerza, largo, fuerza)
        }
    }

    private fun configurarTeclados() {
        val actualizar = {
            binding.tvTecladosResumen.text = getString(
                R.string.s1_teclados_resumen,
                binding.etNumero.text?.toString().orEmpty().ifEmpty { "—" },
                binding.etCorreo.text?.toString().orEmpty().ifEmpty { "—" },
                binding.etTelefono.text?.toString().orEmpty().ifEmpty { "—" },
            )
        }
        binding.etNumero.doAfterTextChanged { actualizar() }
        binding.etCorreo.doAfterTextChanged { actualizar() }
        binding.etTelefono.doAfterTextChanged { actualizar() }
        actualizar()
    }

    private fun configurarMultilinea() {
        val actualizar = { texto: CharSequence? ->
            val lineas = if (texto.isNullOrEmpty()) 0 else texto.lines().size
            binding.tvMultilineaLineas.text = getString(R.string.s1_multilinea_lineas, lineas)
        }
        binding.etMultilinea.doAfterTextChanged { actualizar(it) }
        actualizar(null)
    }

    private fun configurarSugerencias() {
        val estados = resources.getStringArray(R.array.estados_mexico)
        binding.acEstados.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, estados))
        binding.acEstados.setOnItemClickListener { parent, _, posicion, _ ->
            binding.tvSugerenciaElegida.text = getString(R.string.s1_sugerencias_elegido, parent.getItemAtPosition(posicion) as String)
        }
    }

    private fun configurarBusqueda() {
        val lenguajes = resources.getStringArray(R.array.lenguajes).toList()
        val filtrar = { consulta: String ->
            val resultado = lenguajes.filter { it.contains(consulta.trim(), ignoreCase = true) }
            binding.tvBusquedaResultados.text = if (resultado.isEmpty()) {
                getString(R.string.s1_busqueda_sin_resultados, consulta)
            } else {
                getString(R.string.s1_busqueda_resultados, resultado.size, resultado.joinToString(", "))
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrar(newText.orEmpty())
                return true
            }
        })
        filtrar("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
