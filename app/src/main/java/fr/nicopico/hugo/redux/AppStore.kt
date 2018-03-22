@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Screen
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.authService
import fr.nicopico.hugo.service.babyService
import fr.nicopico.hugo.service.timelineService
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore
import redux.logger.Logger
import redux.logger.createLoggerMiddleware

val INITIAL_STATE = AppState(
        user = null,
        timeline = Timeline(),
        screen = Screen.Loading,
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
        AuthMiddleware(authService),
        MessageMiddleware(),
        RemoteMiddleware(babyService, timelineService),
        createLoggerMiddleware(reduxLogger)
)
private val reducer = combineReducers(babyReducer, timelineReducer, remoteReducer, navigationReducer)

val appStore = createStore(reducer, INITIAL_STATE, enhancer)