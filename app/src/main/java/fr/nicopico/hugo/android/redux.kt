@file:Suppress("FunctionName", "PropertyName")

package fr.nicopico.hugo.android

import android.app.Activity
import android.support.v4.app.Fragment
import fr.nicopico.hugo.domain.model.AppState
import redux.api.Store

private val Activity.store: Store<AppState>
    get() = (application as App).store

private val Fragment.store: Store<AppState>
    get() = (activity!!.application as App).store

interface ReduxView {
    fun subscribe(subscriber: () -> Unit): Store.Subscription
    fun render(state: AppState)

    fun Activity._subscribe(subscriber: () -> Unit): Store.Subscription = store.subscribe(subscriber)

    fun Fragment._subscribe(subscriber: () -> Unit): Store.Subscription = store.subscribe(subscriber)
}

interface StateProvider {
    val state: AppState

    val Activity._state: AppState
        get() = store.state

    val Fragment._state: AppState
        get() = store.state
}

interface ReduxDispatcher {

    fun dispatch(action: Any)

    fun Activity._dispatch(action: Any) {
        store.dispatch(action)
    }

    fun Fragment._dispatch(action: Any) {
        store.dispatch(action)
    }
}