package com.pacmac.devinfo.export

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.net.URLConnection
import java.util.Locale

object ExportUtils {

    const val EXPORT_FILE = "EXPORT_FILE"

    fun writeRecordsToFile(context: Context, records: List<UIObject>, reportName: String, format: Int): String? {
        val exportFile = File(context.filesDir, "$reportName.csv")
        try {
            if (exportFile.exists()) exportFile.delete()
            exportFile.createNewFile()
        } catch (e: Exception) {
            Log.e("ExportUtils", "Failed to create export file", e)
        }

        return try {
            BufferedWriter(FileWriter(exportFile.absolutePath, true)).use { out ->
                val sb = StringBuilder()
                for (data in records) {
                    if (data.type == ListType.ICON) {
                        val state = when (data.state) {
                            ThreeState.YES -> context.getString(R.string.yes_string)
                            ThreeState.NO -> context.getString(R.string.no_string)
                            else -> context.getString(R.string.not_available_info)
                        }
                        sb.append(String.format(Locale.ENGLISH, "%s,%s\n", data.label, state))
                    } else if (data.suffix == null) {
                        sb.append(String.format(Locale.ENGLISH, "%s,%s\n", data.label, data.value))
                    } else {
                        sb.append(String.format(Locale.ENGLISH, "%s,%s,%s\n", data.label, data.value, data.suffix))
                    }
                }
                out.write(sb.toString())
                out.flush()
            }
            exportFile.absolutePath
        } catch (e: Exception) {
            Log.e("ExportUtils", "Failed to write records to file", e)
            null
        }
    }

    fun writeDataToTXT(context: Context, data: String, reportName: String): String? {
        val exportFile = File(context.filesDir, reportName)
        try {
            if (exportFile.exists()) exportFile.delete()
            exportFile.createNewFile()
        } catch (e: Exception) {
            Log.e("ExportUtils", "Failed to create TXT export file", e)
        }

        return try {
            BufferedWriter(FileWriter(exportFile.absolutePath, true)).use { out ->
                out.write(data)
                out.flush()
            }
            exportFile.absolutePath
        } catch (e: Exception) {
            Log.e("ExportUtils", "Failed to write data to TXT file", e)
            null
        }
    }

    fun sendShareIntent(context: Context, file: File) {
        val subject = String.format(context.getString(R.string.device_info_export), Build.MANUFACTURER, Build.MODEL)
        val intentShareFile = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.exported_to) + " " + file.name)
            type = URLConnection.guessContentTypeFromName(file.name)
        }

        val fileUri: Uri? = try {
            FileProvider.getUriForFile(context, "com.pacmac.devicediag.fileprovider", file)
        } catch (e: IllegalArgumentException) {
            Log.e("ExportUtils", "Failed to get URI for file", e)
            null
        }

        val clipData = ClipData(
            ClipDescription("ExportData", arrayOf(ClipDescription.MIMETYPE_TEXT_URILIST)),
            ClipData.Item(fileUri)
        )
        intentShareFile.clipData = clipData
        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intentShareFile.type = "application/*"

        context.startActivity(Intent.createChooser(intentShareFile, context.getString(R.string.chooser_export_data)))
    }
}
