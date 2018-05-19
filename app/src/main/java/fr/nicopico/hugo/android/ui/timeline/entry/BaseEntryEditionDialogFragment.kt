package fr.nicopico.hugo.android.ui.timeline.entry

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.StateProvider
import fr.nicopico.hugo.android.ui.shared.FormDialogFragment
import fr.nicopico.hugo.android.ui.shared.TimeAndDatePickerDialogFragment
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.confirm
import fr.nicopico.hugo.android.utils.show
import fr.nicopico.hugo.android.utils.toast
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import fr.nicopico.hugo.domain.redux.REMOVE_ENTRY
import fr.nicopico.hugo.domain.utils.getDateFormat
import fr.nicopico.hugo.domain.utils.getTimeFormat
import fr.nicopico.hugo.domain.utils.round
import kotlinx.android.synthetic.main.dialog_form.*
import java.util.*
import kotlin.properties.Delegates

abstract class BaseEntryEditionDialogFragment : FormDialogFragment(), StateProvider {

    companion object {
        private const val REQUEST_CODE_TIME_DATE = 1
    }

    private val dateFormat by lazy { getDateFormat(context!!) }
    private val timeFormat by lazy { getTimeFormat(context!!) }
    private var entryTime: Date by Delegates.observable(Date().round()) { _, _, value ->
        displayDate(value)
    }

    protected abstract val dateOrTimeTextView: TextView

    override val state: AppState
        get() = _state
    private val entry: Timeline.Entry? by lazy { getStateEntry() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateOrTimeTextView.click { showTimeAndDateDialog() }
        displayDate(entryTime)

        entry?.let { entry ->
            entryTime = entry.time
            render(entry)
            imgDelete.apply {
                show()
                click {
                    dispatch(REMOVE_ENTRY(entry))
                    dismiss()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_TIME_DATE -> {
                entryTime = TimeAndDatePickerDialogFragment.extractResult(data!!)
            }
        }
    }

    override fun onSubmit(view: View) {
        buildUpdatedEntry(entry, entryTime).let {
            val action = ADD_ENTRY(it)
            val validityResult = checkEntryValidity(it)
            when (validityResult) {
                is ValidityResult.Valid -> {
                    dispatch(action)
                    dismiss()
                }
                is ValidityResult.ConfirmationNeeded -> {
                    confirm(context!!,
                            validityResult.message,
                            validityResult.confirmButtonLabel,
                            R.string.cancel,
                            {
                                dispatch(action)
                                dismiss()
                            }
                    )
                }
                is ValidityResult.Invalid -> toast(validityResult.message)
            }
        }
    }

    protected abstract fun getStateEntry(): Timeline.Entry?
    protected abstract fun buildUpdatedEntry(entry: Timeline.Entry?, entryTime: Date): Timeline.Entry
    protected abstract fun checkEntryValidity(entry: Timeline.Entry): ValidityResult
    protected abstract fun render(entry: Timeline.Entry)

    private fun showTimeAndDateDialog() {
        TimeAndDatePickerDialogFragment.show(
                entryTime,
                this@BaseEntryEditionDialogFragment,
                REQUEST_CODE_TIME_DATE
        )
    }

    private fun displayDate(date: Date) {
        dateOrTimeTextView.text = getString(
                R.string.date_time_format,
                dateFormat.format(date),
                timeFormat.format(date)
        )
    }

    protected sealed class ValidityResult {
        object Valid : ValidityResult()
        class ConfirmationNeeded(@StringRes val message: Int, @StringRes val confirmButtonLabel: Int) : ValidityResult()
        class Invalid(@StringRes val message: Int) : ValidityResult()
    }
}