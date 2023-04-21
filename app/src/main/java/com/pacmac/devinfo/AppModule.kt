package com.pacmac.devinfo

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pacmac.devinfo.cellular.PSLKt
import com.pacmac.devinfo.gps.LocationRepository
import com.pacmac.devinfo.main.data.AppRepository
import com.pacmac.devinfo.main.data.AppRepositoryImpl
import com.pacmac.devinfo.main.data.AppService
import com.pacmac.devinfo.main.data.AppServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val APP_CACHE_PREF_FILE = "APP_CACHE_PREF_FILE"
private const val MAIN_PREF_FILE = "de_vi_ce"
private const val EXPORT_SHARED_PREF_FILE = "EXPORT_SHARED_PREF_FILE"


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context)
    }

    @Provides
    fun providePSL(): PSLKt {
        return PSLKt()
    }

    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    fun provideConnectionManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager {
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    @Provides
    fun provideTelephonySubscriptionManager(@ApplicationContext context: Context): SubscriptionManager {
        return context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    fun provideLocationRepository(
        locationManager: LocationManager,
        connectivityManager: ConnectivityManager,
        geocoder: Geocoder,
        packageManager: PackageManager
    ): LocationRepository =
        LocationRepository(
            locationManager = locationManager,
            conManager = connectivityManager,
            geocoder = geocoder,
            packageManager = packageManager
        )

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, MAIN_PREF_FILE), SharedPreferencesMigration(context, EXPORT_SHARED_PREF_FILE)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(APP_CACHE_PREF_FILE) }
        )
    }

    @Singleton
    @Provides
    fun provideAppRepository(
        dataStore: DataStore<Preferences>,
        appService: AppService,
        packageManager: PackageManager
    ): AppRepository =
        AppRepositoryImpl(
            dataStore = dataStore,
            appService = appService,
            packageManager = packageManager
        )

    @Provides
    fun provideAppService(): AppService = AppServiceImpl()
}
