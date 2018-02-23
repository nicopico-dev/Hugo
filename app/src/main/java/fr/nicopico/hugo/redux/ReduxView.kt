package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import redux.api.Store

interface ReduxView {

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