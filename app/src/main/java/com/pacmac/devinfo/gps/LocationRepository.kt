package com.pacmac.devinfo.gps

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.GnssStatus
import android.location.GpsSatellite
import android.location.GpsStatus
import android.location.LocationListener
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import com.pacmac.devinfo.gps.models.GPSStatusModel
import com.pacmac.devinfo.gps.models.LocationUpdate
import com.pacmac.devinfo.gps.models.NMEALog
import com.pacmac.devinfo.gps.models.Satellite
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.IOException
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject


class LocationRepository @Inject constructor(
    private val locationManager: LocationManager,
    private val conManager: ConnectivityManager,
    private val geocoder: Geocoder,
    private val packageManager: PackageManager
) {

    private var isGPSEnabled = false

    private fun createLocationListener(producer: ProducerScope<LocationUpdate>): LocationListener {
        return LocationListener { location ->
            val update = LocationUpdate(
                updateTime = location.time,
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                speed = location.speed,
                accuracy = location.accuracy,
                bearing = location.bearing,
            )

            producer.trySend(update)
        }
    }

    private fun createGnssStatusCallback(producer: ProducerScope<GPSStatusModel>): GnssStatus.Callback {

        return object : GnssStatus.Callback() {
            override fun onStarted() {
                producer.trySend(GPSStatusModel(Status.STARTING))
            }

            override fun onStopped() {
                producer.trySend(GPSStatusModel(Status.INACTIVE))
            }

            override fun onFirstFix(ttffMillis: Int) {
                producer.trySend(GPSStatusModel(Status.FIRST_FIX, firstFixTime = ttffMillis))
            }

            override fun onSatelliteStatusChanged(status: GnssStatus) {
                val satelliteList: ArrayList<Satellite> = arrayListOf()

                var id = 1
                for (i in 0 until status.satelliteCount) {
                    if (status.usedInFix(i)) {
                        val satellite = Satellite(
                            id++,
                            status.getCn0DbHz(i),  // SNR
                            status.getSvid(i),  // The identification number for the satellite at the specific index.
                            status.getAzimuthDegrees(i),
                            status.getElevationDegrees(i)
                        )
                        satellite.setConstellationType(status.getConstellationType(i))
                        satelliteList.add(satellite)
                    }
                }
                producer.trySend(
                    GPSStatusModel(
                        Status.ACTIVE,
                        satelliteCount = status.satelliteCount,
                        satellites = satelliteList
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun gpsStatusListener(producer: ProducerScope<GPSStatusModel>): GpsStatus.Listener {
        return GpsStatus.Listener { event ->
            try {
                val gpsStatus = locationManager.getGpsStatus(null)
                when (event) {
                    GpsStatus.GPS_EVENT_FIRST_FIX -> {
                        val timeToFirstFix = gpsStatus?.let {
                            gpsStatus.timeToFirstFix
                        } ?: -1
                        producer.trySend(
                            GPSStatusModel(
                                Status.FIRST_FIX,
                                firstFixTime = timeToFirstFix
                            )
                        )
                    }

                    GpsStatus.GPS_EVENT_STARTED -> producer.trySend(GPSStatusModel(Status.STARTING))
                    GpsStatus.GPS_EVENT_STOPPED -> producer.trySend(GPSStatusModel(Status.INACTIVE))
                    GpsStatus.GPS_EVENT_SATELLITE_STATUS -> {
                        val satelliteIterator: Iterator<GpsSatellite> =
                            gpsStatus!!.satellites.iterator()
                        val satelliteList: ArrayList<Satellite> = arrayListOf()

                        var id = 1
                        while (satelliteIterator.hasNext()) {
                            val satellite = satelliteIterator.next()
                            if (satellite.usedInFix()) {
                                satelliteList.add(
                                    Satellite(
                                        id++,
                                        satellite.snr,
                                        satellite.prn,
                                        satellite.azimuth,
                                        satellite.elevation
                                    )
                                )
                            }
                        }
                        producer.trySend(
                            GPSStatusModel(
                                Status.ACTIVE,
                                satelliteCount = satelliteList.size,
                                satellites = satelliteList
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getGnssYearOfHardware(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.gnssYearOfHardware
        } else -1
    }

    fun isGPSEnabled(): Boolean {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        return isGPSEnabled && hasGPS
    }

    @SuppressLint("MissingPermission")
    fun subscribeToLocationUpdates(): Flow<LocationUpdate> = callbackFlow {
        val locationListener = createLocationListener(this)
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )

        awaitClose {
            locationManager.removeUpdates(locationListener)
        }
    }

    @SuppressLint("MissingPermission")
    fun subscribeToGPSStatus(): Flow<GPSStatusModel> = callbackFlow {
        val gnssStatusCallback = createGnssStatusCallback(this)
        val gpsStatusListener = gpsStatusListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.registerGnssStatusCallback(gnssStatusCallback, null)
        } else {
            locationManager.addGpsStatusListener(gpsStatusListener)
        }

        awaitClose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
            } else {
                locationManager.removeGpsStatusListener(gpsStatusListener)
            }
        }
    }

    private val MAX_LOG_LINES = 128
    private val nmeaLog: Queue<NMEALog> = LinkedList()

    @SuppressLint("MissingPermission")
    fun subscribeToNMEAMessage(): Flow<Queue<NMEALog>> = callbackFlow {
        val nmeaListener = OnNmeaMessageListener { message, timestamp ->
            val log = NMEALog(Utils.formatTimeForNMEA(timestamp), message)
            nmeaLog.add(log)

            if (nmeaLog.size >= MAX_LOG_LINES) {
                nmeaLog.poll()
            }
            println("NMEA - new log : ${log.timeDate}")
            trySend(nmeaLog)
        }
        println("NMEA - REGISTER LISTENER")

        locationManager.addNmeaListener(nmeaListener, null)

        awaitClose {
            println("NMEA - AWAIT CLOSE")
            locationManager.removeNmeaListener(nmeaListener)
        }
    }

    fun fetchCurrentAddress(
        latitude: Double,
        longitude: Double
    ): Pair<String, String> {
        if (isNetworkConnected() && Geocoder.isPresent()) {
            try {
                val addresses =
                    geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses == null || addresses.size == 0) return "" to ""
                var street = addresses[0].thoroughfare
                var numHouse = addresses[0].subThoroughfare
                var city = addresses[0].subAdminArea
                var postalCode = addresses[0].postalCode
                street = street ?: ""
                numHouse = numHouse ?: ""
                city = city ?: ""
                postalCode = postalCode ?: ""

                val addressLine1 = "$street $numHouse"
                val addressLine2 = "$city $postalCode"
                return addressLine1 to addressLine2

            } catch (ex: IOException) {
                ex.printStackTrace() // will throw if no connection to server
            }
        }
        return "" to ""
    }

    private fun isNetworkConnected(): Boolean {
        var isConnected = false
        val networkInfo: NetworkInfo?

        // check WIFI state and if present in device
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
        ) {
            networkInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            isConnected = networkInfo!!.isConnectedOrConnecting

        } else if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        ) {
            networkInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            isConnected = networkInfo!!.isConnectedOrConnecting
        }
        return isConnected
    }
}