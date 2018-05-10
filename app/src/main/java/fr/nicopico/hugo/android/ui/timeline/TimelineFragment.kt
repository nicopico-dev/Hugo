package fr.nicopico.hugo.android.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.debug
import fr.nicopico.hugo.android.ui.BaseFragment
import fr.nicopico.hugo.android.ui.ReduxLifecycleListener
import fr.nicopico.hugo.android.ui.timeline.entry.addChangeDialog
import fr.nicopico.hugo.android.ui.timeline.entry.addFoodDialog
import fr.nicopico.hugo.android.ui.timeline.entry.addHealthAndHygieneDialog
import fr.nicopico.hugo.android.ui.timeline.entry.editChangeDialog
import fr.nicopico.hugo.android.ui.timeline.entry.editFoodDialog
import fr.nicopico.hugo.android.ui.timeline.entry.editHealthAndHygieneDialog
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.toggle
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.CareType.CHANGE
import fr.nicopico.hugo.domain.model.CareType.FOOD
import fr.nicopico.hugo.domain.model.CareType.HEALTH_HYGIENE
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.FETCH_TIMELINE
import fr.nicopico.hugo.domain.redux.STOP_FETCHING_TIMELINE
import fr.nicopico.hugo.domain.redux.UNSELECT_BABY
import fr.nicopico.hugo.domain.services.trace
import kotlinx.android.synthetic.main.fragment_timeline.*
import java.util.*

class TimelineFragment : BaseFragment(), HugoLogger {

    private val timelineAdapter by lazy {
        TimelineAdapter(context!!)
    }
    private var firstSectionDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        ReduxLifecycleListener
                .create(this, FETCH_TIMELINE, STOP_FETCHING_TIMELINE)
                .observe()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvTimeline.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timelineAdapter
        }

        fabAdd.click { toggleFabMenu() }
        txtFabAddChange.click { addChangeDialog(); toggleFabMenu() }
        txtFabAddFood.click { addFoodDialog(); toggleFabMenu() }
        txtFabAddHealthHygiene.click { addHealthAndHygieneDialog(); toggleFabMenu() }

        timelineAdapter.itemLongClick { entry: Timeline.Entry ->
            when (entry.type) {
                CHANGE -> editChangeDialog(entry)
                FOOD -> editFoodDialog(entry)
                HEALTH_HYGIENE -> editHealthAndHygieneDialog(entry)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.timeline_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_select_baby) {
            dispatch(UNSELECT_BABY)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun render(state: AppState) {
        val sections = state.timeline.sections
        trace("renderTimeline") {
            incrementCounter("sections", sections.size.toLong())
            timelineAdapter.data = sections
        }

        val newerFirstEntry = firstSectionDate?.let {
            sections.firstOrNull()?.time?.after(firstSectionDate)
        } ?: true

        if (newerFirstEntry) {
            debug("New item added, scroll to top")
            with(rcvTimeline) {
                post { scrollToPosition(0) }
                postInvalidate()
            }
        }

        firstSectionDate = sections.firstOrNull()?.time
    }

    private fun toggleFabMenu() {
        // TODO Animate
        fabAddMenu.toggle()
    }
}