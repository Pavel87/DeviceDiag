package com.pacmac.devinfo.wifi;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class NetworkViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "network_info";

    private MutableLiveData<List<UIObject>> wifiInfo = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getWifiInfo(Context context) {
        loadWifiInfo(context);
        return wifiInfo;
    }

    public List<UIObject> getWifiInfoForExport() {
        return wifiInfo.getValue();
    }

    private void loadWifiInfo(Context context) {
        List<UIObject> list = new ArrayList<>();
        boolean isLocationPermissionEnabled = Utility.checkPermission(context, Utility.ACCESS_FINE_LOCATION);
        list.addAll(NetworkUtils.getRadiosState(context));
        list.addAll(NetworkUtils.getWifiInformation(context, isLocationPermissionEnabled));
        list.addAll(NetworkUtils.getWifiFeatures(context));

        wifiInfo.postValue(list);
    }

}
