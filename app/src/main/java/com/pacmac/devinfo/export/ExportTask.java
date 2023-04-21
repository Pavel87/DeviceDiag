package com.pacmac.devinfo.export;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.CellInfo;

import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.battery.BatteryViewModelKt;
import com.pacmac.devinfo.camera.CameraUtilsKt;
import com.pacmac.devinfo.camera.CameraViewModelKt;
import com.pacmac.devinfo.camera.model.CameraSpec;
import com.pacmac.devinfo.cellular.CellularViewModelKt;
import com.pacmac.devinfo.cellular.MobileNetworkUtilKt;
import com.pacmac.devinfo.cellular.model.BasicPhoneModel;
import com.pacmac.devinfo.cellular.model.CellNetworkModel;
import com.pacmac.devinfo.cellular.model.SIMInfoModel;
import com.pacmac.devinfo.config.BuildPropViewModelKt;
import com.pacmac.devinfo.cpu.CPUViewModelKt;
import com.pacmac.devinfo.display.DisplayViewModelKt;
import com.pacmac.devinfo.gps.GPSViewModelKt;
import com.pacmac.devinfo.gps.Utils;
import com.pacmac.devinfo.main.model.MainInfoModel;
import com.pacmac.devinfo.main.MainViewModelKt;
import com.pacmac.devinfo.sensor.SensorViewModelKt;
import com.pacmac.devinfo.storage.StorageViewModelKt;
import com.pacmac.devinfo.wifi.NetworkViewModelKt;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;


@Deprecated //TODO
public class ExportTask extends AsyncTask<ViewModel, Void, String> {

    private final OnExportTaskFinished listener;
    private final Context context;
    private final String fileName;

    @Deprecated //TODO
    public ExportTask(Context context, String fileName, OnExportTaskFinished listener) {
        this.listener = listener;
        this.context = context;
        this.fileName = fileName;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(ViewModel... viewModels) {

        List<UIObject> list = null;
        String exportFilePath = null;

        if (viewModels[0] instanceof MainViewModelKt) {
            list = MainInfoModel.Companion.toUIModelList(context, ((MainViewModelKt) viewModels[0]).getMainInfo().getValue());
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof CellularViewModelKt) {
            BasicPhoneModel basicPhoneModel = ((CellularViewModelKt) viewModels[0]).getBasicInfo().getValue();
            List<SIMInfoModel> sim = ((CellularViewModelKt) viewModels[0]).getSimInfos().getValue();
            CellNetworkModel network = ((CellularViewModelKt) viewModels[0]).getNetworkInfos().getValue();
            List<CellInfo> cellInfos = ((CellularViewModelKt) viewModels[0]).getCellInfos().getValue();
            List<Pair<String, String>> config = ((CellularViewModelKt) viewModels[0]).getFilteredCarrierConfig().getValue();

            list = MobileNetworkUtilKt.INSTANCE.getAllPhoneInfoForExport(context, basicPhoneModel, sim, network, cellInfos, config);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof CPUViewModelKt) {
            list = ((CPUViewModelKt) viewModels[0]).getCpuInfoForExport(context);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof StorageViewModelKt) {
            list = ((StorageViewModelKt) viewModels[0]).getStorageInfoForExport(context);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof BatteryViewModelKt) {
            list = ((BatteryViewModelKt) viewModels[0]).getBatteryInfoForExport(context);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof DisplayViewModelKt) {
            list = ((DisplayViewModelKt) viewModels[0]).getDisplayInfoForExport(context);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof NetworkViewModelKt) {
            list = ((NetworkViewModelKt) viewModels[0]).getWifiInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof BuildPropViewModelKt) {
            list = com.pacmac.devinfo.utils.Utils.INSTANCE.getUIObjectsFromBuildProps(context, ((BuildPropViewModelKt) viewModels[0]).getFilteredBuildProperties().getValue());
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }

        if (viewModels[0] instanceof CameraViewModelKt) {
            list = new ArrayList<>(CameraUtilsKt.INSTANCE.getFormattedGeneralInfo(context, ((CameraViewModelKt) viewModels[0]).getCameraInfoGeneral().getValue(), true));

            int i = 0;
            for (CameraSpec spec : ((CameraViewModelKt) viewModels[0]).getCameraListData().getValue()) {
                list.addAll(CameraUtilsKt.INSTANCE.getCameraSpecParams(context, spec, true, i));
                i++;
            }
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }

        if (viewModels[0] instanceof SensorViewModelKt) {
            list = ((SensorViewModelKt) viewModels[0]).getSensorListForExport(context);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }

        if (viewModels[0] instanceof GPSViewModelKt) {

            if (fileName.equals(GPSViewModelKt.Companion.getEXPORT_FILE_NAME())) {

                UIObject updateTime = Utils.INSTANCE.getGPSUpdateTimeForExport(context, ((GPSViewModelKt) viewModels[0]).getUpdateTimeLive().getValue());
                List<UIObject> mainGPSData = Utils.INSTANCE.getMainGPSInfoList(context, ((GPSViewModelKt) viewModels[0]).getMainGPSData().getValue());
                List<UIObject> satellites = Utils.INSTANCE.getSatellitesForExport(context, ((GPSViewModelKt) viewModels[0]).getSatellites().getValue());
                list = new ArrayList<>();
                list.add(updateTime);
                list.addAll(mainGPSData);
                list.addAll(satellites);
                exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("=============NMEA FEED===============\n");
                sb.append(Utils.INSTANCE.getNMEALofForExport(((GPSViewModelKt) viewModels[0]).getNmeaLog().getValue()));
                exportFilePath = ExportUtils.writeDataToTXT(context, sb.toString(), fileName);
                return exportFilePath;
            }
        }

        if (list == null) {
            return null;
        } else {
            return exportFilePath;
        }
    }


    @Override
    protected void onPostExecute(String filePath) {
        if (listener != null) {
            listener.onExportTaskFinished(filePath);
        }
    }

    public interface OnExportTaskFinished {
        void onExportTaskFinished(String filePath);
    }

}



