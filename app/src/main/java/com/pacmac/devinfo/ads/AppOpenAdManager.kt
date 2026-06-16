package com.pacmac.devinfo.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ui.ExportActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenAdManager @Inject constructor(
    private val application: Application
) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "AppOpenAdManager"
        private const val COOLDOWN_MS = 4 * 60 * 60 * 1000L // 4 hours
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var lastAdShownTime = 0L
    private var currentActivity: Activity? = null

    fun init() {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAd()
    }

    private fun loadAd() {
        if (isLoadingAd || appOpenAd != null) return
        isLoadingAd = true

        val adUnitId = application.getString(R.string.app_open_ad_id)
        val request = AdRequest.Builder().build()

        AppOpenAd.load(application, adUnitId, request, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isLoadingAd = false
                Log.d(TAG, "App open ad loaded")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                Log.d(TAG, "App open ad failed to load: ${loadAdError.message}")
            }
        })
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAvailable()
    }

    private fun showAdIfAvailable() {
        if (isShowingAd) return

        val activity = currentActivity ?: return

        // Skip if returning from ExportActivity (rewarded ad flow)
        if (activity is ExportActivity) return

        // Enforce cooldown
        val now = System.currentTimeMillis()
        if (now - lastAdShownTime < COOLDOWN_MS) return

        val ad = appOpenAd ?: run {
            loadAd()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                lastAdShownTime = System.currentTimeMillis()
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "App open ad shown")
            }
        }

        isShowingAd = true
        ad.show(activity)
    }

    // Application.ActivityLifecycleCallbacks
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        // keep currentActivity set — cleared only on destroy
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
