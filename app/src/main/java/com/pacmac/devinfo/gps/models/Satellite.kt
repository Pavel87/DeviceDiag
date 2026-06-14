package com.pacmac.devinfo.gps.models

import android.location.GnssStatus
import android.os.Parcel
import android.os.Parcelable
import java.util.Locale

class Satellite(
    val id: Int,
    val snr: Float,
    val pnr: Int,
    val azimuth: Float,
    val elevation: Float,
    var constellationType: Int = 0,
) : Parcelable {

    fun getConstellationType(): String = getConstellation(constellationType)

    constructor(source: Parcel) : this(
        id = source.readInt(),
        snr = source.readFloat(),
        pnr = source.readInt(),
        azimuth = source.readFloat(),
        elevation = source.readFloat(),
        constellationType = source.readInt(),
    )

    override fun describeContents(): Int = hashCode()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(pnr)
        parcel.writeFloat(snr)
        parcel.writeFloat(azimuth)
        parcel.writeFloat(elevation)
        parcel.writeInt(constellationType)
    }

    override fun toString(): String =
        String.format(Locale.ENGLISH, "%d,%s,%.1f,%.1f,%.1f", pnr, getConstellation(constellationType), snr, azimuth, elevation)

    private fun getConstellation(type: Int): String = when (type) {
        GnssStatus.CONSTELLATION_UNKNOWN -> "UNKNOWN"
        GnssStatus.CONSTELLATION_GPS -> "GPS"
        GnssStatus.CONSTELLATION_SBAS -> "SBAS"
        GnssStatus.CONSTELLATION_GLONASS -> "GLONASS"
        GnssStatus.CONSTELLATION_QZSS -> "QZSS"
        GnssStatus.CONSTELLATION_BEIDOU -> "BEIDOU"
        GnssStatus.CONSTELLATION_GALILEO -> "GALILEO"
        GnssStatus.CONSTELLATION_IRNSS -> "IRNSS"
        else -> type.toString()
    }

    companion object CREATOR : Parcelable.Creator<Satellite> {
        override fun createFromParcel(source: Parcel): Satellite = Satellite(source)
        override fun newArray(size: Int): Array<Satellite?> = arrayOfNulls(size)
    }
}
