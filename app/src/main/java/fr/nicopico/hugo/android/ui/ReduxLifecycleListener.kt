package fr.nicopico.hugo.android.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.ReduxView
import fr.nicopico.hugo.android.StateProvider
import fr.nicopico.hugo.android.debug
import fr.nicopico.hugo.domain.model.AppState
import redux.api.Store

typealias StateChangeDetector = (AppState, AppState) -> Boolean

class ReduxLifecycleListener private constructor(
        private val lifecycleOwner: LifecycleOwner,
        private val reduxView: ReduxView,
        private val reduxDispatcher: ReduxDispatcher,
        private val stateProvider: StateProvider,
        private val startAction: Any? = null,
        private val stopAction: Any? = null
) : LifecycleObserver, HugoLogger {

    companion object {
        fun <T> create(observer: T, startAction: Any? = null, stopAction: Any? = null): ReduxLifecycleListener
                where T : LifecycleOwner,
                      T : StateProvider,
                      T : ReduxView,
                      T : ReduxDispatcher {
            return ReduxLifecycleListener(observer, observer, observer, observer, startAction, stopAction)
        }

        fun <T> create(observer: T, reduxView: ReduxView, startAction: Any? = null, stopAction: Any? = null): ReduxLifecycleListener
                where T : LifecycleOwner,
                      T : StateProvider,
                      T : ReduxDispatcher {
            return ReduxLifecycleListener(observer, reduxView, observer, observer, startAction, stopAction)
        }
    }

    private var subscription: Store.Subscription? = null
    private var latestState: AppState? = null
    private var changeDetector: StateChangeDetector = { _, _ -> true }

    fun restrictOn(keyExtractor: (AppState) -> Any?): ReduxLifecycleListener {
        return this.apply {
            this.changeDetector = { s1, s2 ->
                keyExtractor(s1) != keyExtractor(s2)
            }
        }
    }

    fun observe() {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        debug { "Initial state: ${stateProvider.state}" }
        reduxView.render(stateProvider.state)
        subscription = reduxView.subscribe {
            val changed = latestState
                    ?.let { changeDetector.invoke(it, stateProvider.state) }
                    ?: true
            if (changed) {
                reduxView.render(stateProvider.state)
                latestState = stateProvider.state
            }
        }
        startAction?.let { reduxDispatcher.dispatch(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        subscription?.unsubscribe()
        subscription = null
        stopAction?.let { reduxDispatcher.dispatch(it) }
    }
}