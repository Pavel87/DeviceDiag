package com.pacmac.devinfo.gps;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Created by pacmac on 6/27/2015.
 */

// ACTIVE SATELITES STORAGE
public class Satellites implements Parcelable {
    private int id, pnr;
    private float snr, azimuth, elevation;

    public static final Creator<Satellites> CREATOR = new Creator<Satellites>() {
        @Override
        public Satellites createFromParcel(Parcel in) {
            return new Satellites(in);
        }

        @Override
        public Satellites[] newArray(int size) {
            return new Satellites[size];
        }
    };

    public int getID() {
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

    public Satellites(int id, float snr, int pnr, float azimuth, float elevation) {

        this.id = id;
        this.snr = snr;
        this.pnr = pnr;
        this.azimuth = azimuth;
        this.elevation = elevation;
    }

    // constructor for parcelable

    public Satellites(Parcel source) {
        this.id = source.readInt();
        this.pnr = source.readInt();
        this.snr = source.readFloat();
        this.azimuth = source.readFloat();
        this.elevation = source.readFloat();
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
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%d,%.1f,%.1f,%.1f", pnr, snr, azimuth, elevation);
    }
}
