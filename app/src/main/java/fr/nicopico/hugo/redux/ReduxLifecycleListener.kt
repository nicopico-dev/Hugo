package fr.nicopico.hugo.redux

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import fr.nicopico.hugo.model.AppState
import redux.api.Store

class ReduxLifecycleListener(
        private val observer: (AppState) -> Unit,
        private val startAction: Any? = null,
        private val stopAction: Any? = null
) : LifecycleObserver {

    private var subscription: Store.Subscription? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        observer(appStore.state)
        subscription = appStore.subscribe { observer(appStore.state) }
        startAction?.let { appStore.dispatch(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        subscription?.unsubscribe()
        subscription = null
        stopAction?.let { appStore.dispatch(it) }
    }
}