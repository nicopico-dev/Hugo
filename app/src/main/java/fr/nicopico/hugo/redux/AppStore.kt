@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.service.BabyService
import fr.nicopico.hugo.service.TimelineService
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore

private val initialState = AppState(
        user = null,
        timeline = Timeline(),
        babies = emptyList(),
        selectedBaby = null
)

private val enhancer = applyMiddleware(
        LoggerMiddleware,
        MessageMiddleware,
        AuthMiddleware(AuthService.create()),
        BabyMiddleware(BabyService.create()),
        TimelineMiddleware(TimelineService.create())
)
private val reducer = combineReducers(babyReducer, timelineReducer, remoteReducer)

val appStore by lazy {
    createStore(reducer, initialState, enhancer)
}