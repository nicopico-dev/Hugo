package fr.nicopico.hugo.redux

import redux.api.Store

fun <S, R> Store<S>.subscribeDistinct(extractor: (S) -> R, subscriber: (R) -> Unit): Store.Subscription {
    var previous: R? = extractor(state)
    return subscribe {
        val last = extractor(state)
        if (last != previous) {
            previous = last
            subscriber.invoke(last)
        }
    }
}

fun <S> Store<S>.subscribeDistinct(subscriber: (S) -> Unit) = subscribeDistinct({ it }, subscriber)