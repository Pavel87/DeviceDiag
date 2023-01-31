package com.pacmac.devinfo.storage

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StorageViewModelKt : ViewModel() {

    private val storageInfo = mutableStateOf<List<UIObject>>(arrayListOf())

    fun getStorageInfo(): State<List<UIObject>> = storageInfo

    fun observeStorageInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadStorageInfo(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getStorageInfoForExport(context: Context): List<UIObject> {
        val list: MutableList<UIObject> = ArrayList()
        list.add(
            UIObject(
                context.getString(R.string.title_activity_storage_info), "", ListType.TITLE
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.param), context.getString(R.string.value), ListType.TITLE
            )
        )
        list.addAll(storageInfo.value)
        return list
    }


    private fun loadStorageInfo(context: Context) {
        val list: MutableList<UIObject> = ArrayList()
        list.add(UIObject(context.getString(R.string.device_ram), "", ListType.TITLE))
        val flashHW = StorageUtils.getRAMHardware()
        if (flashHW != null) {
            list.add(UIObject(context.getString(R.string.device_ram_hw), flashHW))
        }
        val totalRAM = StorageUtils.getTotalMemory(context)
        list.add(UIObject(context.getString(R.string.device_total), totalRAM.value, totalRAM.unit))
        val available = StorageUtils.getAvailableMemory(context)
        list.add(
            UIObject(
                context.getString(R.string.device_available), available.value, available.unit
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.device_low_memory),
                StorageUtils.getLowMemoryStatus(context)
            )
        )


        //retrieve STORAGE OPTIONS
        val listStorage = StorageUtils.getDeviceStorage(context)
        if (listStorage.size > 0) {
            if (listStorage.size > 1) {
                var total: Long = 0
                var free: Long = 0
                for (storage in listStorage) {
                    total += storage.total
                    free += storage.free
                }
                list.add(
                    UIObject(
                        context.getString(R.string.device_storage_label), "", ListType.TITLE
                    )
                )
                val totalStorage = StorageUtils.byteConvertor(total)
                val availableStorage = StorageUtils.byteConvertor(free)
                list.add(
                    UIObject(
                        context.resources.getString(R.string.device_total),
                        totalStorage.value,
                        totalStorage.unit
                    )
                )
                list.add(
                    UIObject(
                        context.getString(R.string.device_available),
                        availableStorage.value,
                        availableStorage.unit
                    )
                )
                val used = StorageUtils.byteConvertor(total - free)
                list.add(UIObject(context.getString(R.string.device_used), used.value, used.unit))
            }
            for (s in listStorage) {
                list.add(UIObject(StorageUtils.getTypeString(context, s.type), "", ListType.TITLE))
                val totalSD = StorageUtils.byteConvertor(s.total)
                list.add(
                    UIObject(
                        context.resources.getString(R.string.device_total),
                        totalSD.value,
                        totalSD.unit
                    )
                )
                val freeSD = StorageUtils.byteConvertor(s.free)
                list.add(
                    UIObject(
                        context.getString(R.string.device_available), freeSD.value, freeSD.unit
                    )
                )
                val usedSD = StorageUtils.byteConvertor(s.total - s.free)
                list.add(
                    UIObject(
                        context.getString(R.string.device_used), usedSD.value, usedSD.unit
                    )
                )
            }
        }
        storageInfo.value = list
    }
}
