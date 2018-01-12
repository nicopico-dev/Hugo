package fr.nicopico.hugo.ui.timeline

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.utils.round
import kotlinx.android.synthetic.main.dialog_add_change.*
import java.util.*

abstract class AddTimelineEntryDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val REQUEST_CODE_TIME_DATE = 1
    }

    private var entryTime: Date = Date().round()
    private val dateFormat by lazy { fr.nicopico.hugo.utils.getDateFormat(context!!) }
    private val timeFormat by lazy { fr.nicopico.hugo.utils.getTimeFormat(context!!) }

    protected abstract val dateOrTimeTextView: TextView
    protected fun getEntryTime(): Date = entryTime

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtDateOrTime.apply {
            text = getString(
                    R.string.date_time_format,
                    dateFormat.format(entryTime),
                    timeFormat.format(entryTime)
            )
            click {
                TimeAndDatePickerDialogFragment.show(entryTime, this@AddTimelineEntryDialogFragment, REQUEST_CODE_TIME_DATE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
                REQUEST_CODE_TIME_DATE -> {
                entryTime = TimeAndDatePickerDialogFragment.extractResult(data!!)
                    dateOrTimeTextView.text = getString(
                        R.string.date_time_format,
                        dateFormat.format(entryTime),
                        timeFormat.format(entryTime)
                )
            }
        }
    }
}