package fr.nicopico.hugo.android.ui

import android.support.v4.app.Fragment
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.ReduxView
import fr.nicopico.hugo.android.StateProvider
import fr.nicopico.hugo.domain.model.AppState

abstract class BaseFragment : Fragment(),
        ReduxView, ReduxDispatcher, StateProvider {

    override fun dispatch(action: Any) = _dispatch(action)
    override fun subscribe(subscriber: () -> Unit) = _subscribe(subscriber)
    override val state: AppState
        get() = _state
}