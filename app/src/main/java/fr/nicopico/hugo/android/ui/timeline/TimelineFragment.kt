package fr.nicopico.hugo.android.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.BaseFragment
import fr.nicopico.hugo.android.ui.ReduxLifecycleListener
import fr.nicopico.hugo.android.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.dimensionForOffset
import fr.nicopico.hugo.android.utils.toggle
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.CareType.*
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.FETCH_TIMELINE
import fr.nicopico.hugo.domain.redux.STOP_FETCHING_TIMELINE
import fr.nicopico.hugo.domain.redux.UNSELECT_BABY
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : BaseFragment() {

    private val timelineAdapter by lazy {
        TimelineAdapter(context!!)
    }
    private var previousTimelineCount = 0

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

    override fun render(state: AppState) {
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