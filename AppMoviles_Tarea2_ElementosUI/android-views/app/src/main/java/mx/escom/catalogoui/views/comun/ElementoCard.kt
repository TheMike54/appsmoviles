package mx.escom.catalogoui.views.comun

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import mx.escom.catalogoui.views.R

/**
 * Tarjeta que documenta un elemento del catálogo: muestra su nombre, una explicación
 * corta y, debajo, la demostración interactiva.
 *
 * Las vistas que se escriben dentro de <ElementoCard> en el XML se mueven al contenedor
 * de la demostración, así cada sección solo declara el título, la descripción y la demo.
 */
class ElementoCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewOutlinedStyle,
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val raiz: View = LayoutInflater.from(context)
        .inflate(R.layout.view_elemento_card, this, false)
    private val tvTitulo: TextView = raiz.findViewById(R.id.tv_titulo)
    private val tvDescripcion: TextView = raiz.findViewById(R.id.tv_descripcion)
    private val contenedorDemo: LinearLayout = raiz.findViewById(R.id.contenedor_demo)

    init {
        addView(raiz)
        context.obtainStyledAttributes(attrs, R.styleable.ElementoCard).apply {
            tvTitulo.text = getString(R.styleable.ElementoCard_titulo)
            tvDescripcion.text = getString(R.styleable.ElementoCard_descripcion)
            recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // Todo lo que no sea la estructura interna es parte de la demostración.
        val hijos = (0 until childCount).map { getChildAt(it) }.filter { it !== raiz }
        hijos.forEach { hijo ->
            removeView(hijo)
            contenedorDemo.addView(hijo)
        }
    }
}
