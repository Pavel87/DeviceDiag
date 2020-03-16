package com.pacmac.devinfo.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.CellularViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

public class ExportUtils {

    public final static String EXPORT_FILE = "EXPORT_FILE";
    public final static String EXPORT_SHARED_PREF_FILE = "EXPORT_SHARED_PREF_FILE";
    public final static String EXPORT_SLOT_AVAILABLE = "EXPORT_SLOT_AVAILABLE";

    public static String writeRecordsToFile(Context context, List<UIObject> records, String reportName, int format) {

        File exportFile = new File(context.getFilesDir(), reportName + ".csv");

        try {
            if (exportFile.exists()) {
                exportFile.delete();
            }
            exportFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(exportFile.getAbsolutePath(), true));


            StringBuilder sb = new StringBuilder();
            for (UIObject data : records) {
                if (data.getSuffix() == null) {
                    sb.append(String.format(Locale.ENGLISH, "%s,%s\n",
                            data.getLabel(), data.getValue()));
                } else {
                    sb.append(String.format(Locale.ENGLISH, "%s,%s,%s\n",
                            data.getLabel(), data.getValue(), data.getSuffix()));
                }
            }

            // SAVE OUTPUT TO FILE
            out.write(sb.toString());
            out.flush();
            out.close();
            return exportFile.getAbsolutePath();
        } catch (Exception e) {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }
    }


    public static void sendShareIntent(Context context, File file) {

        String subject = String.format("Device Info Export - %s %s", Build.MANUFACTURER,  Build.MODEL);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Data exported to " + file.getName());

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));

        Uri fileUri = null;
        try {
            fileUri = FileProvider.getUriForFile(context, "com.pacmac.devicediag.fileprovider", file);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        ClipData clipData = new ClipData(new ClipDescription("ExportData",
                new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), new ClipData.Item(fileUri));
        intentShareFile.setClipData(clipData);

        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Grant temporary read permission to the content URI
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.setType("application/*");


        context.startActivity(Intent.createChooser(intentShareFile, "Export Data"));
    }
}
