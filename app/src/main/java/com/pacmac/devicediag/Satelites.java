package com.pacmac.devicediag;

/**
 * Created by pacmac on 6/27/2015.
 */

// ACTIVE SATELITES STORAGE
public class Satelites {
    int id, pnr;
    float snr, azimuth, elevation;

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
}
