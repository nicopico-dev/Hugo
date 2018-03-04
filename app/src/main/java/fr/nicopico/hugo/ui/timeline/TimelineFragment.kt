package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.CareType.*
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.redux.*
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.dimensionForOffset
import fr.nicopico.hugo.ui.shared.toggle
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : BaseFragment(), ReduxView {

    private val timelineAdapter by lazy {
        TimelineAdapter(context!!, appStore.state.timeline.entries)
    }
    private var previousTimelineCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        ReduxLifecycleListener(::updateScreen, FETCH_TIMELINE, STOP_FETCHING_TIMELINE)
                .subscribe(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvTimeline.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timelineAdapter

            // Decorations
            addItemDecoration(SpaceItemDecoration(layoutManager, dimensionForOffset(R.dimen.space_medium)))
            addItemDecoration(DateSectionDecoration(context, timelineAdapter))
        }

        fabAdd.click { toggleFabMenu() }
        txtFabAddChange.click { addChangeDialog(); toggleFabMenu() }
        txtFabAddFood.click { addFoodDialog(); toggleFabMenu() }
        txtFabAddHealthHygiene.click { addHealthAndHygieneDialog(); toggleFabMenu() }

        timelineAdapter.longClickListener = { entry: Timeline.Entry ->
            when(entry.type) {
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

    private fun updateScreen(state: AppState) {
        timelineAdapter.data = state.timeline.entries
        if (previousTimelineCount > timelineAdapter.itemCount) {
            previousTimelineCount = timelineAdapter.itemCount
            rcvTimeline.scrollToPosition(0)
        }
    }

    private fun toggleFabMenu() {
        // TODO Animate
        fabAddMenu.toggle()
    }
}