package com.pacmac.devinfo.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.MobileNetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class CellularViewModel extends ViewModel {

    private MutableLiveData<List<UIObject>> basicInfo = new MutableLiveData<>();
    private MutableLiveData<List<List<UIObject>>> simInfos = new MutableLiveData<>();
    private MutableLiveData<List<UIObject>> carrierConfig = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getBasicInfo(Context context) {
        loadBasicPhoneInfo(context);
        return basicInfo;
    }


    public MutableLiveData<List<List<UIObject>>> getSimInfos(Context context) {
        loadSIMInfos(context);
        return simInfos;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MutableLiveData<List<UIObject>> getCarrierConfig(Context context) {
        loadCarrierConfig(context);
        return carrierConfig;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCarrierConfig(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            carrierConfig.postValue(MobileNetworkUtil.getCarrierConfig(telephonyManager));
        }

    }

    private void loadSIMInfos(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }

        List<List<UIObject>> list = new ArrayList<>();
        int slotCount = 0;
        try {
            slotCount = Integer.parseInt(MobileNetworkUtil.getSIMCount(context, telephonyManager).getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isMultiSIM = slotCount > 1;

        for (int i = 0; i < slotCount; i++) {
            List<UIObject> simInfo = new ArrayList<>();
            simInfo.add(MobileNetworkUtil.getSimState(telephonyManager, i, isMultiSIM));
            simInfo.add(MobileNetworkUtil.getLine1Number(context, telephonyManager, i, isMultiSIM));
            simInfo.add(MobileNetworkUtil.getVoiceMailNumber(context, telephonyManager, i, isMultiSIM));
            simInfo.add(MobileNetworkUtil.getSIMServiceProviderName(context, telephonyManager, i, isMultiSIM));
            simInfo.add(MobileNetworkUtil.getSIMMCC(context, telephonyManager, i, isMultiSIM));
            simInfo.add(MobileNetworkUtil.getSIMMNC(context, telephonyManager, i, isMultiSIM));
            simInfo.add(MobileNetworkUtil.getSIMCountryISO(context, telephonyManager, i, isMultiSIM));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && !Build.VERSION.RELEASE.contains("10")) {
                simInfo.add(MobileNetworkUtil.getIMEIOrMEID(context, telephonyManager, i));
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                simInfo.add(MobileNetworkUtil.getTAC(context, telephonyManager, i));
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                simInfo.add(MobileNetworkUtil.getManufacturerCode(context, telephonyManager, i));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                simInfo.add(MobileNetworkUtil.getCarrierID(context, telephonyManager, i, isMultiSIM));
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                simInfo.add(MobileNetworkUtil.getGroupIdLevel(context, telephonyManager, i));
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                simInfo.add(MobileNetworkUtil.getSIMSerialNumber(context, telephonyManager, i, isMultiSIM));
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                simInfo.add(MobileNetworkUtil.getIMSI(context, telephonyManager, i, isMultiSIM));
            }

            list.add(simInfo);
        }
        simInfos.postValue(list);
    }


    private void loadBasicPhoneInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }

        List<UIObject> list = new ArrayList<>();


        list.add(MobileNetworkUtil.getSIMCount(context, telephonyManager));
        list.add(MobileNetworkUtil.getPhoneRadio(context, telephonyManager));
        list.add(MobileNetworkUtil.getDeviceSoftwareVersion(context, telephonyManager));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            list.add(MobileNetworkUtil.isSmsCapable(context, telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            list.add(MobileNetworkUtil.isVoiceCapable(telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            list.add(MobileNetworkUtil.isConcurrentVoiceAndDataSupported(context, telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            list.add(MobileNetworkUtil.isRttSupported(context, telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            list.add(MobileNetworkUtil.isWorldPhone(telephonyManager));
        }
        basicInfo.postValue(list);
    }
}