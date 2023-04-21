package com.pacmac.devinfo

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.defaultRequest

object KtorClient {

    private val client = HttpClient(Android) {
    }

    val getInstance = client
}