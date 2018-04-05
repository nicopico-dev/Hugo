package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.services.AnalyticsService
import fr.nicopico.hugo.domain.services.AuthService
import fr.nicopico.hugo.domain.services.BabyService
import fr.nicopico.hugo.domain.services.TimelineService
import redux.api.Store
import redux.applyMiddleware
import redux.combineReducers
import redux.createStore
import redux.logger.Logger
import redux.logger.createLoggerMiddleware

fun createStore(
        authService: AuthService,
        babyService: BabyService,
        timelineService: TimelineService,
        analyticsService: AnalyticsService,
        logger: Logger<AppState>
): Store<AppState> {

    val initialState = AppState(
            user = null,
            timeline = Timeline(),
            screen = Screen.Loading,
            babies = emptyList(),
            selectedBaby = null
    )

    val enhancer = applyMiddleware(
            AuthMiddleware(authService),
            MessageMiddleware(),
            RemoteMiddleware(babyService, timelineService),
            AnalyticsMiddleware(analyticsService),
            createLoggerMiddleware(logger)
    )

    val reducer = combineReducers(
            babyReducer,
            timelineReducer,
            createRemoteReducer(initialState),
            navigationReducer,
            goBackReducer,
            messageReducer
    )

    return createStore(reducer, initialState, enhancer)
}
