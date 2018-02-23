package fr.nicopico.hugo.redux

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import fr.nicopico.hugo.model.AppState
import redux.api.Store

class ReduxLifecycleListener(
        private val observer: (AppState) -> Unit,
        private val startAction: Any? = null,
        private val stopAction: Any? = null,
        private val changeDetector: (AppState, AppState) -> Boolean = { _, _ -> true }
) : LifecycleObserver {

    private var subscription: Store.Subscription? = null
    private var latestState: AppState? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        observer(appStore.state)
        subscription = appStore.subscribe {
            val changed = latestState?.let { changeDetector.invoke(it, appStore.state) } ?: true
            if (changed) {
                observer(appStore.state)
                latestState = appStore.state
            }
        }
        startAction?.let { appStore.dispatch(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        subscription?.unsubscribe()
        subscription = null
        stopAction?.let { appStore.dispatch(it) }
    }
}