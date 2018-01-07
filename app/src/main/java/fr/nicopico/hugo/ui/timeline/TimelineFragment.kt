package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.redux.appStore
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.dimensionForOffset
import fr.nicopico.hugo.ui.shared.toast
import kotlinx.android.synthetic.main.fragment_timeline.*
import redux.api.Store

class TimelineFragment : BaseFragment() {

    private var subscription: Store.Subscription? = null
    private val timelineAdapter by lazy {
        TimelineAdapter(context!!)
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

        fabAdd.click { toast("TODO Add") }

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
    }
}

