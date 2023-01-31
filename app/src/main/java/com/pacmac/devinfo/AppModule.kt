package com.pacmac.devinfo

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import com.pacmac.devinfo.gps.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    fun provideConnectionManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
}
