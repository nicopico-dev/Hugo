package fr.nicopico.hugo.android

import android.app.Application
import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.android.services.FirebaseAnalyticsService
import fr.nicopico.hugo.android.services.FirebaseAuthService
import fr.nicopico.hugo.android.services.FirebaseBabyService
import fr.nicopico.hugo.android.services.FirebaseTimelineService
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.redux.createStore
import fr.nicopico.hugo.domain.services.AnalyticsService
import fr.nicopico.hugo.domain.services.AuthService
import fr.nicopico.hugo.domain.services.BabyService
import fr.nicopico.hugo.domain.services.DisabledAnalyticsService
import fr.nicopico.hugo.domain.services.TimelineService
import redux.api.Store
import redux.logger.Logger

class App : Application() {

    val store: Store<AppState> by lazy {
        createStore(
                authService = authService,
                babyService = babyService,
                timelineService = timelineService,
                analyticsService = analyticsService,
                logger = reduxLogger
        )
    }

    // FIXME These services should be private
    val authService: AuthService by lazy { FirebaseAuthService() }
    val babyService: BabyService by lazy { FirebaseBabyService() }
    val timelineService: TimelineService by lazy { FirebaseTimelineService() }
    private val analyticsService: AnalyticsService by lazy {
        if (BuildConfig.DEBUG) {
            DisabledAnalyticsService()
        } else {
            FirebaseAnalyticsService(this)
        }
    }

    private val reduxLogger = object : Logger<AppState> {
        private val logger = HugoLogger("REDUX")
        override fun log(entry: Logger.Entry<AppState>) {
            logger.debug { "${entry.action} -> ${entry.newState}" }
        }
    }
}

