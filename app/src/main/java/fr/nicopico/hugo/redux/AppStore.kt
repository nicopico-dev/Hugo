@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.service.TimelineService
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore

private val initialState = AppState(
        user = null,
        timeline = Timeline()
)

private val enhancer = applyMiddleware(
        LoggerMiddleware,
        TimelineMiddleware(AuthService.create(), TimelineService.create())
)
private val reducer = combineReducers(timelineReducer, remoteReducer)

val appStore by lazy {
    createStore(reducer, initialState, enhancer)
}