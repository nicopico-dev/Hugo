package fr.nicopico.hugo.ui.babies

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.View
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.redux.ADD_BABY
import fr.nicopico.hugo.redux.REMOVE_BABY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.UPDATE_BABY
import fr.nicopico.hugo.service.babyService
import fr.nicopico.hugo.ui.shared.FormDialogFragment
import fr.nicopico.hugo.ui.shared.argument
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.editorAction
import fr.nicopico.hugo.ui.shared.isNotEmpty
import fr.nicopico.hugo.ui.shared.show
import fr.nicopico.hugo.ui.shared.textChanged
import fr.nicopico.hugo.ui.shared.textS
import fr.nicopico.hugo.ui.shared.withArguments
import fr.nicopico.hugo.utils.loadSuspend
import fr.nicopico.hugo.utils.then
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.android.synthetic.main.form_baby.*
import kotlinx.coroutines.experimental.Deferred

fun Fragment.addBabyDialog() = AddBabyDialogFragment.create().show(fragmentManager!!, null)
fun Fragment.editBabyDialog(baby: Baby) = EditBabyDialogFragment.create(baby).show(fragmentManager!!, null)

open class AddBabyDialogFragment : FormDialogFragment(), ReduxView {

    companion object {
        fun create() = AddBabyDialogFragment()
    }

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
        dismiss()
    }
}

class EditBabyDialogFragment : AddBabyDialogFragment() {

    companion object {
        private const val ARG_BABY_KEY = "ARG_BABY_KEY"

        fun create(baby: Baby) = EditBabyDialogFragment()
                .withArguments(ARG_BABY_KEY to baby.key)
    }

    private val babyKey by argument<String>(ARG_BABY_KEY)
    private var deferredBaby: Deferred<Baby>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            deferredBaby = loadSuspend {
                babyService.get(babyKey)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgDelete.apply {
            show()
            click {
                dispatch(REMOVE_BABY(buildBaby()))
                dismiss()
            }
        }

        deferredBaby?.then {
            edtName.textS = it.name
            edtName.selectAll()
        }
    }

    override fun buildBaby(): Baby {
        return super.buildBaby().copy(key = babyKey)
    }

    override fun onSubmit(view: View) {
        val updatedBaby = buildBaby()
        dispatch(UPDATE_BABY(updatedBaby))
        dismiss()
    }
}