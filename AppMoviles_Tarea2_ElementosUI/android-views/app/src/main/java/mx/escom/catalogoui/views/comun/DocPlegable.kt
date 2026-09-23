package mx.escom.catalogoui.views.comun

import android.view.View
import androidx.annotation.StringRes
import mx.escom.catalogoui.views.R
import mx.escom.catalogoui.views.databinding.ViewDocPlegableBinding

/** Llena la tarjeta de documentación plegable y hace que se abra o cierre al tocarla. */
fun ViewDocPlegableBinding.configurar(@StringRes cuerpo: Int, abiertaAlInicio: Boolean = false) {
    tvDocEncabezado.setText(R.string.s4_doc_encabezado)
    tvDocCuerpo.text = root.context.getText(cuerpo)
    tvDocCuerpo.visibility = if (abiertaAlInicio) View.VISIBLE else View.GONE
    root.setOnClickListener {
        tvDocCuerpo.visibility = if (tvDocCuerpo.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
}
