package com.pacmac.devinfo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.telephony.ServiceState;
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


    @SuppressLint("NewApi")
    public static UIObject getICCID(Context context, int slotID) {

        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (subscriptionManager == null) {
            return new UIObject(context.getResources().getString(R.string.iccid),
                    context.getResources().getString(R.string.not_available_info));
        }
        @SuppressLint("MissingPermission")
        SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
        if (subscriptionInfo == null) {
            return new UIObject(context.getResources().getString(R.string.iccid),
                    context.getResources().getString(R.string.not_available_info));
        } else {
            return new UIObject(context.getResources().getString(R.string.iccid),
                    subscriptionInfo.getIccId());
        }
    }

    @SuppressLint("NewApi")
    public static UIObject isEmbedded(Context context, int slotID) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (subscriptionManager == null) {
            return new UIObject(context.getResources().getString(R.string.embedded),
                    context.getResources().getString(R.string.not_available_info));
        }
        @SuppressLint("MissingPermission")
        SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
        if (subscriptionInfo == null) {
            return new UIObject(context.getResources().getString(R.string.embedded),
                    context.getResources().getString(R.string.not_available_info));
        } else {
            return new UIObject(context.getResources().getString(R.string.embedded),
                    subscriptionInfo.isEmbedded() ?
                            context.getResources().getString(R.string.yes_string) : context.getResources().getString(R.string.no_string));
        }
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


    /**
     * NETWORK INFO
     */
    @SuppressLint("MissingPermission")
    public static UIObject getMCC(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            String mccmnc = telephonyManager.getNetworkOperator();
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
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.mcc),
                        context.getResources().getString(R.string.not_available_info));
            }

            String mcc = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                mcc = t.getNetworkOperator();
                if (mcc.length() > 3) {
                    return new UIObject(context.getResources().getString(R.string.mcc), mcc.substring(0, 3));
                } else {
                    return new UIObject(context.getResources().getString(R.string.mcc),
                            context.getResources().getString(R.string.not_available_info));
                }
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
            if (mcc == null || mcc.equals("0")) {
                mcc = context.getResources().getString(R.string.not_available_info);
            }
            return new UIObject(context.getResources().getString(R.string.mcc), mcc);
        }
    }

    @SuppressLint("MissingPermission")
    public static UIObject getMNC(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            String mccmnc = telephonyManager.getNetworkOperator();
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
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.mnc),
                        context.getResources().getString(R.string.not_available_info));
            }

            String mcc = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                mcc = t.getNetworkOperator();
                if (mcc.length() > 3) {
                    return new UIObject(context.getResources().getString(R.string.mnc), mcc.substring(3));
                } else {
                    return new UIObject(context.getResources().getString(R.string.mnc),
                            context.getResources().getString(R.string.not_available_info));
                }
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
            if (mcc == null || mcc.equals("0")) {
                mcc = context.getResources().getString(R.string.not_available_info);
            }
            return new UIObject(context.getResources().getString(R.string.mnc), mcc);
        }
    }

    @SuppressLint("MissingPermission")
    public static UIObject getNetworkSPN(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        String spn = null;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            spn = telephonyManager.getNetworkOperatorName();

        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.spn),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.spn),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                spn = t.getNetworkOperatorName();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }

        }
        if (spn == null || spn.length() == 0) {
            spn = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.spn), spn);
    }

    @SuppressLint("MissingPermission")
    public static UIObject getNetworkType(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int type = 0;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            type = telephonyManager.getNetworkType();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.network_type),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.network_type),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                type = t.getNetworkType();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }
        return new UIObject(context.getResources().getString(R.string.network_type), getNetworkTypeString(type));
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static UIObject getVoiceNetworkType(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int type = 0;
        if (!isMultiSIM) {
            type = telephonyManager.getVoiceNetworkType();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.voice_network_type),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.voice_network_type),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                type = t.getVoiceNetworkType();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }
        return new UIObject(context.getResources().getString(R.string.voice_network_type), getNetworkTypeString(type));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    public static UIObject getDataNetworkType(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int type = 0;
        if (!isMultiSIM) {
            type = telephonyManager.getDataNetworkType();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.data_network_type),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.data_network_type),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                type = t.getDataNetworkType();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }
        return new UIObject(context.getResources().getString(R.string.data_network_type), getNetworkTypeString(type));
    }


    @SuppressLint("MissingPermission")
    public static UIObject getGeneration(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int type = 0;
        if (!isMultiSIM || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                type = telephonyManager.getNetworkType();
            } else {
                type = telephonyManager.getDataNetworkType();
            }
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.network_gen),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.network_gen),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                type = t.getDataNetworkType();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }
        return new UIObject(context.getResources().getString(R.string.network_gen), getNetworkClass(context, type));
    }


    @SuppressLint("MissingPermission")
    public static UIObject getNetworkCountryCode(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        String countryCode = null;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            countryCode = telephonyManager.getNetworkCountryIso();

        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.network_country_code),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.network_country_code),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                countryCode = t.getNetworkCountryIso();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }

        }
        if (countryCode == null || countryCode.length() == 0) {
            countryCode = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.network_country_code), countryCode);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public static UIObject isDataEnabled(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        boolean isDataEnabled = false;
        if (!isMultiSIM) {
            isDataEnabled = telephonyManager.isDataEnabled();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.data_enabled),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.data_enabled),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                isDataEnabled = t.isDataEnabled();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }

        }
        return new UIObject(context.getResources().getString(R.string.data_enabled), isDataEnabled ?
                context.getResources().getString(R.string.yes_string) : context.getResources().getString(R.string.no_string));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public static UIObject isDataRoamingEnabled(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        boolean isDataRoamingEnabled = false;
        if (!isMultiSIM) {
            isDataRoamingEnabled = telephonyManager.isDataRoamingEnabled();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.data_roaming_enabled),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.data_roaming_enabled),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                isDataRoamingEnabled = t.isDataRoamingEnabled();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }

        }
        return new UIObject(context.getResources().getString(R.string.data_roaming_enabled), isDataRoamingEnabled ?
                context.getResources().getString(R.string.yes_string) : context.getResources().getString(R.string.no_string));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public static UIObject getForbiddenPlmns(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        String[] plmns = null;
        if (!isMultiSIM) {
            plmns = telephonyManager.getForbiddenPlmns();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.forbidden_plmns),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.forbidden_plmns),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                plmns = t.getForbiddenPlmns();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }
        if (plmns == null || plmns.length == 0) {
            return new UIObject(context.getResources().getString(R.string.forbidden_plmns),
                    context.getResources().getString(R.string.not_available_info));
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String plmn : plmns) {
            stringBuilder.append(plmn + "  ");
        }
        String result = stringBuilder.toString().trim().replace("  ", ", ");

        return new UIObject(context.getResources().getString(R.string.forbidden_plmns), result);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public static UIObject getVoiceServiceState(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int state = -1;
        if (!isMultiSIM) {
            state = telephonyManager.getServiceState().getState();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.service_state),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.service_state),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                state = t.getServiceState().getState();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }
        if (state == -1) {
            return new UIObject(context.getResources().getString(R.string.service_state),
                    context.getResources().getString(R.string.not_available_info));
        }

        return new UIObject(context.getResources().getString(R.string.service_state), getVoiceServiceState(state));
    }

    @SuppressLint("MissingPermission")
    public static UIObject getVoiceServiceState(Context context, ServiceState serviceState) {
        int state = -1;
        if (serviceState != null) {
            state = serviceState.getState();
        }
        if (state == -1) {
            return new UIObject(context.getResources().getString(R.string.service_state),
                    context.getResources().getString(R.string.not_available_info));
        }

        return new UIObject(context.getResources().getString(R.string.service_state), getVoiceServiceState(state));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    public static UIObject getLTECABandwidths(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int[] bandwidths = null;
        if (!isMultiSIM) {
            bandwidths = telephonyManager.getServiceState().getCellBandwidths();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.cell_bandwidths),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.cell_bandwidths),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                bandwidths = t.getServiceState().getCellBandwidths();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }

        if (bandwidths == null || bandwidths.length == 0) {
            return new UIObject(context.getResources().getString(R.string.cell_bandwidths),
                    context.getResources().getString(R.string.not_available_info));
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int bandwidth : bandwidths) {
            stringBuilder.append(bandwidth + "  ");
        }
        String result = stringBuilder.toString().trim().replace("  ", "kHz ");


        return new UIObject(context.getResources().getString(R.string.cell_bandwidths), result);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"MissingPermission", "WrongConstant"})
    public static UIObject getLTECADuplexMode(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        int duplexMode = -1;
        if (!isMultiSIM) {
            duplexMode = telephonyManager.getServiceState().getDuplexMode();
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.duplex_mode),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.duplex_mode),
                        context.getResources().getString(R.string.not_available_info));
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                duplexMode = t.getServiceState().getDuplexMode();
            } else {
//                mcc = String.format(Locale.ENGLISH, "%d", subscriptionInfo.getMcc());
            }
        }

        if (duplexMode < 0) {
            return new UIObject(context.getResources().getString(R.string.duplex_mode),
                    context.getResources().getString(R.string.not_available_info));
        }

        return new UIObject(context.getResources().getString(R.string.duplex_mode), getDuplexModeString(duplexMode));
    }


    private static String getDuplexModeString(int value) {
        switch (value) {
            case ServiceState.DUPLEX_MODE_FDD:
                return "FDD";
            case ServiceState.DUPLEX_MODE_TDD:
                return "TDD";
            default:
                return "Unknown";
        }
    }

    private static String getVoiceServiceState(int value) {
        switch (value) {
            case 0:
                return "In Service";
            case 1:
                return "Out Of Service";
            case 2:
                return "Emergency Calls Only";
            case 3:
                return "Power Off";
        }
        return "Unknown";
    }

    private static String getNetworkTypeString(int value) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            switch (value) {
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return "GSM";
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return "TD-SCDMA";
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                    return "IWLAN";
                case 19:
                    return "LTE CA";
                case 20:
                    return "5G (None Stand Alone)";
            }
        }

        switch (value) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA (Either IS95A or IS95B)";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO revision 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO revision A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO revision B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPAP";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Uknown";
        }
        return "error";
    }

    private static String getNetworkClass(Context context, int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case 19:
                return "4G";
            case 20:
                return "5G";
            default:
                return context.getResources().getString(R.string.not_available_info);
        }
    }
}
