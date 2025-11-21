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

    override fun getCurrentActivity(): ComponentActivity? {
        return currentActivity.get()
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity as? ComponentActivity)
    }

    override fun onActivityPaused(activity: Activity) {
        // 不在 Paused 時清空引用，因為 Activity 仍然可見，可以顯示對話框
        // 評分對話框需要在這個階段顯示
    }

    override fun onActivityStopped(activity: Activity) {
        // 在 Activity 停止時清空引用，此時 Activity 已經不可見
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

    override fun onActivityStarted(activity: Activity) {
        // 在 Activity 啟動時也設置引用，確保在導航返回時能獲取到
        if (currentActivity.get() == null) {
            currentActivity = WeakReference(activity as? ComponentActivity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
