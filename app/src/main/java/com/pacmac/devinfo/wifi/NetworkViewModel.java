package com.pacmac.devinfo.wifi;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class NetworkViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "network_info";

    private MutableLiveData<List<UIObject>> wifiInfo = new MutableLiveData<>();

    private List<UIObject> radioState = new ArrayList<>();
    private List<UIObject> wifiInformation = new ArrayList<>();
    private List<UIObject> dhcpInformation = new ArrayList<>();
    private List<UIObject> wifiFeatures = new ArrayList<>();


    public MutableLiveData<List<UIObject>> getWifiInfo(Context context) {
        loadWifiInfo(context);
        return wifiInfo;
    }

    public List<UIObject> getWifiInfoForExport() {

        if (wifiInfo.getValue() != null) {
            List<UIObject> list = new ArrayList<>();

            if (radioState != null && radioState.size() != 0) {
                list.addAll(radioState);
            }

            if (wifiInformation != null && wifiInformation.size() != 0) {
                list.add(new UIObject("", ""));
                list.add(new UIObject("", ""));
                list.addAll(wifiInformation);
            }
            if (dhcpInformation != null && dhcpInformation.size() != 0) {
                list.add(new UIObject("", ""));
                list.add(new UIObject("", ""));
                list.addAll(dhcpInformation);
            }
            if (wifiFeatures != null && wifiFeatures.size() != 0) {
                list.add(new UIObject("", ""));
                list.add(new UIObject("", ""));
                list.addAll(wifiFeatures);
            }
            return list;
        }
        return null;
    }

    private void loadWifiInfo(Context context) {
        List<UIObject> list = new ArrayList<>();
        boolean isLocationPermissionEnabled = Utility.checkPermission(context, Utility.ACCESS_FINE_LOCATION);
        radioState = NetworkUtils.getRadiosState(context);
        wifiInformation = NetworkUtils.getWifiInformation(context, isLocationPermissionEnabled);
        dhcpInformation = NetworkUtils.getDHCPInfo(context);
        wifiFeatures = NetworkUtils.getWifiFeatures(context);

        list.addAll(radioState);
        list.addAll(wifiInformation);
        if (dhcpInformation != null) {
            list.addAll(dhcpInformation);
        }
        list.addAll(wifiFeatures);

        wifiInfo.postValue(list);
    }

}
