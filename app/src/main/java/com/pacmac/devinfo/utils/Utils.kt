package com.pacmac.devinfo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object Utils {

    fun openGooglePlayListing(packageName: String, context: Context) {
        val appPackage = packageName
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackage"))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
            context.startActivity(intent)
        }
    }

}