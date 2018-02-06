package fr.nicopico.hugo.redux

import redux.api.Store

fun <S, R> Store<S>.distinctSubscribe(extractor: (S) -> R, subscriber: (R) -> Unit): Store.Subscription {
    var previous: R? = extractor(state)
    return subscribe {
        val last = extractor(state)
        if (last === previous) {
            previous = last
            subscriber.invoke(last)
        }
    }
}

fun <S> Store<S>.distinctSubscribe(subscriber: (S) -> Unit) = distinctSubscribe({ it }, subscriber)