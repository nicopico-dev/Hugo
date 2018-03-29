package fr.nicopico.hugo.android.ui.timeline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.App
import fr.nicopico.hugo.android.ui.shared.FormDialogFragment
import fr.nicopico.hugo.android.ui.shared.TimeAndDatePickerDialogFragment
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.services.TimelineService
import fr.nicopico.hugo.domain.utils.getDateFormat
import fr.nicopico.hugo.domain.utils.getTimeFormat
import fr.nicopico.hugo.domain.utils.round
import java.util.*
import kotlin.properties.Delegates

abstract class TimelineEntryDialogFragment : FormDialogFragment() {

    companion object {
        private const val REQUEST_CODE_TIME_DATE = 1
    }

    val timelineService: TimelineService
        get() = (activity!!.application as App).timelineService

    private val dateFormat by lazy { getDateFormat(context!!) }
    private val timeFormat by lazy { getTimeFormat(context!!) }
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

