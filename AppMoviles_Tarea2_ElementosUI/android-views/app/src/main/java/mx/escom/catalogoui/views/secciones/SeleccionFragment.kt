package mx.escom.catalogoui.views.secciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.FragmentSeleccionBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Sección 3: elementos para elegir entre opciones. */
class SeleccionFragment : Fragment() {

    private var _binding: FragmentSeleccionBinding? = null
    private val binding get() = _binding!!

    private var fechaElegida: String? = null
    private var horaElegida: String? = null

    private val comidas = listOf(
        "Manzana" to R.id.chip_fruta, "Plátano" to R.id.chip_fruta, "Mango" to R.id.chip_fruta,
        "Zanahoria" to R.id.chip_verdura, "Brócoli" to R.id.chip_verdura, "Espinaca" to R.id.chip_verdura,
        "Arroz" to R.id.chip_grano, "Maíz" to R.id.chip_grano, "Avena" to R.id.chip_grano,
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeleccionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configurarCasillas()
        configurarOpciones()
        configurarInterruptor()
        configurarDeslizadores()
        configurarDesplegable()
        configurarFechaHora()
        configurarChips()
    }

    private fun configurarCasillas() {
        val hijas = listOf(binding.cbQueso, binding.cbChampinones, binding.cbPimiento)
        var actualizando = false

        val refrescar = {
            val marcadas = hijas.count { it.isChecked }
            actualizando = true
            binding.cbTodos.checkedState = when (marcadas) {
                0 -> MaterialCheckBox.STATE_UNCHECKED
                hijas.size -> MaterialCheckBox.STATE_CHECKED
                else -> MaterialCheckBox.STATE_INDETERMINATE
            }
            actualizando = false
            val estado = when (binding.cbTodos.checkedState) {
                MaterialCheckBox.STATE_CHECKED -> getString(R.string.s3_estado_marcado)
                MaterialCheckBox.STATE_INDETERMINATE -> getString(R.string.s3_estado_indeterminado)
                else -> getString(R.string.s3_estado_desmarcado)
            }
            binding.tvCheckbox.text = getString(R.string.s3_checkbox_estado, marcadas, estado)
        }

        hijas.forEach { casilla -> casilla.setOnCheckedChangeListener { _, _ -> if (!actualizando) refrescar() } }
        binding.cbTodos.addOnCheckedStateChangedListener { _, estado ->
            if (actualizando) return@addOnCheckedStateChangedListener
            // Tocar «Todos» marca o desmarca a todas las hijas.
            val marcar = estado == MaterialCheckBox.STATE_CHECKED
            actualizando = true
            hijas.forEach { it.isChecked = marcar }
            actualizando = false
            refrescar()
        }
        binding.cbQueso.isChecked = true
        refrescar()
    }

    private fun configurarOpciones() {
        binding.grupoTamano.setOnCheckedChangeListener { grupo, id ->
            val texto = grupo.findViewById<android.widget.RadioButton>(id)?.text ?: return@setOnCheckedChangeListener
            binding.tvRadio.text = getString(R.string.s3_radio_elegido, texto)
        }
    }

    private fun configurarInterruptor() {
        binding.switchNotificaciones.setOnCheckedChangeListener { _, activo ->
            binding.tvSwitch.setText(if (activo) R.string.s3_switch_on else R.string.s3_switch_off)
        }
    }

    private fun configurarDeslizadores() {
        val mostrarVolumen = { valor: Float -> binding.tvVolumen.text = getString(R.string.s3_volumen_valor, valor.toInt()) }
        binding.sliderVolumen.addOnChangeListener { _, valor, _ -> mostrarVolumen(valor) }
        mostrarVolumen(binding.sliderVolumen.value)

        val mostrarRango = {
            val (minimo, maximo) = binding.sliderRango.values
            binding.tvRango.text = getString(R.string.s3_rango_valor, minimo.toInt(), maximo.toInt())
        }
        binding.sliderRango.setValues(200f, 700f)
        binding.sliderRango.addOnChangeListener { _, _, _ -> mostrarRango() }
        mostrarRango()
    }

    private fun configurarDesplegable() {
        val paises = resources.getStringArray(R.array.paises)
        binding.spinnerPais.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, paises)
        binding.spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, vista: View?, posicion: Int, id: Long) {
                binding.tvSpinner.text = getString(R.string.s3_spinner_elegido, paises[posicion])
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun configurarFechaHora() {
        val mostrar = {
            binding.tvFechaHora.text = getString(
                R.string.s3_fecha_hora,
                fechaElegida ?: getString(R.string.s3_sin_elegir),
                horaElegida ?: getString(R.string.s3_sin_elegir),
            )
        }
        binding.btnFecha.setOnClickListener {
            val selector = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.s3_elegir_fecha)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            selector.addOnPositiveButtonClickListener { milis ->
                // MaterialDatePicker entrega la fecha en UTC.
                val formato = SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "MX"))
                formato.timeZone = TimeZone.getTimeZone("UTC")
                fechaElegida = formato.format(milis)
                mostrar()
            }
            selector.show(childFragmentManager, "fecha")
        }
        binding.btnHora.setOnClickListener {
            val selector = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(R.string.s3_elegir_hora)
                .build()
            selector.addOnPositiveButtonClickListener {
                horaElegida = String.format(Locale.getDefault(), "%02d:%02d", selector.hour, selector.minute)
                mostrar()
            }
            selector.show(childFragmentManager, "hora")
        }
        mostrar()
    }

    private fun configurarChips() {
        val filtrar = {
            val activos = binding.grupoChips.checkedChipIds
            val visibles = if (activos.isEmpty()) comidas else comidas.filter { it.second in activos }
            binding.tvChips.text = getString(R.string.s3_chips_resultado, visibles.size, visibles.joinToString(", ") { it.first })
        }
        binding.grupoChips.setOnCheckedStateChangeListener { _, _ -> filtrar() }
        filtrar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
