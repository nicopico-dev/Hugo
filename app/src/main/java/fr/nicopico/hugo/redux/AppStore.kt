@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.service.BabyService
import fr.nicopico.hugo.service.TimelineService
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore
import redux.logger.Logger
import redux.logger.createLoggerMiddleware

private val initialState = AppState(
        user = null,
        timeline = Timeline(),
        babies = emptyList(),
        selectedBaby = null
)

private val reduxLogger = object : Logger<AppState> {
    private val logger = HugoLogger("REDUX")
    override fun log(entry: Logger.Entry<AppState>) {
        logger.debug { "${entry.action} -> ${entry.newState}" }
    }
}

private val enhancer = applyMiddleware(
        createLoggerMiddleware(reduxLogger),
        MessageMiddleware(),
        AuthMiddleware(AuthService.create()),
        RemoteMiddleware(BabyService.create(), TimelineService.create())
)
private val reducer = combineReducers(babyReducer, timelineReducer, remoteReducer)

val appStore = createStore(reducer, initialState, enhancer)