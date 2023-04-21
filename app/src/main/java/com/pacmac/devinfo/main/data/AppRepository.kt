package com.pacmac.devinfo.main.data

import com.pacmac.devinfo.UpToDateEnum
import com.pacmac.devinfo.main.model.PermissionState
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    suspend fun updatePermissionStatus(permission: String, state: PermissionState)

    fun getPermissionStatus(permission: String): Flow<PermissionState>


    suspend fun updateLastAppVersion(versionCode: Int)

    fun getLastStoredAppVersion(): Flow<Int>

    suspend fun getLatestAppUpdate(): Flow<UpToDateEnum>

    fun appUpgradeModalDisplayed()

    fun getExportSlots(): Flow<Int>

    suspend fun updateExportSlot(slotOpened: Int)

}