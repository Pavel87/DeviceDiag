package com.pacmac.devinfo.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;

import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.battery.BatteryViewModel;
import com.pacmac.devinfo.cellular.CellularViewModel;
import com.pacmac.devinfo.config.BuildPropertiesViewModel;
import com.pacmac.devinfo.cpu.CPUViewModel;
import com.pacmac.devinfo.display.DisplayViewModel;
import com.pacmac.devinfo.gps.GPSViewModel;
import com.pacmac.devinfo.main.MainViewModel;
import com.pacmac.devinfo.storage.StorageViewModel;
import com.pacmac.devinfo.wifi.NetworkViewModel;

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
            list = ((MainViewModel) viewModels[0]).getMainInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof CellularViewModel) {
            list = ((CellularViewModel) viewModels[0]).getAllPhoneInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof CPUViewModel) {
            list = ((CPUViewModel) viewModels[0]).getCpuInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof StorageViewModel) {
            list = ((StorageViewModel) viewModels[0]).getStorageInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof BatteryViewModel) {
            list = ((BatteryViewModel) viewModels[0]).getBatteryInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof DisplayViewModel) {
            list = ((DisplayViewModel) viewModels[0]).getDisplayInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof NetworkViewModel) {
            list = ((NetworkViewModel) viewModels[0]).getWifiInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }
        if (viewModels[0] instanceof BuildPropertiesViewModel) {
            list = ((BuildPropertiesViewModel) viewModels[0]).getBuildPropertiesForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
        }

        if (viewModels[0] instanceof GPSViewModel) {

            if (fileName.equals(GPSViewModel.EXPORT_FILE_NAME)) {
                list = ((GPSViewModel) viewModels[0]).getMainGPSDataForExport();
                exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("=============NMEA FEED===============\n");
                sb.append(Html.fromHtml(((GPSViewModel) viewModels[0]).getMessageLive().getValue()));
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



