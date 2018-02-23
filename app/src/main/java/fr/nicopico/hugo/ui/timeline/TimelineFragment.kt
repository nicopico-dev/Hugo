package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.CareType
import fr.nicopico.hugo.redux.*
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.*
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : BaseFragment(), ReduxView {

    companion object {
        const val SCREEN = "SCREEN_TIMELINE"
    }

    override val screen: String = SCREEN

    private val timelineAdapter by lazy {
        TimelineAdapter(context!!, appStore.state.timeline)
    }
    private var previousTimelineCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ReduxLifecycleListener(::updateScreen, FETCH_TIMELINE, STOP_FETCHING_TIMELINE))
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvTimeline.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            adapter = timelineAdapter

            // Decorations
            addItemDecoration(SpaceItemDecoration(layoutManager, dimensionForOffset(R.dimen.space_medium)))
            addItemDecoration(DateSectionDecoration(context, timelineAdapter))
        }

        fabAdd.click { toggleFabMenu() }
        fabAddChange.click(onAddEntryFactory(CareType.CHANGE))
        fabAddFood.click(onAddEntryFactory(CareType.FOOD))
        fabAddHealthHygiene.click(onAddEntryFactory(CareType.HEALTH_HYGIENE))
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
        timelineAdapter.data = state.timeline
        if (previousTimelineCount < timelineAdapter.itemCount) {
            previousTimelineCount = timelineAdapter.itemCount
            rcvTimeline.scrollToPosition(previousTimelineCount - 1)
        }
    }

    private fun toggleFabMenu() {
        // TODO Animate
        if (groupFabs.visibility == View.VISIBLE) {
            groupFabs.hide()
        } else {
            groupFabs.show()
        }
    }

    private fun onAddEntryFactory(careType: CareType): (View) -> Unit {
        return {
            toggleFabMenu()
            val dialogFragment: DialogFragment = when (careType) {
                CareType.CHANGE -> AddChangeDialogFragment.create()
                CareType.FOOD -> AddFoodDialogFragment.create()
                CareType.HEALTH_HYGIENE -> AddHealthAndHygieneDialogFragment.create()
            }
            dialogFragment.show(fragmentManager, "ADD_ENTRY")
        }
    }
}