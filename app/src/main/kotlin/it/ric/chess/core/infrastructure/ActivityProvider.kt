package it.ric.chess.core.infrastructure

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityProvider @Inject constructor() : Application.ActivityLifecycleCallbacks {
    private var currentActivityReference: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = currentActivityReference?.get()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) {
            currentActivityReference = null
        }
    }
}
