package com.pacmac.devinfo.gps;

import android.content.Context;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GPSViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "gps_info";
    public static final String EXPORT_NMEA_FILE_NAME = "NMEA.txt";

    private MutableLiveData<List<UIObject>> gpsInfo = new MutableLiveData<>();

    private boolean enabled = false;

    private String gpsState = "";
    private String gnssHardwareYear = "";
    private int firstFix = -1;
    private String latitude = "";
    private String longitude = "";
    private String altitude = "";
    private String speed = "";
    private String accuracy = "";
    private String bearing = "";
    private String visibleSatellites = "";

    private MutableLiveData<List<Satellites>> satellitesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> updateTimeLive = new MutableLiveData<>("--:--:--");


    /**
     * NMEA FEED
     */
    private MutableLiveData<String> message = new MutableLiveData<>();
    private String html = "";
    private final int MAX_LOG_LINES = 128;
    private int count = 0;


    public MutableLiveData<String> getMessageLive() {
        return message;
    }

    public MutableLiveData<String> getUpdateTimeLive() {
        return updateTimeLive;
    }

    public LiveData<List<Satellites>> getSatellites() {
        return satellitesLiveData;
    }

    public void updateSatellites(List<Satellites> satellites) {
        satellitesLiveData.postValue(satellites);
    }

    public MutableLiveData<List<UIObject>> getMainGPSData() {
        loadGPSdata();
        return gpsInfo;
    }

    public List<UIObject> getMainGPSDataForExport() {
        List<UIObject> list = new ArrayList<>();
        list.add(new UIObject("Location Update Time", updateTimeLive.getValue()));
        if (gpsInfo.getValue() != null) {
            list.addAll(gpsInfo.getValue());
        }

        if (satellitesLiveData.getValue() != null) {

            list.add(new UIObject("", "", 1));
            list.add(new UIObject("Satellites", "", 1));
            list.add(new UIObject("ID", "PNR,SNR,AZIMUTH,ELEVATION", 1));

            int i = 1;
            for (Satellites satellite : satellitesLiveData.getValue()) {
                list.add(new UIObject(String.valueOf(i), satellite.toString(), 1));
                i++;
            }
        }
        return list;
    }

    private void loadGPSdata() {

        List<UIObject> list = new ArrayList<>();

        list.add(new UIObject("GPS Status", gpsState));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            list.add(new UIObject("GNSS Hardware Year", gnssHardwareYear));
        }

        list.add(new UIObject("First Fix", firstFix != -1 ? getFirstFix() : "", firstFix != -1 ? getFirstFixUnit() : ""));
        list.add(new UIObject("Latitude", latitude));
        list.add(new UIObject("Longitude", longitude));
        list.add(new UIObject("Altitude", altitude));
        list.add(new UIObject("Speed", speed, speed.length() != 0 ? "km/h" : ""));
        list.add(new UIObject("Accuracy", accuracy, accuracy.length() != 0 ? "m" : ""));
        list.add(new UIObject("Bearing", bearing, accuracy.length() != 0 ? "m" : ""));
        list.add(new UIObject("Visible Satellites", visibleSatellites));

        gpsInfo.postValue(list);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setGpsState(String gpsState) {
        this.gpsState = gpsState;
    }

    public void setGnssHardwareYear(String gnssHardwareYear) {
        this.gnssHardwareYear = gnssHardwareYear;
    }

    public void setFirstFix(int firstFix) {
        this.firstFix = firstFix;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public void setVisibleSatellites(String visibleSatellites) {
        this.visibleSatellites = visibleSatellites;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTimeLive.postValue(updateTime);
    }

    public String getFirstFixUnit() {
        if (firstFix > 1000) {
            return "s";
        }
        return "ms";
    }

    public String getFirstFix() {
        if (firstFix > 1000) {
            return String.format(Locale.ENGLISH, "%.1f", (firstFix / 1000.0));
        }
        return String.valueOf(firstFix);
    }


    public void onNmeaMessage(Context context, String message, long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int milis = cal.get(Calendar.MILLISECOND);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        if (html.split("<br>").length > MAX_LOG_LINES) {
            html = html.substring(html.indexOf("<br>") + 4);
        }

        String timeDate = hour + ":" + minute + ":" + second + ":" + milis + " " + day + "/" + month + ": ";
        html = html + "<font color=\"" + context.getResources().getColor(R.color.text_primary) + "\">" + "<b>" + timeDate + "</b>" + "</font>" + message + "<br>";

        this.message.postValue(html);
    }
}