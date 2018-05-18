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

class BabyEditionDialogFragment : FormDialogFragment(), StateProvider {

    override val dialogTitleId: Int = R.string.baby
    override val formLayoutId: Int = R.layout.form_baby

    override val state: AppState
        get() = _state
    private val baby: Baby? by lazy { (state.screen as? Screen.BabyEdition)?.baby }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtName.apply {
            textChanged { submittable = edtName.isNotEmpty() }
            editorAction { onSubmit(it) }
        }

        if (baby != null) {
            imgDelete.apply {
                show()
                click {
                    dispatch(REMOVE_BABY(buildBaby()))
                    dismiss()
                }
            }

            edtName.textS = baby!!.name
            edtName.selectAll()
        }
    }

    @CallSuper
    private fun buildBaby(): Baby = Baby(edtName.text.toString())
            .let {
                if (baby != null) it.copy(key = baby!!.key) else it
            }

    override fun onSubmit(view: View) {
        if (baby == null) {
            dispatch(ADD_BABY(buildBaby()))
        } else {
            dispatch(UPDATE_BABY(buildBaby()))
        }
        dismiss()
    }
}