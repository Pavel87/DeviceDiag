package com.pacmac.devinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class GPSModel extends ViewModel {


    private MutableLiveData<List<Satelites>> satellitesLiveData = new MutableLiveData<>();

    private GPSLocationInfoObject gpsLocationInfoObject = new GPSLocationInfoObject();

    private MutableLiveData<GPSLocationInfoObject> locationInfoObjectLiveData = new MutableLiveData<>();


    public LiveData<List<Satelites>> getSatellites() {
        return satellitesLiveData;
    }

    public void updateSatellites(List<Satelites> satellites) {
        satellitesLiveData.postValue(satellites);
    }


    public LiveData<GPSLocationInfoObject> getUpToDateLocationInfo() {
        return locationInfoObjectLiveData;
    }

    public GPSLocationInfoObject getGpsLocationInfoObject() {
        return gpsLocationInfoObject;
    }


    public void updateGPSInfoLiveData() {
        locationInfoObjectLiveData.postValue(this.gpsLocationInfoObject);
    }



    class GPSLocationInfoObject {

        private String longitudeS = "";
        private String latitudeS = "";
        private String altitudeS = "";
        private String speedS = "";
        private String accuracyS = "";
        private String bearingS = "";
        private String lastFix = "Waiting...";
        private String timeToFix = "";
        private String gpsInfo = "Unknown";
        private int satelliteCount = 0;


        public void setLongitudeS(String longitudeS) {
            this.longitudeS = longitudeS;
        }

        public void setLatitudeS(String latitudeS) {
            this.latitudeS = latitudeS;
        }

        public void setAltitudeS(String altitudeS) {
            this.altitudeS = altitudeS;
        }

        public void setSpeedS(String speedS) {
            this.speedS = speedS;
        }

        public void setAccuracyS(String accuracyS) {
            this.accuracyS = accuracyS;
        }

        public void setBearingS(String bearingS) {
            this.bearingS = bearingS;
        }

        public void setLastFix(String lastFix) {
            this.lastFix = lastFix;
        }

        public void setSatelliteCount(int satelliteCount) {
            this.satelliteCount = satelliteCount;
        }

        public void setTimeToFix(String timeToFix) {
            this.timeToFix = timeToFix;
        }

        public void setGpsInfo(String gpsInfo) {
            this.gpsInfo = gpsInfo;
        }

        public String getLongitudeS() {
            return longitudeS;
        }

        public String getLatitudeS() {
            return latitudeS;
        }

        public String getAltitudeS() {
            return altitudeS;
        }

        public String getSpeedS() {
            return speedS;
        }

        public String getAccuracyS() {
            return accuracyS;
        }

        public String getBearingS() {
            return bearingS;
        }

        public String getLastFix() {
            return lastFix;
        }

        public int getSatelliteCount() {
            return satelliteCount;
        }

        public String getTimeToFix() {
            return timeToFix;
        }

        public String getGpsInfo() {
            return gpsInfo;
        }
    }
}
