package fr.nicopico.hugo.ui.shared

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import fr.nicopico.hugo.R
import fr.nicopico.hugo.utils.getDateFormat
import fr.nicopico.hugo.utils.getTimeFormat
import fr.nicopico.hugo.utils.withHourMinute
import fr.nicopico.hugo.utils.withYearMonthDay
import kotlinx.android.synthetic.main.dialog_time_date_picker.*
import java.util.*

class TimeAndDatePickerDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_DATE = "ARG_DATE"
        private const val RESULT_DATE_TIME = "RESULT_DATE_TIME"
        private const val EPOCH_YEAR = 1900

        fun show(date: Date, targetFragment: Fragment, requestCode: Int = 0) {
            val dialogFragment = TimeAndDatePickerDialogFragment()
                    .withArguments(ARG_DATE to date.time)
            dialogFragment.setTargetFragment(targetFragment, requestCode)
            dialogFragment.show(targetFragment.fragmentManager, "TIME_AND_DATE")
        }

        fun extractResult(data: Intent): Date {
            return Date(data.getLongExtra(RESULT_DATE_TIME, 0))
        }
    }

    private lateinit var date: Date
    private val dateFormat by lazy { getDateFormat(context!!) }
    private val timeFormat by lazy { getTimeFormat(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = Date(arguments!!.getLong(ARG_DATE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_time_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timePicker.apply {
            setTime(date)
            setIs24HourView(DateFormat.is24HourFormat(context))
            setOnTimeChangedListener { _, hourOfDay, minute ->
                date = date.withHourMinute(hourOfDay, minute)
            }
        }

        datePicker.init(date) { _, year, monthOfYear, dayOfMonth ->
            date = date.withYearMonthDay(year, monthOfYear, dayOfMonth)
        }

        txtDateOrTime.apply {
            text = dateFormat.format(date)
            click { toggleDateOrTime() }
        }
        btnCancel.click { dismiss() }
        btnSubmit.click {
            val data = Intent().putExtra(RESULT_DATE_TIME, date.time)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
            dismiss()
        }
    }

    private fun toggleDateOrTime() {
        // TODO Animate
        if (timePicker.visible) {
            timePicker.hide()
            datePicker.show()
            txtDateOrTime.text = timeFormat.format(date)
        } else {
            timePicker.show()
            datePicker.hide()
            txtDateOrTime.text = dateFormat.format(date)
        }
    }

    @Suppress("DEPRECATION")
    private fun TimePicker.setTime(date: Date) {
        currentHour = date.hours
        currentMinute = date.minutes
    }

    @Suppress("DEPRECATION")
    private fun DatePicker.init(date: Date, onDateChangedListener: (DatePicker, Int, Int, Int) -> Unit) {
        init(date.year + EPOCH_YEAR, date.month, date.date, onDateChangedListener)
    }
}