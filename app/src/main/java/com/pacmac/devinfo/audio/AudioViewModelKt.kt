package com.pacmac.devinfo.audio

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AudioViewModelKt @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager
) : ViewModel() {

    private val _audioInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val audioInfo: StateFlow<List<UIObject>> = _audioInfo.asStateFlow()

    init {
        _audioInfo.value = AudioInfoKt.getAudioInfo(context, packageManager)
    }

    fun getAudioInfoForExport(context: Context): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.title_activity_audio_info), "", ListType.TITLE))
        add(UIObject(context.getString(R.string.audio_codec_name), context.getString(R.string.value), ListType.TITLE))
        addAll(_audioInfo.value)
    }
}
