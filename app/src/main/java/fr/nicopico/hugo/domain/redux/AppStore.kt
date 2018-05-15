package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.ScreenStack
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.services.AnalyticsService
import fr.nicopico.hugo.domain.services.AuthService
import fr.nicopico.hugo.domain.services.BabyService
import fr.nicopico.hugo.domain.services.PersistenceService
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
        persistenceService: PersistenceService,
        logger: Logger<AppState>
): Store<AppState> {

    val initialState = AppState(
            user = null,
            timeline = Timeline(),
            screenStack = ScreenStack(Screen.Loading),
            babies = emptyList(),
            selectedBaby = null
    )

    val enhancer = applyMiddleware(
            AuthMiddleware(authService),
            MessageMiddleware(),
            RemoteMiddleware(babyService, timelineService),
            PersistenceMiddleware(persistenceService),
            AnalyticsMiddleware(analyticsService),
            DebounceTimelineMiddleware(100),
            createLoggerMiddleware(logger)
    )

    val reducer = combineReducers(
            babyReducer,
            timelineReducer,
            createRemoteReducer(initialState),
            navigationReducer,
            messageReducer
    )

    return createStore(reducer, initialState, enhancer)
}
