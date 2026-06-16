package com.pacmac.devinfo.ads

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pacmac.devinfo.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdManager @Inject constructor(
    private val application: Application
) {

    companion object {
        private const val TAG = "InterstitialAdManager"
        private const val SHOW_EVERY_N_EXITS = 4
    }

    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false
    private var exitCounter = 0

    fun preload() {
        loadAd()
    }

    private fun loadAd() {
        if (isLoadingAd || interstitialAd != null) return
        isLoadingAd = true

        val adUnitId = application.getString(R.string.interstitial_id_1)
        val request = AdRequest.Builder().build()

        InterstitialAd.load(application, adUnitId, request, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                isLoadingAd = false
                Log.d(TAG, "Interstitial ad loaded")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                Log.d(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
            }
        })
    }

    fun maybeShowInterstitial(activity: Activity, onDismissed: () -> Unit) {
        exitCounter++

        // Never show on 1st exit, then every Nth exit
        if (exitCounter < SHOW_EVERY_N_EXITS || exitCounter % SHOW_EVERY_N_EXITS != 0) {
            onDismissed()
            return
        }

        val ad = interstitialAd
        if (ad == null) {
            loadAd()
            onDismissed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAd()
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                loadAd()
                onDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad shown")
            }
        }

        interstitialAd = null
        ad.show(activity)
    }
}
