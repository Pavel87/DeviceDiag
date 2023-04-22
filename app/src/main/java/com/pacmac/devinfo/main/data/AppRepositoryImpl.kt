package com.pacmac.devinfo.main.data

import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.pacmac.devinfo.UpToDateEnum
import com.pacmac.devinfo.main.model.PermissionState
import com.pacmac.devinfo.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val appService: AppService,
    private val packageManager: PackageManager
) : AppRepository {

    override suspend fun updatePermissionStatus(permission: String, state: PermissionState) {
        dataStore.edit { preferences ->
            val key = getPermissionPrefKey(permission)
            preferences[key] = state.status
        }
    }

    override fun getPermissionStatus(permission: String) = flow {
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(PermissionState.DENIED)
            } else {
                throw exception
            }
        }.map { preferences ->
            // Get our show completed value, defaulting to false if not set:
            val status = preferences[getPermissionPrefKey(permission)] ?: 0
            emit(PermissionState.values().find { it.status == status }
                ?: PermissionState.DENIED)
        }.first()
    }

    override suspend fun updateLastAppVersion(versionCode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VERSION_CODE_KEY] = versionCode
        }
    }

    override fun getLastStoredAppVersion(): Flow<Int>  = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                0
            } else {
                throw exception
            }
        }.map { preferences ->
            println("PACMAC -- getLastStoredAppVersion")
            preferences[PreferencesKeys.VERSION_CODE_KEY] ?: 0
        }

    private fun getPermissionPrefKey(permission: String): Preferences.Key<Int> {
        return when (permission) {
            Utils.LOCATION_PERMISSION -> PreferencesKeys.LOCATION_PERMISSION_KEY
            Utils.STORAGE_PERMISSION -> PreferencesKeys.STORAGE_PERMISSION_KEY
            Utils.PHONE_PERMISSION -> PreferencesKeys.PHONE_PERMISSION_KEY
            Utils.PHONE_NUMBER_PERMISSION -> PreferencesKeys.PHONE_NUMBER_PERMISSION_KEY
            Utils.CAMERA_PERMISSION -> PreferencesKeys.CAMERA_PERMISSION_KEY
            else -> {
                throw Exception("Missing pref key for permission: $permission")
            }
        }
    }

    private object PreferencesKeys {
        val VERSION_CODE_KEY = intPreferencesKey("version_key")
        val LOCATION_PERMISSION_KEY = intPreferencesKey(Utils.LOCATION_PERMISSION)
        val STORAGE_PERMISSION_KEY = intPreferencesKey(Utils.STORAGE_PERMISSION)
        val PHONE_PERMISSION_KEY = intPreferencesKey(Utils.PHONE_PERMISSION)
        val PHONE_NUMBER_PERMISSION_KEY = intPreferencesKey(Utils.PHONE_NUMBER_PERMISSION)
        val CAMERA_PERMISSION_KEY = intPreferencesKey(Utils.CAMERA_PERMISSION)
        val EXPORT_SLOT_AVAILABLE = intPreferencesKey("EXPORT_SLOT_AVAILABLE")
    }


    /**
     * APP VERSION CHECK
     */

    private val APP_VERSION_DEFAULT: String = "0.0.0"
    private var appVersion = APP_VERSION_DEFAULT
    private var userHasSeenAppUpgradeModal = false

    private fun getCurrentAppVersion(): String {
        if (appVersion == APP_VERSION_DEFAULT) {
            try {
                appVersion = packageManager.getPackageInfo(
                    "com.pacmac.devicediag.free",
                    PackageManager.GET_META_DATA
                ).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return appVersion
    }

    override suspend fun getLatestAppUpdate() = flow {
        if (userHasSeenAppUpgradeModal) {
            println("PACMAC - new app version available modal displayed")
            emit(UpToDateEnum.UNKNOWN)
            return@flow
        }
        val appVersion = getCurrentAppVersion()

        try {
            val serverAppVersionString: String = appService.getAppVersion()
            println("PACMAC - fetched version: $serverAppVersionString")
            if (appVersion == serverAppVersionString) {
                emit(UpToDateEnum.YES)
            } else {
                emit(Utils.hasVersionIncreased(appVersion, serverAppVersionString))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        emit(UpToDateEnum.UNKNOWN)
    }

    override fun appUpgradeModalDisplayed() {
        userHasSeenAppUpgradeModal = true
    }


    /**
     * EXPORT
     */

    override fun getExportSlots(): Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.EXPORT_SLOT_AVAILABLE] ?: 0
        }

    override suspend fun updateExportSlot(slotOpened: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXPORT_SLOT_AVAILABLE] = slotOpened
        }
    }
}