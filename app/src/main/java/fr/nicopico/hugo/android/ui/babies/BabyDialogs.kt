package fr.nicopico.hugo.android.ui.babies

import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.StateProvider
import fr.nicopico.hugo.android.ui.shared.FormDialogFragment
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.editorAction
import fr.nicopico.hugo.android.utils.isNotEmpty
import fr.nicopico.hugo.android.utils.show
import fr.nicopico.hugo.android.utils.textChanged
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.redux.ADD_BABY
import fr.nicopico.hugo.domain.redux.REMOVE_BABY
import fr.nicopico.hugo.domain.redux.UPDATE_BABY
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.android.synthetic.main.form_baby.*

open class AddBabyDialogFragment : FormDialogFragment() {

    override val dialogTitleId: Int = R.string.baby
    override val formLayoutId: Int = R.layout.form_baby

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtName.apply {
            textChanged { submittable = edtName.isNotEmpty() }
            editorAction { onSubmit(it) }
        }
    }

    @CallSuper
    protected open fun buildBaby(): Baby = Baby(edtName.text.toString())

    override fun onSubmit(view: View) {
        dispatch(ADD_BABY(buildBaby()))
    }
}

class EditBabyDialogFragment : AddBabyDialogFragment(), StateProvider {

    override val state: AppState
        get() = _state
    private val baby by lazy { (state.screen as Screen.BabyEdition).baby }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgDelete.apply {
            show()
            click {
                dispatch(REMOVE_BABY(buildBaby()))
                dismiss()
            }
        }

        edtName.textS = baby.name
        edtName.selectAll()
    }

    override fun buildBaby(): Baby {
        return super.buildBaby().copy(key = baby.key)
    }

    override fun onSubmit(view: View) {
        val updatedBaby = buildBaby()
        dispatch(UPDATE_BABY(updatedBaby))
        dismiss()
    }
}