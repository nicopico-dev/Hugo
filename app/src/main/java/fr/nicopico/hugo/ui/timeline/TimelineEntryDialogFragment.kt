package fr.nicopico.hugo.ui.timeline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.ui.shared.FormDialogFragment
import fr.nicopico.hugo.ui.shared.TimeAndDatePickerDialogFragment
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.utils.round
import java.util.*
import kotlin.properties.Delegates

abstract class TimelineEntryDialogFragment : FormDialogFragment() {

    companion object {
        private const val REQUEST_CODE_TIME_DATE = 1
    }

    private val dateFormat by lazy { fr.nicopico.hugo.utils.getDateFormat(context!!) }
    private val timeFormat by lazy { fr.nicopico.hugo.utils.getTimeFormat(context!!) }
    protected var entryTime: Date by Delegates.observable(Date().round()) { _, _, value ->
        displayDate(value)
    }

    protected abstract val dateOrTimeTextView: TextView
    abstract fun buildEntry(): Timeline.Entry

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateOrTimeTextView.click { timeAndDateDialog() }
        displayDate(entryTime)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_TIME_DATE -> {
                entryTime = TimeAndDatePickerDialogFragment.extractResult(data!!)
            }
        }
    }

    private fun timeAndDateDialog() {
        TimeAndDatePickerDialogFragment.show(entryTime, this@TimelineEntryDialogFragment, REQUEST_CODE_TIME_DATE)
    }

    private fun displayDate(date: Date) {
        dateOrTimeTextView.text = getString(
                R.string.date_time_format,
                dateFormat.format(date),
                timeFormat.format(date)
        )
    }
}

