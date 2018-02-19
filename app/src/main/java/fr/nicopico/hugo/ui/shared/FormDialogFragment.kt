package fr.nicopico.hugo.ui.shared

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.view.plusAssign
import fr.nicopico.hugo.R
import kotlinx.android.synthetic.main.dialog_form.*

abstract class FormDialogFragment : BottomSheetDialogFragment() {

    protected abstract val dialogTitle: CharSequence
    protected open val dialogIcon: Int = 0
    protected open val formLayoutId: Int? = null
    protected open val formLayout: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        require(formLayout != null || formLayoutId != null)
        val layout = inflater.inflate(R.layout.dialog_form, container, false)
        return formLayoutId?.let { layoutId ->
            inflater.inflate(layoutId, layout.findViewById(R.id.formContainer))
        } ?: layout.apply {
            findViewById<ViewGroup>(R.id.formContainer) += formLayout!!
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTitle.apply {
            text = dialogTitle
            drawables(start = dialogIcon)
        }

        btnSubmit.click(::onSubmit)
        btnCancel.apply {
            visible(isCancelable)
            click(::onCancel)
        }
    }

    protected abstract fun onSubmit(view: View)

    protected open fun onCancel(view: View) {
        dismiss()
    }
}