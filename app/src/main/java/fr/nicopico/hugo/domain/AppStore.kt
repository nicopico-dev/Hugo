@file:Suppress("ClassName")

package fr.nicopico.hugo.domain

import fr.nicopico.hugo.domain.model.*
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.verbose
import redux.api.Reducer
import redux.createStore

val appStore by lazy {
    // TODO Restore state
    createStore(reducer, AppState(listOf(
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
    )))
}

private val logger = HugoLogger("reducer")
private val reducer = Reducer<AppState> { state, action ->
    logger.verbose { "Action received $action" }
    when (action) {
        is ADD_ENTRY -> {
            state.copy(timeline = state.timeline + action.entry)
        }
        is UPDATE_ENTRY -> {
            val index = state.timeline.indexOf(action.oldEntry)
            val updatedTimeline = state.timeline.toMutableList().apply {
                removeAt(index)
                add(index, action.newEntry)
            }
            state.copy(timeline = updatedTimeline)
        }
        is REMOVE_ENTRY -> {
            state.copy(timeline = state.timeline - action.entry)
        }
        else -> state
    }
}

data class ADD_ENTRY(val entry: Timeline.Entry)
data class UPDATE_ENTRY(val oldEntry: Timeline.Entry, val newEntry: Timeline.Entry)
data class REMOVE_ENTRY(val entry: Timeline.Entry)