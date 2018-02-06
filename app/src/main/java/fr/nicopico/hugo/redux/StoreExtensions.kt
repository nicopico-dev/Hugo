package fr.nicopico.hugo.redux

import redux.api.Store

fun <S, R> Store<S>.distinctSubscribe(extractor: (S) -> R, subscriber: (R) -> Unit) {
    var previous: R? = extractor(state)
    subscribe {
        val last = extractor(state)
        if (last === previous) {
            previous = last
            subscriber.invoke(last)
        }
    }
}

fun <S> Store<S>.distinctSubscribe(subscriber: (S) -> Unit) = distinctSubscribe({ it }, subscriber)