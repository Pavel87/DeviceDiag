package com.pacmac.devinfo.cellular;

import android.content.Context;
import android.os.Build;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.MobileNetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class CellularViewModel extends ViewModel {

    private MutableLiveData<List<UIObject>> basicInfo = new MutableLiveData<>();
    private MutableLiveData<List<List<UIObject>>> simInfos = new MutableLiveData<>();
    private MutableLiveData<List<UIObject>> carrierConfig = new MutableLiveData<>();
    private MutableLiveData<List<UIObject>> networkInfos = new MutableLiveData<>();


    private ServiceState serviceState = null;


    public MutableLiveData<List<UIObject>> getBasicInfo(Context context) {
        loadBasicPhoneInfo(context);
        return basicInfo;
    }


    public MutableLiveData<List<List<UIObject>>> getSimInfos(Context context) {
        loadSIMInfos(context);
        return simInfos;
    }

    public MutableLiveData<List<UIObject>> getNetworkInfos(Context context) {
        loadNetworkInfo(context);
        return networkInfos;
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
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                simInfo.add(MobileNetworkUtil.getICCID(context, i));
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                simInfo.add(MobileNetworkUtil.isEmbedded(context, i));
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


    private void loadNetworkInfo(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }

        List<UIObject> list = new ArrayList<>();

        int slotCount = 0;
        try {
            slotCount = Integer.parseInt(MobileNetworkUtil.getSIMCount(context, telephonyManager).getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isMultiSIM = slotCount > 1;

        if (!isMultiSIM) {
            UIObject genObject = MobileNetworkUtil.getGeneration(context, telephonyManager, 0, isMultiSIM);
            boolean is4G = genObject.getValue().contains("4G");
            boolean is5G = genObject.getValue().contains("5G");

            // generation
            list.add(MobileNetworkUtil.getGeneration(context, telephonyManager, 0, isMultiSIM));
            // service state
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                list.add(MobileNetworkUtil.getVoiceServiceState(context, telephonyManager, 0, isMultiSIM));
            } else {
                list.add(MobileNetworkUtil.getVoiceServiceState(context, serviceState));
            }
            // network type
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                list.add(MobileNetworkUtil.getNetworkType(context, telephonyManager, 0, isMultiSIM));
            } else {
                list.add(MobileNetworkUtil.getVoiceNetworkType(context, telephonyManager, 0, isMultiSIM));
                list.add(MobileNetworkUtil.getDataNetworkType(context, telephonyManager, 0, isMultiSIM));
            }
            // SPN
            list.add(MobileNetworkUtil.getNetworkSPN(context, telephonyManager, 0, isMultiSIM));
            // mcc
            list.add(MobileNetworkUtil.getMCC(context, telephonyManager, 0, isMultiSIM));
            // mnc
            list.add(MobileNetworkUtil.getMNC(context, telephonyManager, 0, isMultiSIM));
            // network CC
            list.add(MobileNetworkUtil.getNetworkCountryCode(context, telephonyManager, 0, isMultiSIM));
            // data enabled
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                list.add(MobileNetworkUtil.isDataEnabled(context, telephonyManager, 0, isMultiSIM));
            }
            // data roaming enabled
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                list.add(MobileNetworkUtil.isDataRoamingEnabled(context, telephonyManager, 0, isMultiSIM));
            }
            // forbidden PLMNs
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                list.add(MobileNetworkUtil.getForbiddenPlmns(context, telephonyManager, 0, isMultiSIM));
            }
            // LTE CA bandwidths
            if ((is4G || is5G) && Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                list.add(MobileNetworkUtil.getLTECADuplexMode(context, telephonyManager, 0, isMultiSIM));
                list.add(MobileNetworkUtil.getLTECABandwidths(context, telephonyManager, 0, isMultiSIM));
            }


        } else {


        }


        networkInfos.postValue(list);
    }


    public void updateServiceState(ServiceState serviceState, Context context) {
        this.serviceState = serviceState;
        new Thread(() -> {
            loadNetworkInfo(context);
        }).start();
    }

    public void refreshSIMInfo(Context context) {
        new Thread(() -> {
            getBasicInfo(context);
            getSimInfos(context);
        }).start();
    }

    public void refreshNetworkInfo(Context context) {
        new Thread(() -> {
//
            getNetworkInfos(context);
        }).start();
    }

}