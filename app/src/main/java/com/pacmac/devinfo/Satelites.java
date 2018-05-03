package com.pacmac.devinfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pacmac on 6/27/2015.
 */

// ACTIVE SATELITES STORAGE
public class Satelites implements Parcelable {
    private int id, pnr;
    private float snr, azimuth, elevation;

    public static final Creator<Satelites> CREATOR = new Creator<Satelites>() {
        @Override
        public Satelites createFromParcel(Parcel in) {
            return new Satelites(in);
        }

        @Override
        public Satelites[] newArray(int size) {
            return new Satelites[size];
        }
    };

    public int getID(){
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

    public Satelites(int id, float snr, int pnr, float azimuth, float elevation) {

        this.id = id;
        this.snr = snr;
        this.pnr = pnr;
        this.azimuth = azimuth;
        this.elevation = elevation;
    }

    // constructor for parcelable

    public Satelites(Parcel source){
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

}
