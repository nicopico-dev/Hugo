package fr.nicopico.hugo.ui.babies

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.redux.ADD_BABY
import fr.nicopico.hugo.redux.StateHelper
import fr.nicopico.hugo.ui.shared.FormDialogFragment

class AddBabyDialogFragment : FormDialogFragment(), StateHelper {

    companion object {
        fun create() = AddBabyDialogFragment()
    }

    override val dialogTitleId: Int = R.string.baby
    override val formLayout: View?
        get() = edtName

    private val edtName by lazy {
        EditText(context).apply {
            hint = getString(R.string.baby_name)
            imeOptions += EditorInfo.IME_ACTION_DONE
            setSingleLine(true)

            setOnEditorActionListener { v, _, _ ->
                onSubmit(v)
                true
            }
        }
    }

    override fun onSubmit(view: View) {
        val baby = Baby(edtName.text.toString())
        dispatch(ADD_BABY(baby))
        dismiss()
    }
}