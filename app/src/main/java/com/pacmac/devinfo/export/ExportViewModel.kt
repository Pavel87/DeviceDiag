package com.pacmac.devinfo.export

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pacmac.devinfo.main.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {

    val openedSlots = appRepository.getExportSlots()

    private val _onStartPromoActivity = MutableSharedFlow<Boolean>()
    val onStartPromoActivity = _onStartPromoActivity.asSharedFlow()

    private val _onRewardEarned = MutableSharedFlow<Int>()
    val onRewardEarned = _onRewardEarned.asSharedFlow()

    private val _showRewardAd = MutableSharedFlow<RewardedAd>()
    val showRewardAd = _showRewardAd.asSharedFlow()

    private val _loadRewardAd = MutableSharedFlow<Unit>()
    val loadRewardAd = _loadRewardAd.asSharedFlow()

    private val _connectionError = MutableSharedFlow<Unit>()
    val connectionError = _connectionError.asSharedFlow()

    private val _isShowAdButtonEnabled = mutableStateOf(true)
    val isShowAdButtonEnabled: State<Boolean> = _isShowAdButtonEnabled

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading


    private var rewardedAd: RewardedAd? = null
    private var error = -1
    private var isAdLoading = false
    private var userClick = false


    fun onAdClick() {
        userClick = true
        _isLoading.value = true
        if (!isAdLoading) {
            if (rewardedAd != null && error != AdRequest.ERROR_CODE_NETWORK_ERROR) {
                _isShowAdButtonEnabled.value = false
                userClick = false
                rewardedAd?.let {
                    viewModelScope.launch {
                        _showRewardAd.emit(it)
                    }
                }
            } else if (error == AdRequest.ERROR_CODE_NETWORK_ERROR) {
                viewModelScope.launch {
                    _connectionError.emit(Unit)
                    createAndLoadRewardedAd()
                }
            } else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
                viewModelScope.launch {
                    createAndLoadRewardedAd()
                }
                _isShowAdButtonEnabled.value = true
            }
        }
    }

    private fun addExportSlot() {
        viewModelScope.launch {
            val openedSlots = appRepository.getExportSlots().firstOrNull() ?: 0
            _onRewardEarned.emit(openedSlots)
            println("PACMAC - addExportSlot $openedSlots")
            appRepository.updateExportSlot((openedSlots + 1).coerceAtMost(5))
        }
    }

    private suspend fun createAndLoadRewardedAd() {
        isAdLoading = true
        _loadRewardAd.emit(Unit)
    }

    fun getAdShowCallback() = object : RewardedAdLoadCallback() {
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            rewardedAd = null
            Log.d("PACMAC-EXPORT", "onAdFailedToLoad:" + loadAdError.message)
            error = loadAdError.code
            isAdLoading = false
            userClick = false
            _isShowAdButtonEnabled.value = true
            _isLoading.value = false
        }

        override fun onAdLoaded(ad: RewardedAd) {
            Log.d("PACMAC-EXPORT", "onAdLoaded")
            ad.fullScreenContentCallback = fullScreenContentCallback
            rewardedAd = ad
            isAdLoading = false
            error = -1
            _isShowAdButtonEnabled.value = true
            _isLoading.value = false
            if (userClick) {
                rewardedAd?.let {
                    viewModelScope.launch {
                        _showRewardAd.emit(it)
                    }
                }
            }
            userClick = false
        }
    }

    fun getUserEarnedRewardListener() = OnUserEarnedRewardListener {
        addExportSlot()
    }

    private val fullScreenContentCallback: FullScreenContentCallback =
        object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("PACMAC-EXPORT", "Ad was shown.")
                _isShowAdButtonEnabled.value = false
                viewModelScope.launch {
                    createAndLoadRewardedAd()
                }
                _isLoading.value = false
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.d("PACMAC-EXPORT", "Ad failed to show: " + adError.message)
                rewardedAd = null
                viewModelScope.launch {
                    createAndLoadRewardedAd()
                    _onStartPromoActivity.emit(true)
                }
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d("PACMAC-EXPORT", "Ad was dismissed.")
                rewardedAd = null
            }
        }

    fun onExportClick() {
        viewModelScope.launch {
            val openedSlots = appRepository.getExportSlots().firstOrNull() ?: 0
            println("PACMAC - onExportClick $openedSlots")
            appRepository.updateExportSlot((openedSlots - 1).coerceAtLeast(0))
        }
    }

}