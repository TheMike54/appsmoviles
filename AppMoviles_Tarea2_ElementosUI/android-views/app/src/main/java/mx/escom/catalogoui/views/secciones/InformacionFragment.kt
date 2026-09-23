package mx.escom.catalogoui.views.secciones

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.text.inSpans
import androidx.fragment.app.Fragment
import coil3.load
import coil3.request.crossfade
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.FragmentInformacionBinding
import mx.escom.catalogoui.views.databinding.HojaOpcionesBinding

/** Sección 5: elementos que informan o dan retroalimentación al usuario. */
class InformacionFragment : Fragment() {

    private var _binding: FragmentInformacionBinding? = null
    private val binding get() = _binding!!

    private var progreso = 30
    private var sinLeer = 3
    private var badge: BadgeDrawable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInformacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configurarTextos()
        configurarImagenes()
        configurarProgreso()
        configurarMensajes()
        configurarDialogo()
        configurarHoja()
        configurarBadge()
    }

    private fun configurarTextos() {
        val colorPrimario = MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorPrimary)
        // Un mismo TextView con varios énfasis usando spans.
        binding.tvEnfasis.text = SpannableStringBuilder()
            .append("Texto normal, ")
            .inSpans(StyleSpan(Typeface.BOLD)) { append("negrita") }
            .append(", ")
            .inSpans(StyleSpan(Typeface.ITALIC)) { append("cursiva") }
            .append(", ")
            .inSpans(UnderlineSpan()) { append("subrayado") }
            .append(" y ")
            .inSpans(ForegroundColorSpan(colorPrimario), StyleSpan(Typeface.BOLD)) { append("color") }
            .append(".")

        val aplicarTamano = { sp: Float ->
            binding.tvTitular.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
            binding.tvTamano.text = getString(R.string.s5_tamano_valor, sp.toInt())
        }
        binding.sliderTamano.addOnChangeListener { _, valor, _ -> aplicarTamano(valor) }
        aplicarTamano(binding.sliderTamano.value)
    }

    private fun configurarImagenes() {
        binding.grupoEscalado.setOnCheckedStateChangeListener { _, ids ->
            val escala = when (ids.firstOrNull()) {
                R.id.chip_ajustar -> ImageView.ScaleType.FIT_CENTER
                R.id.chip_original -> ImageView.ScaleType.CENTER
                else -> ImageView.ScaleType.CENTER_CROP
            }
            binding.ivLocal.scaleType = escala
            binding.ivUrl.scaleType = escala
        }
        binding.btnRecargarImagen.setOnClickListener { descargarImagen() }
        descargarImagen()
    }

    private fun descargarImagen() {
        // Coil descarga la imagen en segundo plano. Si no hay internet muestra un ícono de error.
        binding.ivUrl.load(URL_IMAGEN) {
            crossfade(true)
            listener(
                onStart = { _binding?.tvUrlEstado?.setText(R.string.s5_imagen_cargando) },
                onSuccess = { _, _ -> _binding?.tvUrlEstado?.setText(R.string.s5_imagen_url) },
                onError = { _, _ ->
                    _binding?.let {
                        it.tvUrlEstado.setText(R.string.s5_imagen_error)
                        it.ivUrl.setImageResource(R.drawable.ic_info)
                    }
                },
            )
        }
    }

    private fun configurarProgreso() {
        val mostrar = {
            binding.progresoLineal.setProgressCompat(progreso, true)
            binding.progresoCircular.setProgressCompat(progreso, true)
            binding.tvProgreso.text = getString(R.string.s5_progreso_valor, progreso)
            binding.btnAvanzar.isEnabled = progreso < 100
        }
        binding.btnAvanzar.setOnClickListener { progreso = (progreso + 10).coerceAtMost(100); mostrar() }
        binding.btnReiniciar.setOnClickListener { progreso = 0; mostrar() }
        mostrar()
    }

    private fun configurarMensajes() {
        binding.btnToast.setOnClickListener {
            Toast.makeText(requireContext(), R.string.s5_toast, Toast.LENGTH_SHORT).show()
            binding.tvMensajes.setText(R.string.s5_toast_mostrado)
        }
        binding.btnSnackbar.setOnClickListener {
            Snackbar.make(binding.raiz, R.string.s5_snackbar, Snackbar.LENGTH_LONG)
                .setAction(R.string.s4_deshacer) { _binding?.tvMensajes?.setText(R.string.s5_snackbar_deshecho) }
                .show()
        }
    }

    private fun configurarDialogo() {
        binding.btnDialogo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_delete)
                .setTitle(R.string.s5_dialogo_pregunta)
                .setMessage(R.string.s5_dialogo_mensaje)
                .setNegativeButton(R.string.s5_cancelar) { _, _ -> binding.tvDialogo.setText(R.string.s5_dialogo_cancelado) }
                .setPositiveButton(R.string.s5_eliminar) { _, _ -> binding.tvDialogo.setText(R.string.s5_dialogo_eliminado) }
                .show()
        }
    }

    private fun configurarHoja() {
        binding.btnHoja.setOnClickListener {
            val hoja = BottomSheetDialog(requireContext())
            val contenido = HojaOpcionesBinding.inflate(layoutInflater)
            var elegido = false
            listOf(contenido.opcionCompartir, contenido.opcionCopiar, contenido.opcionEditar).forEach { opcion ->
                opcion.setOnClickListener {
                    elegido = true
                    binding.tvHoja.text = getString(R.string.s5_hoja_elegido, opcion.text)
                    hoja.dismiss()
                }
            }
            hoja.setOnDismissListener { if (!elegido) _binding?.tvHoja?.setText(R.string.s5_hoja_cerrada) }
            hoja.setContentView(contenido.root)
            hoja.show()
        }
    }

    @OptIn(markerClass = [ExperimentalBadgeUtils::class])
    private fun configurarBadge() {
        val mostrar = {
            binding.tvBandeja.text = getString(R.string.s5_bandeja_estado, sinLeer)
            badge?.number = sinLeer
            badge?.isVisible = sinLeer > 0
        }
        // El badge se ancla cuando la vista ya tiene tamaño.
        binding.ivCorreo.post {
            val b = _binding ?: return@post
            badge = BadgeDrawable.create(requireContext()).also { BadgeUtils.attachBadgeDrawable(it, b.ivCorreo) }
            mostrar()
        }
        binding.btnNuevoCorreo.setOnClickListener { sinLeer++; mostrar() }
        binding.btnLeer.setOnClickListener { sinLeer = 0; mostrar() }
        mostrar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        badge = null
    }

    companion object {
        private const val URL_IMAGEN = "https://picsum.photos/id/1015/800/500"
    }
}
