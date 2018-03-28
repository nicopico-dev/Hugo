package fr.nicopico.hugo.android.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.debug
import fr.nicopico.hugo.domain.model.AppState
import redux.api.Store

typealias StateChangeDetector = (AppState, AppState) -> Boolean

class ReduxLifecycleListener(
        private val appStore: Store<AppState>,
        private val observer: (AppState) -> Unit,
        private val startAction: Any? = null,
        private val stopAction: Any? = null
) : LifecycleObserver, HugoLogger {

    private var subscription: Store.Subscription? = null
    private var latestState: AppState? = null
    private var changeDetector: StateChangeDetector = { _, _ -> true }

    fun restrictOn(changeDetector: StateChangeDetector): ReduxLifecycleListener {
        return this.apply {
            this.changeDetector = changeDetector
        }
    }

    fun subscribe(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        debug { "Initial state: ${appStore.state}"}
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