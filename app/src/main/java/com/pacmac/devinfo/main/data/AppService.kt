package com.pacmac.devinfo.main.data

interface AppService {
    suspend fun getAppVersion(): String
}