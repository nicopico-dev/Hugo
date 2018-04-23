package fr.nicopico.hugo.android

import android.app.Activity
import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.android.services.FirebaseAnalyticsService
import fr.nicopico.hugo.android.services.FirebaseAuthService
import fr.nicopico.hugo.android.services.FirebaseBabyService
import fr.nicopico.hugo.android.services.FirebasePerformanceService
import fr.nicopico.hugo.android.services.FirebaseTimelineService
import fr.nicopico.hugo.android.services.SharedPrefsPersistenceService
import fr.nicopico.hugo.android.utils.ActivityProvider
import fr.nicopico.hugo.android.utils.SimpleActivityLifecycleCallbacks
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.redux.REMOTE_ERROR
import fr.nicopico.hugo.domain.redux.createStore
import fr.nicopico.hugo.domain.services.AnalyticsService
import fr.nicopico.hugo.domain.services.AuthService
import fr.nicopico.hugo.domain.services.BabyService
import fr.nicopico.hugo.domain.services.DisabledAnalyticsService
import fr.nicopico.hugo.domain.services.PersistenceService
import fr.nicopico.hugo.domain.services.TimelineService
import redux.api.Store
import redux.logger.Logger
import java.lang.ref.WeakReference

class App : Application() {

    val store: Store<AppState> by lazy {
        createStore(
                authService = authService,
                babyService = babyService,
                timelineService = timelineService,
                analyticsService = analyticsService,
                persistenceService = persistenceService,
                logger = reduxLogger
        )
    }

    // FIXME These services should be private
    val authService: AuthService by lazy { FirebaseAuthService() }
    val babyService: BabyService by lazy { FirebaseBabyService() }
    val timelineService: TimelineService by lazy { FirebaseTimelineService() }
    private val persistenceService: PersistenceService by lazy { SharedPrefsPersistenceService(this) }
    private val analyticsService: AnalyticsService by lazy {
        if (BuildConfig.DEBUG) {
            DisabledAnalyticsService()
        } else {
            val activityProvider = CurrentActivityProvider.createAndRegister(this)
            FirebaseAnalyticsService(this, activityProvider)
        }
    }

    private val reduxLogger = object : Logger<AppState> {
        private val logger = HugoLogger("REDUX")
        override fun log(entry: Logger.Entry<AppState>) {
            logger.debug { "${entry.action} -> ${entry.newState}" }

            // Log REMOTE_ERROR as error too
            entry.action.let {
                if (it is REMOTE_ERROR) {
                    logger.error(it.error)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            HugoLogger.minLogLevel = Log.VERBOSE
        } else {
            // Better Crashlytics integration
            HugoLogger.onLogEvent = {
                if (it.level >= Log.INFO) {
                    with(it) {
                        Crashlytics.log("$tag: $message")
                    }
                }

                it.error?.let { nonFatalError ->
                    Crashlytics.logException(nonFatalError)
                }
            }
        }

        FirebasePerformanceService.initialize()
    }

    private class CurrentActivityProvider : SimpleActivityLifecycleCallbacks(), ActivityProvider {

        companion object {
            fun createAndRegister(application: Application) = CurrentActivityProvider()
                    .apply {
                        application.registerActivityLifecycleCallbacks(this)
                    }
        }

        private var weakActivity: WeakReference<Activity>? = null
        override val currentActivity: Activity?
            get() = weakActivity?.get()

        override fun onActivityStarted(activity: Activity) {
            weakActivity = WeakReference(activity)
        }
    }
}

