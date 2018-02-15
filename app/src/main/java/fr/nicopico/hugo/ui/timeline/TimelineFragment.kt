package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.CareType
import fr.nicopico.hugo.redux.FETCH_TIMELINE
import fr.nicopico.hugo.redux.STOP_FETCHING_TIMELINE
import fr.nicopico.hugo.redux.appStore
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import redux.api.Store

class TimelineFragment : BaseFragment() {

    companion object {
        const val SCREEN = "SCREEN_TIMELINE"
    }

    override val screen: String = SCREEN

    private var subscription: Store.Subscription? = null
    private val timelineAdapter by lazy {
        TimelineAdapter(context!!, appStore.state.timeline)
    }
    private var previousTimelineCount = 0

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

        // TODO Use LiveData + LifeCycleObserver for interaction with the appStore?
        refresh()
        subscription = appStore.subscribe {
            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        appStore.dispatch(FETCH_TIMELINE)
    }

    override fun onPause() {
        super.onPause()
        appStore.dispatch(STOP_FETCHING_TIMELINE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.unsubscribe()
    }

    private fun refresh() {
        val timeline = appStore.state.timeline
        timelineAdapter.data = timeline
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
            val dialogFragment = when (careType) {
                CareType.CHANGE -> AddChangeDialogFragment.create()
                CareType.FOOD -> AddFoodDialogFragment.create()
                CareType.HEALTH_HYGIENE -> AddHealthAndHygieneDialogFragment.create()
            }
            dialogFragment.show(fragmentManager, "ADD_ENTRY")
        }
    }
}