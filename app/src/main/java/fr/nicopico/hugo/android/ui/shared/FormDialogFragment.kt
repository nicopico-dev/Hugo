package fr.nicopico.hugo.android.ui.shared

import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.plusAssign
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.drawables
import fr.nicopico.hugo.android.utils.visible
import fr.nicopico.hugo.domain.redux.POP_SCREEN
import kotlinx.android.synthetic.main.dialog_form.*
import kotlin.properties.Delegates

abstract class FormDialogFragment : BottomSheetDialogFragment(),
        ReduxDispatcher {

    protected abstract val dialogTitleId: Int
    protected open val dialogIconId: Int = 0
    protected open val formLayoutId: Int? = null
    protected open val formLayout: View? = null

    override fun dispatch(action: Any) = _dispatch(action)

    protected var submittable by Delegates.observable(true) { _, _, value ->
        btnSubmit.isEnabled = value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        require(formLayout != null || formLayoutId != null)
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.dialog_form, container, false)
                .apply {
                    findViewById<ViewGroup>(R.id.formContainer) += formLayoutId
                            ?.let { layoutId -> inflater.inflate(layoutId, formContainer, false) }
                            ?: formLayout!!
                }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTitle.apply {
            setText(dialogTitleId)
            drawables(start = dialogIconId)
        }

        btnSubmit.click(::onSubmit)
        btnCancel.apply {
            visible = isCancelable
            click(::onCancel)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dispatch(POP_SCREEN)
    }

    protected abstract fun onSubmit(view: View)

    protected open fun onCancel(view: View) {
        dismiss()
    }
}