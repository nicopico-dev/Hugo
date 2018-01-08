package fr.nicopico.hugo.ui.timeline

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.model.*
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import fr.nicopico.hugo.domain.redux.appStore
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.utils.round
import kotlinx.android.synthetic.main.dialog_add_change.*
import java.util.*

class AddChangeDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val REQUEST_CODE_TIME_DATE = 1

        fun create() = AddChangeDialogFragment()
    }

    private var entryTime: Date = Date().round()

    private val dateFormat by lazy { fr.nicopico.hugo.utils.getDateFormat(context!!) }
    private val timeFormat by lazy { fr.nicopico.hugo.utils.getTimeFormat(context!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.dialog_add_change, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtDateOrTime.apply {
            text = getString(
                    R.string.date_time_format,
                    dateFormat.format(entryTime),
                    timeFormat.format(entryTime)
            )
            click {
                TimeAndDatePickerDialogFragment.show(entryTime, this@AddChangeDialogFragment, REQUEST_CODE_TIME_DATE)
            }
        }

        btnSubmit.click {
            val time = entryTime
            val cares = mutableListOf<Care>()
            if (chkPee.isChecked) cares.add(Pee)
            if (chkPoo.isChecked) cares.add(Poo)

            val entry = Timeline.Entry(CareType.CHANGE, time, cares)
            appStore.dispatch(ADD_ENTRY(entry))

            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_TIME_DATE -> {
                entryTime = TimeAndDatePickerDialogFragment.extractResult(data!!)
                txtDateOrTime.text = getString(
                        R.string.date_time_format,
                        dateFormat.format(entryTime),
                        timeFormat.format(entryTime)
                )
            }
        }
    }
}