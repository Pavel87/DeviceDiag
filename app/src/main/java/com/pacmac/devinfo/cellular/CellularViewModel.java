package com.pacmac.devinfo.cellular;

import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;

public class CellularViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "phone_cellular_info";

    private MutableLiveData<List<UIObject>> basicInfo = new MutableLiveData<>();
    private MutableLiveData<List<List<UIObject>>> simInfos = new MutableLiveData<>();
    private MutableLiveData<List<UIObject>> carrierConfig = new MutableLiveData<>();
    private MutableLiveData<List<UIObject>> networkInfos = new MutableLiveData<>();
    private MutableLiveData<List<UIObject>> cellInfos = new MutableLiveData<>();

    private MutableLiveData<String> configFilter = new MutableLiveData<>("");


    private ServiceState serviceState = null;


    public List<UIObject> getAllPhoneInfoForExport(Context context) {
        List<UIObject> fullList = new ArrayList<>();

        if (basicInfo.getValue() == null) {
            return null;
        }
        fullList.add(new UIObject(context.getString(R.string.activity_title_phone_info), ""));
        fullList.addAll(basicInfo.getValue());

        if (simInfos.getValue() == null) {
            return null;
        }
        fullList.add(new UIObject("", ""));
        for (List<UIObject> simInfo : simInfos.getValue()) {
            fullList.addAll(simInfo);
        }

        if (networkInfos.getValue() == null) {
            return null;
        }
        fullList.add(new UIObject("", ""));
        fullList.add(new UIObject(context.getString(R.string.active_network_info), ""));
        fullList.addAll(networkInfos.getValue());


        if (cellInfos.getValue() == null) {
            return null;
        }
        fullList.add(new UIObject("", ""));
        fullList.add(new UIObject(context.getString(R.string.connected_cell_info), ""));
        fullList.addAll(cellInfos.getValue());

        if (carrierConfig.getValue() == null) {
            return null;
        }
        fullList.add(new UIObject("", ""));
        fullList.add(new UIObject(context.getString(R.string.carrier_config_long), ""));
        fullList.addAll(carrierConfig.getValue());
        return fullList;
    }


    public MutableLiveData<List<UIObject>> getCellInfos(Context context) {
        new Thread(() -> loadCellInfos(context)).start();
        return cellInfos;
    }

    public MutableLiveData<List<UIObject>> getBasicInfo(Context context) {
        new Thread(() -> loadBasicPhoneInfo(context)).start();
        return basicInfo;
    }


    public MutableLiveData<List<List<UIObject>>> getSimInfos(Context context) {
        new Thread(() -> loadSIMInfos(context)).start();
        return simInfos;
    }

    public MutableLiveData<List<UIObject>> getNetworkInfos(Context context) {
        new Thread(() -> loadNetworkInfo(context)).start();
        return networkInfos;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MutableLiveData<List<UIObject>> getCarrierConfig(Context context) {
        new Thread(() -> loadCarrierConfig(context)).start();
        return carrierConfig;
    }

    public MutableLiveData<String> getConfigFilter() {
        return configFilter;
    }

    public void setConfigFilter(String query) {
        this.configFilter.postValue(query);
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
            simInfo.add(MobileNetworkUtil.getSimState(context, telephonyManager, i, isMultiSIM));
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
                boolean isCDMA = telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA;
                if (isCDMA) {
                    simInfo.add(MobileNetworkUtil.getManufacturerCode(context, telephonyManager, i));
                }
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                simInfo.add(MobileNetworkUtil.getCarrierID(context, telephonyManager, i, isMultiSIM));
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    simInfo.add(MobileNetworkUtil.getGroupIdLevel(context, telephonyManager, i, isMultiSIM));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                simInfo.add(MobileNetworkUtil.getSIMSerialNumber(context, telephonyManager, i, isMultiSIM));
            } else {
                simInfo.add(MobileNetworkUtil.getICCID(context, i));
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                simInfo.add(MobileNetworkUtil.getIMSI(context, telephonyManager, i, isMultiSIM));
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
            list.add(MobileNetworkUtil.isVoiceCapable(context, telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            list.add(MobileNetworkUtil.isConcurrentVoiceAndDataSupported(context, telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            list.add(MobileNetworkUtil.isRttSupported(context, telephonyManager));
            list.add(MobileNetworkUtil.isMultiSIMSupported(context, telephonyManager));
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            list.add(MobileNetworkUtil.isWorldPhone(context, telephonyManager));
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

        list.add(MobileNetworkUtil.getDataState(context, telephonyManager));
        list.add(MobileNetworkUtil.getDataActivity(context, telephonyManager));

        for (int i = 0; i < slotCount; i++) {

            list.add(new UIObject(context.getString(R.string.network), String.valueOf(i + 1), 1));

            UIObject genObject = MobileNetworkUtil.getGeneration(context, telephonyManager, i, isMultiSIM);
            boolean is4G = genObject.getValue().contains("4G");
            boolean is5G = genObject.getValue().contains("5G");

            // generation
            list.add(MobileNetworkUtil.getGeneration(context, telephonyManager, i, isMultiSIM));
            // service state
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                list.add(MobileNetworkUtil.getVoiceServiceState(context, telephonyManager, i, isMultiSIM));
            } else {
                list.add(MobileNetworkUtil.getVoiceServiceState(context, serviceState));
            }


            // network type
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                list.add(MobileNetworkUtil.getNetworkType(context, telephonyManager, i, isMultiSIM));
            } else {
                list.add(MobileNetworkUtil.getVoiceNetworkType(context, telephonyManager, i, isMultiSIM));
                list.add(MobileNetworkUtil.getDataNetworkType(context, telephonyManager, i, isMultiSIM));
            }

            // SPN
            list.add(MobileNetworkUtil.getNetworkSPN(context, telephonyManager, i, isMultiSIM));
//            list.add(MobileNetworkUtil.getNetworkSPN2(context, telephonyManager, serviceState, i));
            // mcc
            list.add(MobileNetworkUtil.getMCC(context, telephonyManager, i, isMultiSIM));
//            list.add(MobileNetworkUtil.getMCC2(context, telephonyManager, serviceState, i));
            // mnc
            list.add(MobileNetworkUtil.getMNC(context, telephonyManager, i, isMultiSIM));
//            list.add(MobileNetworkUtil.getMNC2(context, telephonyManager, serviceState, i));


            // network CC
            list.add(MobileNetworkUtil.getNetworkCountryCode(context, telephonyManager, i, isMultiSIM));
            // data enabled
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                list.add(MobileNetworkUtil.isDataEnabled(context, telephonyManager, i, isMultiSIM));
            }
            // data roaming enabled
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                list.add(MobileNetworkUtil.isDataRoamingEnabled(context, telephonyManager, i, isMultiSIM));
            }
            // forbidden PLMNs
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                list.add(MobileNetworkUtil.getForbiddenPlmns(context, telephonyManager, 0, isMultiSIM));
            }
            // Reject Cause for Data Network

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                list.add(MobileNetworkUtil.getRejectCause(context, telephonyManager, i, isMultiSIM));
            }

            // LTE CA bandwidths
            if ((is4G || is5G) && Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                list.add(MobileNetworkUtil.getLTECADuplexMode(context, telephonyManager, i, isMultiSIM));
                list.add(MobileNetworkUtil.getLTECABandwidths(context, telephonyManager, i, isMultiSIM));

                MobileNetworkUtil.get5GStatus(context, telephonyManager, list, i, isMultiSIM);
            }
        }
        networkInfos.postValue(list);
    }


    private void loadCellInfos(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }

        List<CellInfo> cellInfos = MobileNetworkUtil.getAllCellInfo(telephonyManager);
        if (cellInfos == null || cellInfos.size() == 0) {
            return;
        }

        this.cellInfos.postValue(MobileNetworkUtil.getCellTowerInfo(context, cellInfos));
    }


    public void updateServiceState(ServiceState serviceState, Context context) {
        this.serviceState = serviceState;
        getNetworkInfos(context);
        getCellInfos(context);
    }


    public void refreshNetworkInfo(Context context) {
        getNetworkInfos(context);
    }

    public void refreshAll(Context context) {
        getBasicInfo(context);
        getSimInfos(context);
        getNetworkInfos(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCarrierConfig(context);
        }
        getCellInfos(context);

    }

}