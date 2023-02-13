package com.pacmac.devinfo.export;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.battery.BatteryViewModelKt;
import com.pacmac.devinfo.camera.CameraUtilsKt;
import com.pacmac.devinfo.camera.CameraViewModelKt;
import com.pacmac.devinfo.camera.model.CameraSpec;
import com.pacmac.devinfo.cellular.CellularViewModel;
import com.pacmac.devinfo.config.BuildPropertiesViewModel;
import com.pacmac.devinfo.cpu.CPUViewModelKt;
import com.pacmac.devinfo.display.DisplayViewModelKt;
import com.pacmac.devinfo.gps.GPSViewModelKt;
import com.pacmac.devinfo.gps.Utils;
import com.pacmac.devinfo.main.MainViewModel;
import com.pacmac.devinfo.sensor.SensorViewModelKt;
import com.pacmac.devinfo.storage.StorageViewModelKt;
import com.pacmac.devinfo.wifi.NetworkViewModelKt;

import java.util.ArrayList;
import java.util.List;

public class ExportTask extends AsyncTask<ViewModel, Void, String> {

    private final OnExportTaskFinished listener;
    private final Context context;
    private final String fileName;

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

        if (viewModels[0] instanceof MainViewModel) {
            list = ((MainViewModel) viewModels[0]).getMainInfoForExport(context);
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof CellularViewModel) {
            list = ((CellularViewModel) viewModels[0]).getAllPhoneInfoForExport(context);
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
        if (viewModels[0] instanceof BuildPropertiesViewModel) {
            list = ((BuildPropertiesViewModel) viewModels[0]).getBuildPropertiesForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }

        if (viewModels[0] instanceof CameraViewModelKt) {
            list = new ArrayList<>(CameraUtilsKt.INSTANCE.getFormattedGeneralInfo(context, ((CameraViewModelKt) viewModels[0]).getCameraInfoGeneral().getValue(), true));

            int i =0;
            for (CameraSpec spec : ((CameraViewModelKt)viewModels[0]).getCameraListData().getValue()) {
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



