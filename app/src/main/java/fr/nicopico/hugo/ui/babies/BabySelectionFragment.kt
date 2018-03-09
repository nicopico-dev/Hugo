package fr.nicopico.hugo.ui.babies

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.redux.FETCH_BABIES
import fr.nicopico.hugo.redux.ReduxLifecycleListener
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.SELECT_BABY
import fr.nicopico.hugo.redux.STOP_FETCHING_BABIES
import fr.nicopico.hugo.redux.appStore
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.dimensionForOffset
import kotlinx.android.synthetic.main.fragment_baby_selection.*

class BabySelectionFragment : BaseFragment(), ReduxView {

    private val babyAdapter by lazy {
        BabyAdapter(context!!, appStore.state.babies)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReduxLifecycleListener(::updateScreen, FETCH_BABIES, STOP_FETCHING_BABIES)
                .subscribe(this)
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

        // Interactions
        fabAdd.click { addBabyDialog() }
        babyAdapter.clickListener = { baby -> dispatch(SELECT_BABY(baby)) }
        babyAdapter.longClickListener = { baby -> editBabyDialog(baby) }
    }

    private fun updateScreen(state: AppState) {
        babyAdapter.data = state.babies
    }
}
