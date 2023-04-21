package com.pacmac.devinfo.main.data

import com.pacmac.devinfo.KtorClient
import com.pacmac.devinfo.utils.Utils
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText

class AppServiceImpl : AppService {

    private val APP_VERSION_CHECK_URL = "https://deviceinfo-23048.firebaseapp.com/version.html"

    private val ktorClient by lazy { KtorClient.getInstance }

    override suspend fun getAppVersion(): String {
        val response: HttpResponse = ktorClient.get(APP_VERSION_CHECK_URL)
        return Utils.extractVersionNameFromHTML(response.readText())
    }
}