package com.pacmac.devinfo.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.CellularViewModel;

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

        if (viewModels[0] instanceof CellularViewModel) {
            list = ((CellularViewModel) viewModels[0]).getAllPhoneInfoForExport();
            exportFilePath = ExportUtils.writeRecordsToFile(context, list, fileName, 0);
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



