package fr.nicopico.hugo.android.ui

import android.app.Activity
import android.support.v4.app.Fragment
import fr.nicopico.hugo.android.App
import fr.nicopico.hugo.android.utils.application
import fr.nicopico.hugo.domain.model.AppState
import redux.api.Store

interface ReduxViewMixin {

    val appStore: Store<AppState>

    fun Activity.getAppStore() = (application as App).store
    fun Fragment.getAppStore() = application.store

    fun dispatch(action: Any) {
        appStore.dispatch(action)
    }

    fun <R> subscribe(extractor: (AppState) -> R, subscriber: (R) -> Unit): Store.Subscription {
        var previous: R? = extractor(appStore.state)
        previous?.let { subscriber.invoke(it) }
        return appStore.subscribe {
            val last = extractor(appStore.state)
            if (last != previous) {
                previous = last
                subscriber.invoke(last)
            }
        }
    }

    fun subscribe(subscriber: (AppState) -> Unit): Store.Subscription = subscribe({ it }, subscriber)
}