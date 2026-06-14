package com.pacmac.devinfo.export

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.battery.BatteryViewModelKt
import com.pacmac.devinfo.camera.CameraUtilsKt
import com.pacmac.devinfo.camera.CameraViewModelKt
import com.pacmac.devinfo.cellular.CellularViewModelKt
import com.pacmac.devinfo.cellular.MobileNetworkUtilKt
import com.pacmac.devinfo.config.BuildPropViewModelKt
import com.pacmac.devinfo.cpu.CPUViewModelKt
import com.pacmac.devinfo.display.DisplayViewModelKt
import com.pacmac.devinfo.gps.GPSViewModelKt
import com.pacmac.devinfo.gps.Utils as GpsUtils
import com.pacmac.devinfo.main.MainViewModelKt
import com.pacmac.devinfo.main.model.MainInfoModel
import com.pacmac.devinfo.sensor.SensorViewModelKt
import com.pacmac.devinfo.storage.StorageViewModelKt
import com.pacmac.devinfo.utils.Utils as AppUtils
import com.pacmac.devinfo.wifi.NetworkViewModelKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExportTask(
    private val context: Context,
    private val fileName: String,
    private val listener: OnExportTaskFinished,
) {
    fun interface OnExportTaskFinished {
        fun onExportTaskFinished(filePath: String?)
    }

    private val scope = MainScope()

    fun execute(vararg viewModels: ViewModel) {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    buildExportData(viewModels[0])
                } catch (e: Exception) {
                    Log.e("ExportTask", "Export failed", e)
                    null
                }
            }
            scope.cancel()
            listener.onExportTaskFinished(result)
        }
    }

    private fun buildExportData(viewModel: ViewModel): String? = when (viewModel) {
        is MainViewModelKt -> {
            val list = MainInfoModel.toUIModelList(context, viewModel.mainInfo.value)
            ExportUtils.writeRecordsToFile(context, list ?: emptyList(), fileName, 0)
        }
        is CellularViewModelKt -> {
            val basicInfo = viewModel.basicInfo.value ?: return null
            val networkInfos = viewModel.networkInfos.value ?: return null
            val list = MobileNetworkUtilKt.getAllPhoneInfoForExport(
                context,
                basicInfo,
                viewModel.simInfos.value,
                networkInfos,
                viewModel.cellInfos.value ?: emptyList(),
                viewModel.filteredCarrierConfig.value.map { it.first to (it.second ?: "") },
            )
            ExportUtils.writeRecordsToFile(context, list ?: emptyList(), fileName, 0)
        }
        is CPUViewModelKt ->
            ExportUtils.writeRecordsToFile(context, viewModel.getCpuInfoForExport(context), fileName, 0)
        is StorageViewModelKt ->
            ExportUtils.writeRecordsToFile(context, viewModel.getStorageInfoForExport(context), fileName, 0)
        is BatteryViewModelKt ->
            ExportUtils.writeRecordsToFile(context, viewModel.getBatteryInfoForExport(context), fileName, 0)
        is DisplayViewModelKt ->
            ExportUtils.writeRecordsToFile(context, viewModel.getDisplayInfoForExport(context), fileName, 0)
        is NetworkViewModelKt ->
            ExportUtils.writeRecordsToFile(context, viewModel.getWifiInfoForExport(), fileName, 0)
        is BuildPropViewModelKt -> {
            val list = AppUtils.getUIObjectsFromBuildProps(context, viewModel.filteredBuildProperties.value)
            ExportUtils.writeRecordsToFile(context, list ?: emptyList(), fileName, 0)
        }
        is CameraViewModelKt -> {
            val exportList = mutableListOf<UIObject>()
            exportList.addAll(CameraUtilsKt.getFormattedGeneralInfo(context, viewModel.cameraInfoGeneral.value, true))
            viewModel.cameraListData.value.forEachIndexed { i, spec ->
                exportList.addAll(CameraUtilsKt.getCameraSpecParams(context, spec, true, i))
            }
            ExportUtils.writeRecordsToFile(context, exportList, fileName, 0)
        }
        is SensorViewModelKt ->
            ExportUtils.writeRecordsToFile(context, viewModel.getSensorListForExport(context), fileName, 0)
        is GPSViewModelKt -> {
            if (fileName == GPSViewModelKt.EXPORT_FILE_NAME) {
                val exportList = mutableListOf<UIObject>()
                exportList.add(GpsUtils.getGPSUpdateTimeForExport(context, viewModel.updateTimeLive.value))
                exportList.addAll(GpsUtils.getMainGPSInfoList(context, viewModel.gpsInfo.value))
                exportList.addAll(GpsUtils.getSatellitesForExport(context, viewModel.satellites.value))
                ExportUtils.writeRecordsToFile(context, exportList, fileName, 0)
            } else {
                val nmea = buildString {
                    append("=============NMEA FEED===============\n")
                    append(GpsUtils.getNMEALofForExport(viewModel.nmeaLog.value))
                }
                ExportUtils.writeDataToTXT(context, nmea, fileName)
            }
        }
        else -> null
    }
}
