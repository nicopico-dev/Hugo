package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.redux.appStore
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import redux.api.Store

class TimelineFragment : BaseFragment() {

    companion object {
        const val REQUEST_ADD_CHANGE = 1
    }

    private var subscription: Store.Subscription? = null
    private val timelineAdapter by lazy {
        TimelineAdapter(context!!)
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
        fabAddChange.click { onAddChange() }
        fabAddFood.click { onAddFood() }
        fabAddHealthHygiene.click { onAddHealthAndHygiene() }

        refresh()
        subscription = appStore.subscribe {
            refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.unsubscribe()
    }

    private fun refresh() {
        timelineAdapter.data = appStore.state.timeline
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

    private fun onAddChange() {
        toggleFabMenu()
        val dialogFragment = AddChangeDialogFragment.create()
        dialogFragment.setTargetFragment(this, REQUEST_ADD_CHANGE)
        dialogFragment.show(fragmentManager, "ADD_CHANGE")
    }

    private fun onAddFood() {
        toggleFabMenu()
        toast("TODO Add food")
    }

    private fun onAddHealthAndHygiene() {
        toggleFabMenu()
        toast("TODO Add health & hygiene")
    }
}