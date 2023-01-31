package com.pacmac.devinfo.gps.models;

import android.location.GnssStatus;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Created by pacmac on 6/27/2015.
 */

// ACTIVE SATELITES STORAGE
public class Satellite implements Parcelable {

    private final int id;
    private final int pnr; // sat pseudo-random satellite ID
    private final float snr, azimuth, elevation;

    private int constellationType = 0;

    public static final Creator<Satellite> CREATOR = new Creator<Satellite>() {
        @Override
        public Satellite createFromParcel(Parcel in) {
            return new Satellite(in);
        }

        @Override
        public Satellite[] newArray(int size) {
            return new Satellite[size];
        }
    };

    public String getConstellationType() {
        return getConstellation(constellationType);
    }

    public int getId() {
        return id;
    }

    public float getSnr() {
        return snr;
    }

    public int getPnr() {
        return pnr;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getElevation() {
        return elevation;
    }

    public void setConstellationType(int constellationType) {
        this.constellationType = constellationType;
    }

    public Satellite(int id, float snr, int pnr, float azimuth, float elevation) {
        this.id = id;
        this.snr = snr;
        this.pnr = pnr;
        this.azimuth = azimuth;
        this.elevation = elevation;
    }

    // constructor for parcelable

    public Satellite(Parcel source) {
        this.id = source.readInt();
        this.pnr = source.readInt();
        this.snr = source.readFloat();
        this.azimuth = source.readFloat();
        this.elevation = source.readFloat();
        this.constellationType = source.readInt();
    }

    // adding PARCELABLE

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(pnr);
        parcel.writeFloat(snr);
        parcel.writeFloat(azimuth);
        parcel.writeFloat(elevation);
        parcel.writeInt(constellationType);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%d,%s,%.1f,%.1f,%.1f",
                pnr,
                getConstellation(constellationType),
                snr,
                azimuth,
                elevation);
    }


    private String getConstellation(int constellationType) {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_UNKNOWN:
                return "UNKNOWN";
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_SBAS:
                return "SBAS";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "GLONASS";
            case GnssStatus.CONSTELLATION_QZSS:
                return "QZSS";
            case GnssStatus.CONSTELLATION_BEIDOU:
                return "BEIDOU";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "GALILEO";
            case GnssStatus.CONSTELLATION_IRNSS:
                return "IRNSS";
            default:
                return Integer.toString(constellationType);
        }
    }

}
