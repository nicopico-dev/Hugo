package fr.nicopico.hugo.ui.babies

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.redux.*
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.dimensionForOffset
import kotlinx.android.synthetic.main.fragment_baby_selection.*

class BabySelectionFragment : BaseFragment() {

    companion object {
        const val SCREEN = "SCREEN_BABY_SELECTION"
    }

    override val screen: String = SCREEN

    private val babyAdapter by lazy {
        BabyAdapter(context!!, appStore.state.babies)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ReduxLifecycleListener(::updateScreen, FETCH_BABIES, STOP_FETCHING_BABIES))
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
    }
    private fun updateScreen(state: AppState) {
        babyAdapter.data = state.babies
    }
}
