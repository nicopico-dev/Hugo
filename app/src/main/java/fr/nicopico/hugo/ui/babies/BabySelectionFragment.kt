package fr.nicopico.hugo.ui.babies

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.redux.*
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.dimensionForOffset
import kotlinx.android.synthetic.main.fragment_baby_selection.*
import redux.api.Store

class BabySelectionFragment : BaseFragment() {

    companion object {
        const val SCREEN = "SCREEN_BABY_SELECTION"
    }

    override val screen: String = SCREEN

    private var subscription: Store.Subscription? = null
    private val babyAdapter by lazy {
        BabyAdapter(context!!, appStore.state.babies)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_baby_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvBabies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = babyAdapter

            // Decorations
            addItemDecoration(SpaceItemDecoration(layoutManager, dimensionForOffset(R.dimen.space_medium)))
        }

        fabAdd.click {
            // TODO Show baby form
            appStore.dispatch(ADD_BABY(Baby("Hugo")))
        }

        babyAdapter.listener = { baby -> appStore.dispatch(SELECT_BABY(baby)) }

        // TODO Use LiveData + LifeCycleObserver for interaction with the appStore?
        refresh()
        subscription = appStore.subscribe {
            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        appStore.dispatch(FETCH_BABIES)
    }

    override fun onPause() {
        super.onPause()
        appStore.dispatch(STOP_FETCHING_BABIES)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.unsubscribe()
    }

    private fun refresh() {
        val babies = appStore.state.babies
        babyAdapter.data = babies
    }
}
