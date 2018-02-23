package fr.nicopico.hugo.ui.babies

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.redux.*
import fr.nicopico.hugo.ui.shared.*
import fr.nicopico.hugo.utils.loadSuspend
import fr.nicopico.hugo.utils.then
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.android.synthetic.main.form_baby.*
import kotlinx.coroutines.experimental.Deferred

fun Fragment.addBabyDialog() = AddBabyDialogFragment.show(fragmentManager!!)
fun Fragment.editBabyDialog(baby: Baby) = EditBabyDialogFragment.show(fragmentManager!!, baby)

open class AddBabyDialogFragment : FormDialogFragment(), StateHelper {

    companion object {
        fun show(fm: FragmentManager) = AddBabyDialogFragment()
                .show(fm, AddBabyDialogFragment.toString())
    }

    override val dialogTitleId: Int = R.string.baby
    override val formLayoutId: Int = R.layout.form_baby

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtName.editorAction { onSubmit(it) }
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

        fun show(fm: FragmentManager, baby: Baby) = EditBabyDialogFragment()
                .withArguments(ARG_BABY_KEY to baby.key)
                .show(fm, AddBabyDialogFragment.toString())
    }

    private val babyKey by argument<String>(ARG_BABY_KEY)
    private var deferredBaby: Deferred<Baby>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            deferredBaby = loadSuspend {
                babyService.getEntry(babyKey)
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
            edtName.setText(it.name)
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