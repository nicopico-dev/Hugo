@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.*
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore

data class AppState(
        val timeline: List<Timeline.Entry>
)

private val initialState = AppState(listOf(
        entry("03/01/2018 10:20", UmbilicalCord, Face, Vitamins),
        entry("03/01/2018 10:40", Pee),
        entry("03/01/2018 11:00",
                BreastFeeding(Breast.LEFT, 20),
                BreastFeeding(Breast.RIGHT, 20),
                BottleFeeding(20)
        ),
        entry("04/01/2018 07:30", Pee, Poo),
        entry("04/01/2018 08:15", UmbilicalCord, Face, Vitamins),
        entry("04/01/2018 11:00", BottleFeeding(35)),
        entry("04/01/2018 14:45",
                BreastFeeding(Breast.LEFT, 20),
                BreastFeeding(Breast.RIGHT, 20),
                BottleFeeding(20)
        )
))

private val enhancer = applyMiddleware(LoggerMiddleware)
private val reducer = combineReducers(timelineReducer)

val appStore by lazy {
    createStore(reducer, initialState, enhancer)
}