package com.pacmac.devinfo.ads

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pacmac.devinfo.R
import com.pacmac.devinfo.main.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedBannerDismissManager @Inject constructor(
    private val application: Application,
    private val appRepository: AppRepository
) {

    companion object {
        private const val TAG = "RewardedBannerDismiss"
        private const val BANNER_FREE_DURATION_MS = 60 * 60 * 1000L // 1 hour
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var rewardedAd: RewardedAd? = null
    private var isLoadingAd = false

    lateinit var bannerFreeUntilFlow: StateFlow<Long>
        private set

    fun init() {
        bannerFreeUntilFlow = appRepository.getBannerFreeUntil()
            .stateIn(scope, SharingStarted.Eagerly, 0L)
        loadAd()
    }

    private fun loadAd() {
        if (isLoadingAd || rewardedAd != null) return
        isLoadingAd = true

        val adUnitId = application.getString(R.string.rewarded2)
        val request = AdRequest.Builder().build()

        RewardedAd.load(application, adUnitId, request, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                isLoadingAd = false
                Log.d(TAG, "Rewarded ad loaded")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                Log.d(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
            }
        })
    }

    fun showRewardedAd(activity: Activity, onRewarded: () -> Unit, onFailed: () -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            loadAd()
            onFailed()
            return
        }

        rewardedAd = null
        ad.show(activity) {
            val bannerFreeUntil = System.currentTimeMillis() + BANNER_FREE_DURATION_MS
            scope.launch {
                appRepository.setBannerFreeUntil(bannerFreeUntil)
            }
            onRewarded()
            loadAd()
        }
    }
}
