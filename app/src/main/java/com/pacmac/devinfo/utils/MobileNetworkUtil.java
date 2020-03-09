package com.pacmac.devinfo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.Utility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MobileNetworkUtil {

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<UIObject> getCarrierConfig(TelephonyManager telephonyManager) {
        PersistableBundle persistableBundle = telephonyManager.getCarrierConfig();

        List<UIObject> list = new ArrayList<>();

        if (persistableBundle != null && persistableBundle.size() > 0) {

            for (String key : persistableBundle.keySet()) {
                String prettyKey = key.replace("_", " ").toUpperCase();
                list.add(new UIObject(prettyKey, String.valueOf(persistableBundle.get(key))));
            }


        }
        return list;
    }

    public static UIObject getSIMCount(Context context, TelephonyManager telephonyManager) {
        int slotCount = -1;

        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && telephonyManager.getPhoneCount() == 0)) {
            slotCount = 0;

        } else {
            final String SIM_COUNT_QLC1 = "ro.multisim.simslotcount";
            final String SIM_COUNT_QLC2 = "ro.hw.dualsim";
            final String SIM_COUNT_MTK = "ro.telephony.sim.count";
            final String PHONE_TYPE_2 = "gsm.current.phone-type2";
            final String GSM_SIM_STATE = "gsm.sim.state";

            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    try {
                        if (Utility.getDeviceProperty(SIM_COUNT_QLC1).equals("2")
                                || Utility.getDeviceProperty(SIM_COUNT_MTK).equals("2")
                                || Utility.getDeviceProperty(GSM_SIM_STATE).contains(",")
                                || Utility.getDeviceProperty(SIM_COUNT_QLC2).equals("true")
                                || !Utility.getDeviceProperty(PHONE_TYPE_2).equals("")) {
                            slotCount = 2;
                        } else if (Utility.getDeviceProperty(SIM_COUNT_QLC1).equals("3")
                                || Utility.getDeviceProperty(SIM_COUNT_MTK).equals("3")) {
                            slotCount = 3;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    slotCount = 1;
                } else {
                    slotCount = telephonyManager.getPhoneCount();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (slotCount != -1) {
            return new UIObject("SIM Count", String.format(Locale.ENGLISH, "%d", slotCount));
        } else {
            return new UIObject("SIM Count", String.format(Locale.ENGLISH, "%d", context.getResources().getString(R.string.error)));
        }
    }

    public static UIObject getPhoneRadio(Context context, TelephonyManager telephonyManager) {
        final String type;
        switch (telephonyManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                type = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                type = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                type = "SIP";
                break;
            default:
                type = context.getResources().getString(R.string.not_available_info);
                break;
        }
        return new UIObject("Phone Radio", type);
    }


    public static UIObject getDeviceSoftwareVersion(Context context, TelephonyManager telephonyManager) {
        @SuppressLint("MissingPermission") String swVersion = telephonyManager.getDeviceSoftwareVersion();
        return new UIObject("Software Version", swVersion != null ?
                swVersion : context.getResources().getString(R.string.not_available_info));
    }

    /**
     * Determines whether the device currently supports RTT (Real-time text).
     *
     * @param telephonyManager
     * @return
     */
    @TargetApi(29)
    public static UIObject isRttSupported(Context context, TelephonyManager telephonyManager) {
        return new UIObject("Real-Time Text", telephonyManager.isRttSupported() ?
                context.getResources().getString(R.string.supported) : context.getResources().getString(R.string.not_supported));
    }

    /**
     * true if the current device supports sms service.
     * If true, this means that the device supports both sending and receiving sms via the telephony network.
     *
     * @return
     */
    @TargetApi(21)
    public static UIObject isSmsCapable(Context context, TelephonyManager telephonyManager) {
        return new UIObject("SMS Service", telephonyManager.isSmsCapable() ?
                context.getResources().getString(R.string.supported) : context.getResources().getString(R.string.not_supported));
    }

    @TargetApi(22)
    public static UIObject isVoiceCapable(TelephonyManager telephonyManager) {
        return new UIObject("Voice Capable", telephonyManager.isVoiceCapable() ? "YES" : "NO");

    }

    @TargetApi(26)
    public static UIObject isConcurrentVoiceAndDataSupported(Context context, TelephonyManager telephonyManager) {
        return new UIObject("Concurrent Voice & Data", telephonyManager.isConcurrentVoiceAndDataSupported() ?
                context.getResources().getString(R.string.supported) : context.getResources().getString(R.string.not_supported));
    }

    /**
     * whether phone can be used around the world
     **/

    @TargetApi(23)
    public static UIObject isWorldPhone(TelephonyManager telephonyManager) {
        return new UIObject("Is World Phone", telephonyManager.isWorldPhone() ? "YES" : "NO");
    }


    /**
     * SIM INFO
     */
    public static UIObject getSIMMCC(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            String mccmnc = telephonyManager.getSimOperator();
            if (mccmnc.length() > 3) {
                return new UIObject(context.getResources().getString(R.string.mcc), mccmnc.substring(0, 3));
            } else {
                return new UIObject(context.getResources().getString(R.string.mcc),
                        context.getResources().getString(R.string.not_available_info));
            }
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.mcc),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.mcc),
                        context.getResources().getString(R.string.not_available_info));
            }
            String mcc;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                mcc = subscriptionInfo.getMccString();
            } else {
                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
            if (mcc == null || mcc.equals("0")) {
                mcc = context.getResources().getString(R.string.not_available_info);
            }
            return new UIObject(context.getResources().getString(R.string.mcc), mcc);
        }
    }


    public static UIObject getSIMMNC(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            String mccmnc = telephonyManager.getSimOperator();
            if (mccmnc.length() > 3) {
                return new UIObject(context.getResources().getString(R.string.mnc), mccmnc.substring(3));
            } else {
                return new UIObject(context.getResources().getString(R.string.mnc),
                        context.getResources().getString(R.string.not_available_info));
            }
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.mnc),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.mnc),
                        context.getResources().getString(R.string.not_available_info));
            }
            String mnc;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                mnc = subscriptionInfo.getMncString();
            } else {
                mnc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMnc());
            }
            if (mnc == null || mnc.equals("0")) {
                mnc = context.getResources().getString(R.string.not_available_info);
            }
            return new UIObject(context.getResources().getString(R.string.mnc), mnc);
        }
    }

    public static UIObject getSIMServiceProviderName(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        String spn = null;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                spn = (telephonyManager.getSimSpecificCarrierIdName() != null) ? telephonyManager.getSimSpecificCarrierIdName().toString() : null;
            }
            if (spn == null) {
                spn = telephonyManager.getSimOperatorName();
            }
            if (spn == null || spn.length() == 0) {
                spn = context.getResources().getString(R.string.not_available_info);
            }
            return new UIObject(context.getResources().getString(R.string.spn), spn);

        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.spn),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.spn),
                        context.getResources().getString(R.string.not_available_info));
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                spn = (t.getSimSpecificCarrierIdName() != null) ? t.getSimSpecificCarrierIdName().toString() : null;
                if (spn == null) {
                    spn = t.getSimOperatorName();
                }
            } else {
                spn = subscriptionInfo.getCarrierName() != null ? subscriptionInfo.getCarrierName().toString() : null;
            }

            if (spn == null) {
                spn = context.getResources().getString(R.string.not_available_info);
            }
            return new UIObject(context.getResources().getString(R.string.spn), spn);
        }
    }


    public static UIObject getSIMCountryISO(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        String countryISO;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            countryISO = telephonyManager.getSimCountryIso();
            return new UIObject(context.getResources().getString(R.string.country_iso), countryISO);
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.country_iso),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.country_iso),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                countryISO = t.getSimCountryIso();
            } else {
                countryISO = subscriptionInfo.getCountryIso();
            }
        }
        if (countryISO == null || countryISO.length() == 0) {
            countryISO = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.country_iso), countryISO);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static UIObject getCarrierID(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        int carrierID;

        if (!isMultiSIM) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                carrierID = telephonyManager.getSimSpecificCarrierId();
            } else {
                carrierID = telephonyManager.getSimCarrierId();
            }
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.carrier_id),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.carrier_id),
                        context.getResources().getString(R.string.not_available_info));
            }

            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                carrierID = telephonyManager.getSimSpecificCarrierId();
            } else {
                carrierID = telephonyManager.getSimCarrierId();
            }
        }

        if (carrierID > 0) {
            return new UIObject(context.getResources().getString(R.string.carrier_id),
                    String.format(Locale.ENGLISH, "%d", carrierID));
        }
        return new UIObject(context.getResources().getString(R.string.carrier_id),
                context.getResources().getString(R.string.not_available_info));
    }

    @SuppressLint("MissingPermission")
    public static UIObject getLine1Number(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        String phoneNumber;

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = telephonyManager.getLine1Number();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.phone_number),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.phone_number),
                        context.getResources().getString(R.string.not_available_info));
            }
            phoneNumber = subscriptionInfo.getNumber();
        }
        if (phoneNumber == null || phoneNumber.length() == 0) {
            phoneNumber = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.phone_number), phoneNumber);
    }

    @SuppressLint("MissingPermission")
    public static UIObject getVoiceMailNumber(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        String phoneNumber;

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = telephonyManager.getVoiceMailNumber();
        } else {
            phoneNumber = getOutput(telephonyManager, "getVoiceMailNumber", slotID);
        }
        if (phoneNumber == null || phoneNumber.length() == 0) {
            phoneNumber = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.voicemail_number), phoneNumber);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static UIObject getManufacturerCode(Context context, TelephonyManager telephonyManager, int slot) {
        String manufacturerCode = telephonyManager.getManufacturerCode(slot);
        return new UIObject(context.getResources().getString(R.string.manufacturer_code),
                (manufacturerCode != null) ? manufacturerCode : context.getResources().getString(R.string.not_available_info));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static UIObject getTAC(Context context, TelephonyManager telephonyManager, int slot) {
        String tac = telephonyManager.getTypeAllocationCode(slot);

        return new UIObject(context.getResources().getString(R.string.tac),
                (tac != null) ? tac : context.getResources().getString(R.string.not_available_info));
    }


    @SuppressLint("MissingPermission")
    public static UIObject getIMEIOrMEID(Context context, TelephonyManager telephonyManager, int slotID) {
        String imeiOrMeid = null;
        boolean isIMEI = telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM;
        String label = telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM ?
                context.getResources().getString(R.string.imei) : context.getResources().getString(R.string.meid);

        try {
            String s = Build.VERSION.RELEASE;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                if (isIMEI) {
                    imeiOrMeid = telephonyManager.getImei(slotID);
                } else {
                    imeiOrMeid = telephonyManager.getMeid(slotID);
                }

            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    imeiOrMeid = getOutput(telephonyManager, "getDeviceId", slotID);
                } else {
                    imeiOrMeid = telephonyManager.getDeviceId();
                }
            }
            return new UIObject(label, imeiOrMeid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("MissingPermission")
    public static UIObject getGroupIdLevel(Context context, TelephonyManager telephonyManager, int slotID) {
        String groupIdLevel1 = null;
        if (slotID == 0 && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            groupIdLevel1 = telephonyManager.getGroupIdLevel1();
            return new UIObject("Group ID Level 1", groupIdLevel1 == null ? context.getResources().
                    getString(R.string.not_available_info) : groupIdLevel1);
        } else {
            groupIdLevel1 = getOutput(telephonyManager, "getGroupIdLevel1", slotID);
            return new UIObject("Group ID Level 1", groupIdLevel1 == null ? context.getResources().
                    getString(R.string.not_available_info) : groupIdLevel1);
        }
    }


    public static UIObject getSimState(TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int state = TelephonyManager.SIM_STATE_UNKNOWN;
        if (!isMultiSIM) {
            state = telephonyManager.getSimState();
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                state = telephonyManager.getSimState(slotID);
            } else {
                String simStateRaw = getOutput(telephonyManager, "getSimState", slotID);
                if (simStateRaw != null) {
                    state = Integer.parseInt(simStateRaw);
                }
            }
        }

        final String stateString;
        switch (state) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                stateString = "Unknown SIM State";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                stateString = "No SIM Inserted";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                stateString = "SIM Locked - PIN Code Required";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                stateString = "SIM Locked - PUK Code Required";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                stateString = "Network Locked ";
                break;
            case TelephonyManager.SIM_STATE_READY:
                stateString = "Ready";
                break;
            case 6:
                stateString = "Not Ready";
                break;
            case 7:
                stateString = "Disabled";
                break;
            case 8:
                stateString = "IO Error - Present but Faulty";
                break;
            case 9:
                stateString = "Restricted";
                break;
            case 10:
                stateString = "Loaded";
                break;
            case 11:
                stateString = "Present";
                break;
            default:
                stateString = "State" + state;
        }
        return new UIObject("SIM State", stateString);
    }


    /**
     * MAX API 27
     *
     * @param context
     * @param telephonyManager
     * @param slotID
     * @param isMultiSIM
     * @return
     */
    @SuppressLint("MissingPermission")
    public static UIObject getSIMSerialNumber(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        String simSerialNumber;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            simSerialNumber = telephonyManager.getSimSerialNumber();
            return new UIObject(context.getResources().getString(R.string.sim_serial_number), simSerialNumber);

        } else {
            simSerialNumber = getOutput(telephonyManager, "getSimSerialNumber", slotID);
            if (simSerialNumber == null) {
                simSerialNumber = context.getResources().getString(R.string.not_available_info);
            }
        }
        return new UIObject(context.getResources().getString(R.string.sim_serial_number), simSerialNumber);
    }

    @SuppressLint("MissingPermission")
    public static UIObject getIMSI(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        String imsi;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            imsi = telephonyManager.getSubscriberId();
            return new UIObject(context.getResources().getString(R.string.imsi), imsi);
        } else {
            imsi = getOutput(telephonyManager, "getSimSerialNumber", slotID);
            if (imsi == null) {
                imsi = context.getResources().getString(R.string.not_available_info);
            }
        }
        return new UIObject(context.getResources().getString(R.string.imsi), imsi);
    }


    private static String getOutput(TelephonyManager telephonyManager, String methodName,
                                    int slotId) {
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephonyManager.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
                if (name.equals(methodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals("int")) {
                        reflectionMethod = name;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephonyManager, reflectionMethod, slotId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    private static String getOpByReflection(TelephonyManager telephony,
                                            String predictedMethodName, int slotID) throws Exception {
        String result = null;
        Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

        Class<?>[] parameter = new Class[1];
        parameter[0] = int.class;
        Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

        Object ob_phone;
        Object[] obParameter = new Object[1];
        obParameter[0] = slotID;
        if (getSimID != null) {
            ob_phone = getSimID.invoke(telephony, obParameter);
            if (ob_phone != null) {
                result = ob_phone.toString();
            }
        }
        return result;
    }


}
