package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.StateProvider
import fr.nicopico.hugo.android.utils.toast
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.FoodTypes
import fr.nicopico.hugo.domain.redux.UPDATE_BABY

class FoodSettingsDialogFragment : DialogFragment(), StateProvider, ReduxDispatcher {

    companion object {
        fun show(fm: FragmentManager) {
            FoodSettingsDialogFragment().show(fm, "FoodChoice")
        }
    }

    //region Redux
    override val state: AppState
        get() = _state

    override fun dispatch(action: Any) = _dispatch(action)
    //endregion

    private val baby: Baby by lazy { state.selectedBaby!! }

    private val checkedFoodTypes by lazy {
        val disabledFoodTypes = baby.disabledFoodTypes
        FoodTypes.allTypes
                .map { it !in disabledFoodTypes }
                .toBooleanArray()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val foodTypeLabels = FoodTypes.allTypes
                .map { it.getLabel(context) }
                .toTypedArray()

        return AlertDialog.Builder(context)
                .setTitle(R.string.care_food_settings_dialog_title)
                .setMultiChoiceItems(foodTypeLabels, checkedFoodTypes, listener)
                .setNeutralButton(android.R.string.cancel, listener)
                .setPositiveButton(android.R.string.ok, listener)
                .create()
    }

    private val listener = object : DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {

        private val buffer by lazy { checkedFoodTypes.copyOf() }

        override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
            buffer[which] = isChecked
        }

        override fun onClick(dialog: DialogInterface?, which: Int) {
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> updateBabyFoodTypes()
                else -> dismiss()
            }
        }

        private fun updateBabyFoodTypes() {
            if (buffer.all { !it }) {
                toast(R.string.care_food_settings_invalid_selection)
                return
            }

            val disabledFoodTypes = FoodTypes.allTypes
                    .filterIndexed { index, _ -> !buffer[index] }
                    .toSet()
            dispatch(UPDATE_BABY(baby.copy(disabledFoodTypes = disabledFoodTypes)))
            dismiss()
        }
    }
}