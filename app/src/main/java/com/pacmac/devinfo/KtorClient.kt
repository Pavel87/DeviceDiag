package com.pacmac.devinfo

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

object KtorClient {

    private val client = HttpClient(Android) {
    }

    val getInstance = client
}
