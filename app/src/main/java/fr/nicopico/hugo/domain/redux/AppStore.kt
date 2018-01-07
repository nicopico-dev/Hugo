package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.*
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.verbose
import redux.api.Reducer
import redux.createStore
import java.util.*

val appStore by lazy {
    // TODO Restore state
    createStore(reducer, AppState(listOf(
            entry("04/01/2018 14:45",
                    BreastFeeding(Breast.LEFT, 20),
                    BreastFeeding(Breast.RIGHT, 20),
                    BottleFeeding(20)
            ),
            entry("04/01/2018 11:00", BottleFeeding(35)),
            entry("04/01/2018 08:15", UmbilicalCord, Face, Vitamins),
            entry("04/01/2018 07:30", Pee, Poo),
            entry("03/01/2018 11:00",
                    BreastFeeding(Breast.LEFT, 20),
                    BreastFeeding(Breast.RIGHT, 20),
                    BottleFeeding(20)
            ),
            entry("03/01/2018 10:40", Pee),
            entry("03/01/2018 10:20", UmbilicalCord, Face, Vitamins)
    )))
}

private val logger = HugoLogger("reducer")
private val reducer = Reducer<AppState> { state, action ->
    logger.verbose { "Reducer received action $action" }
    state
}

data class ADD_ENTRY(val time: Date, val care: Care)
data class REMOVE_ENTRY(val time: Date, val care: Care)