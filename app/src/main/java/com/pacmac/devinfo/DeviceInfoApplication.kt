package com.pacmac.devinfo

import android.app.Application
import com.pacmac.devinfo.ads.AppOpenAdManager
import com.pacmac.devinfo.ads.InterstitialAdManager
import com.pacmac.devinfo.ads.RewardedBannerDismissManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DeviceInfoApplication : Application() {

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    @Inject
    lateinit var interstitialAdManager: InterstitialAdManager

    @Inject
    lateinit var rewardedBannerDismissManager: RewardedBannerDismissManager

    override fun onCreate() {
        super.onCreate()
        appOpenAdManager.init()
        interstitialAdManager.preload()
        rewardedBannerDismissManager.init()
    }
}
