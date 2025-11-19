package com.linli.blackcatnews.rating

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

interface CurrentActivityProvider {
    fun getCurrentActivity(): ComponentActivity?
}

class LifecycleAwareActivityProvider(application: Application) :
    Application.ActivityLifecycleCallbacks,
    CurrentActivityProvider {

    private var currentActivity: WeakReference<ComponentActivity?> = WeakReference(null)

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun getCurrentActivity(): ComponentActivity? = currentActivity.get()

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity as? ComponentActivity)
    }

    override fun onActivityPaused(activity: Activity) {
        val stored = currentActivity.get()
        if (stored === activity) {
            currentActivity = WeakReference(null)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        val stored = currentActivity.get()
        if (stored === activity) {
            currentActivity = WeakReference(null)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
