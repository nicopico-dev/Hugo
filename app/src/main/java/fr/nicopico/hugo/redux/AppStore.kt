@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.service.RemoteService
import fr.nicopico.hugo.service.User
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore

data class AppState(
        val user: User?,
        val loading: Boolean = false,
        val timeline: Timeline
)

private val initialState = AppState(
        user = null,
        timeline = Timeline()
)

private val enhancer = applyMiddleware(
        LoggerMiddleware,
        RemoteMiddleware(AuthService.create(), RemoteService.create())
)
private val reducer = combineReducers(timelineReducer, remoteReducer)

val appStore by lazy {
    createStore(reducer, initialState, enhancer)
}