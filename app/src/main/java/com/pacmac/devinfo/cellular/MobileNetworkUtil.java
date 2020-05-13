package com.pacmac.devinfo.cellular;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthNr;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.Utility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileNetworkUtil {

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<UIObject> getCarrierConfig(TelephonyManager telephonyManager) {
        PersistableBundle persistableBundle = telephonyManager.getCarrierConfig();

        List<UIObject> list = new ArrayList<>();

        if (persistableBundle != null && persistableBundle.size() > 0) {

            for (String key : persistableBundle.keySet()) {
                String prettyKey = key.replace("_", " ").toUpperCase();

                Object data = persistableBundle.get(key);

                if (data instanceof int[]) {
                    StringBuilder temp = new StringBuilder();
                    for (int i : (int[]) data) {
                        temp.append(i);
                        temp.append(",");
                    }
                    if (temp.length() > 0) {
                        data = temp.substring(0, temp.length() - 1);
                    } else {
                        data = "";
                    }
                }

                if (data instanceof String[]) {
                    StringBuilder temp = new StringBuilder();
                    for (String s : (String[]) data) {
                        temp.append(s);
                        temp.append(",");
                    }
                    if (temp.length() > 0) {
                        data = temp.substring(0, temp.length() - 1);
                    } else {
                        data = "";
                    }
                }
                list.add(new UIObject(prettyKey, String.valueOf(data)));
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
            return new UIObject(context.getString(R.string.sim_count), String.format(Locale.ENGLISH, "%d", slotCount));
        } else {
            return new UIObject(context.getString(R.string.sim_count), String.format(Locale.ENGLISH, "%d", context.getResources().getString(R.string.error)));
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
        return new UIObject(context.getString(R.string.phone_radio), type);
    }


    public static UIObject getDeviceSoftwareVersion(Context context, TelephonyManager telephonyManager) {
        @SuppressLint("MissingPermission") String swVersion = telephonyManager.getDeviceSoftwareVersion();
        return new UIObject(context.getString(R.string.sw_version), swVersion != null ?
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
        return new UIObject(context.getString(R.string.real_time_text), telephonyManager.isRttSupported() ?
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
        return new UIObject(context.getString(R.string.sms_service), telephonyManager.isSmsCapable() ?
                context.getResources().getString(R.string.supported) : context.getResources().getString(R.string.not_supported));
    }

    @TargetApi(22)
    public static UIObject isVoiceCapable(Context context, TelephonyManager telephonyManager) {
        return new UIObject(context.getString(R.string.voice_capable), telephonyManager.isVoiceCapable() ?
                context.getString(R.string.yes_string) : context.getString(R.string.no_string));

    }

    @TargetApi(26)
    public static UIObject isConcurrentVoiceAndDataSupported(Context context, TelephonyManager telephonyManager) {
        return new UIObject(context.getString(R.string.concurrent_voice_support), telephonyManager.isConcurrentVoiceAndDataSupported() ?
                context.getResources().getString(R.string.supported) : context.getResources().getString(R.string.not_supported));
    }

    /**
     * whether phone can be used around the world
     **/

    @TargetApi(23)
    public static UIObject isWorldPhone(Context context, TelephonyManager telephonyManager) {
        return new UIObject(context.getString(R.string.is_world_phone), telephonyManager.isWorldPhone() ?
                context.getString(R.string.yes_string) : context.getString(R.string.no_string));
    }
    
    @TargetApi(29)
    public static UIObject isMultiSIMSupported(Context context, TelephonyManager telephonyManager) {
        return new UIObject(context.getString(R.string.multi_sim_support),
                getMultiSIMSupport(context, telephonyManager.isMultiSimSupported()));
    }

    private static String getMultiSIMSupport(Context context, int state) {
        switch (state) {
            case TelephonyManager.MULTISIM_ALLOWED:
            return context.getString(R.string.ms_supported);
            case TelephonyManager.MULTISIM_NOT_SUPPORTED_BY_HARDWARE:
            return context.getString(R.string.restricted_by_hw);
            case TelephonyManager.MULTISIM_NOT_SUPPORTED_BY_CARRIER:
            return context.getString(R.string.restricted_by_carrier);
            default:
            return context.getResources().getString(R.string.not_available_info);
        }
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
                spn = subscriptionInfo.getDisplayName() != null ? subscriptionInfo.getDisplayName().toString() : null;
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
            return new UIObject(context.getResources().getString(R.string.sim_serial_number),
                    context.getResources().getString(R.string.not_available_info));
        }
        @SuppressLint("MissingPermission")
        SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
        if (subscriptionInfo == null) {
            return new UIObject(context.getResources().getString(R.string.sim_serial_number),
                    context.getResources().getString(R.string.not_available_info));
        } else {
            return new UIObject(context.getResources().getString(R.string.sim_serial_number), subscriptionInfo.getIccId());
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

        try {
            if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                phoneNumber = telephonyManager.getVoiceMailNumber();
            } else {
                SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                if (subscriptionManager == null) {
                    return new UIObject(context.getResources().getString(R.string.voicemail_number),
                            context.getResources().getString(R.string.not_available_info));
                }
                @SuppressLint("MissingPermission")
                SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
                if (subscriptionInfo == null) {
                    return new UIObject(context.getResources().getString(R.string.voicemail_number),
                            context.getResources().getString(R.string.not_available_info));
                } else {
                    phoneNumber = getOutput(telephonyManager, "getVoiceMailNumber", subscriptionInfo.getSubscriptionId());
                }
            }
            if (phoneNumber == null || phoneNumber.length() == 0) {
                phoneNumber = context.getResources().getString(R.string.not_available_info);
            }
        } catch (Exception e){
            phoneNumber = context.getResources().getString(R.string.error);
        }
        return new UIObject(context.getResources().getString(R.string.voicemail_number), phoneNumber);
    }

    // ONLY CDMA PHONE
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static UIObject getManufacturerCode(Context context, TelephonyManager telephonyManager, int slot) {
        String manufacturerCode = telephonyManager.getManufacturerCode(slot);
        return new UIObject(context.getResources().getString(R.string.manufacturer_code),
                (manufacturerCode != null) ? manufacturerCode : context.getResources().getString(R.string.not_available_info));
    }

    // ONLY GSM PHONE
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
    public static UIObject getGroupIdLevel(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        String groupIdLevel1 = null;
        if (!isMultiSIM || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            groupIdLevel1 = telephonyManager.getGroupIdLevel1();
            return new UIObject(context.getString(R.string.group_id_level1), groupIdLevel1 == null ? context.getResources().
                    getString(R.string.not_available_info) : groupIdLevel1);
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.group_id_level1),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.group_id_level1),
                        context.getResources().getString(R.string.not_available_info));
            }

            groupIdLevel1 = getOutput(telephonyManager, "getGroupIdLevel1", subscriptionInfo.getSubscriptionId());
            return new UIObject(context.getString(R.string.group_id_level1), groupIdLevel1 == null ? context.getResources().
                    getString(R.string.not_available_info) : groupIdLevel1);
        }
    }


    public static UIObject getSimState(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
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
                stateString = context.getString(R.string.sim_state_unknown);
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                stateString = context.getString(R.string.no_sim_inserted);
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                stateString = context.getString(R.string.sim_locked_pin);
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                stateString = context.getString(R.string.sim_locked_puk);
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                stateString = context.getString(R.string.network_locked);
                break;
            case TelephonyManager.SIM_STATE_READY:
                stateString = context.getString(R.string.sim_ready);
                break;
            case 6:
                stateString = context.getString(R.string.not_ready);
                break;
            case 7:
                stateString = context.getString(R.string.sim_disabled);
                break;
            case 8:
                stateString = context.getString(R.string.sim_io_error);
                break;
            case 9:
                stateString = context.getString(R.string.sim_restricted);
                break;
            case 10:
                stateString = context.getString(R.string.sim_loaded);
                break;
            case 11:
                stateString = context.getString(R.string.sim_present);
                break;
            default:
                stateString = context.getString(R.string.default_sim_state) + state;
        }
        return new UIObject(context.getString(R.string.sim_state), stateString);
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

            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.sim_serial_number),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.sim_serial_number),
                        context.getResources().getString(R.string.not_available_info));
            }

            simSerialNumber = getOutput(telephonyManager, "getSimSerialNumber", subscriptionInfo.getSubscriptionId());
            if (simSerialNumber == null) {
                simSerialNumber = context.getResources().getString(R.string.not_available_info);
            }
        }
        return new UIObject(context.getResources().getString(R.string.sim_serial_number), simSerialNumber);
    }

    @SuppressLint("MissingPermission")
    public static UIObject getIMSI(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        String imsi;
        if (!isMultiSIM || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            imsi = telephonyManager.getSubscriberId();
            return new UIObject(context.getResources().getString(R.string.imsi), imsi);
        } else {

            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.imsi),
                        context.getResources().getString(R.string.not_available_info));
            }
            @SuppressLint("MissingPermission")
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.imsi),
                        context.getResources().getString(R.string.not_available_info));
            }

            imsi = getOutput(telephonyManager, "getSubscriberId", subscriptionInfo.getSubscriptionId());
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

        String mccmnc = null;

        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            mccmnc = telephonyManager.getNetworkOperator();
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

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                mccmnc = t.getNetworkOperator();
            } else {
                mccmnc = getOutput(telephonyManager, "getNetworkOperatorForPhone", slotID);
            }
        }
        if (mccmnc != null && mccmnc.length() > 3) {
            return new UIObject(context.getResources().getString(R.string.mcc), mccmnc.substring(0, 3));
        } else {
            return new UIObject(context.getResources().getString(R.string.mcc),
                    context.getResources().getString(R.string.not_available_info));
        }
    }

    @SuppressLint("MissingPermission")
    public static UIObject getMNC(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {

        String mccmnc = null;
        if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            mccmnc = telephonyManager.getNetworkOperator();
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
                mccmnc = t.getNetworkOperator();
            } else {
                mccmnc = getOutput(telephonyManager, "getNetworkOperatorForPhone", slotID);
            }
        }

        if (mccmnc != null && mccmnc.length() > 3) {
            return new UIObject(context.getResources().getString(R.string.mnc), mccmnc.substring(3));
        } else {
            return new UIObject(context.getResources().getString(R.string.mnc),
                    context.getResources().getString(R.string.not_available_info));
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
                spn = getOutput(telephonyManager, "getNetworkOperatorName", slotID);
            }

        }
        if (spn == null || spn.length() == 0) {
            spn = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.spn), spn);
    }


    @SuppressLint("MissingPermission")
    public static UIObject getNetworkSPN2(Context context, TelephonyManager telephonyManager,
                                          ServiceState serviceState, int slotID) {
        String spn = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
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

            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            spn = t.getServiceState().getOperatorAlphaLong();

        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            spn = telephonyManager.getServiceState().getOperatorAlphaLong();
        } else if (serviceState != null) {
            spn = serviceState.getOperatorAlphaLong();
        }

        if (spn == null || spn.length() == 0) {
            spn = context.getResources().getString(R.string.not_available_info);
        }
        return new UIObject(context.getResources().getString(R.string.spn), spn);
    }

    @SuppressLint("MissingPermission")
    public static UIObject getMCC2(Context context, TelephonyManager telephonyManager,
                                   ServiceState serviceState, int slotID) {
        String mccmnc = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
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

            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            mccmnc = t.getServiceState().getOperatorNumeric();

        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            mccmnc = telephonyManager.getServiceState().getOperatorNumeric();
        } else if (serviceState != null) {
            mccmnc = serviceState.getOperatorNumeric();
        }

        if (mccmnc != null && mccmnc.length() > 3) {
            return new UIObject(context.getResources().getString(R.string.mcc), mccmnc.substring(0, 3));
        } else {
            return new UIObject(context.getResources().getString(R.string.mcc),
                    context.getResources().getString(R.string.not_available_info));
        }
    }

    @SuppressLint("MissingPermission")
    public static UIObject getMNC2(Context context, TelephonyManager telephonyManager,
                                   ServiceState serviceState, int slotID) {
        String mccmnc = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
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

            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            mccmnc = t.getServiceState().getOperatorNumeric();

        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            mccmnc = telephonyManager.getServiceState().getOperatorNumeric();
        } else if (serviceState != null) {
            mccmnc = serviceState.getOperatorNumeric();
        }

        if (mccmnc != null && mccmnc.length() > 3) {
            return new UIObject(context.getResources().getString(R.string.mnc), mccmnc.substring(3));
        } else {
            return new UIObject(context.getResources().getString(R.string.mnc),
                    context.getResources().getString(R.string.not_available_info));
        }
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
                String output = getOutput(telephonyManager, "getNetworkType", slotID);
                if (output != null) {
                    type = Integer.parseInt(output);
                }
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
                String output = getOutput(telephonyManager, "getVoiceNetworkType", slotID);
                if (output != null) {
                    type = Integer.parseInt(output);
                }
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
                String output = getOutput(telephonyManager, "getDataNetworkType", slotID);
                if (output != null) {
                    type = Integer.parseInt(output);
                }
            }
        }
        return new UIObject(context.getResources().getString(R.string.data_network_type), getNetworkTypeString(type));
    }

    @SuppressLint("MissingPermission")
    public static UIObject getDataState(Context context, TelephonyManager telephonyManager) {
        int state = telephonyManager.getDataState();
        return new UIObject(context.getResources().getString(R.string.data_state), getDataStateString(state));
    }

    @SuppressLint("MissingPermission")
    public static UIObject getDataActivity(Context context, TelephonyManager telephonyManager) {
        int state = telephonyManager.getDataActivity();
        return new UIObject(context.getResources().getString(R.string.data_activity), getDataActivityString(state));
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
                String output = getOutput(telephonyManager, "getNetworkType", slotID);
                if (output != null) {
                    type = Integer.parseInt(output);
                }
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
                countryCode = getOutput(telephonyManager, "getNetworkCountryIso", slotID);
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
                try {
                    String output = getOutput(telephonyManager, "getDataEnabled", slotID);
                    if (output != null) {
                        isDataEnabled = Boolean.parseBoolean(output);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            isDataRoamingEnabled = t.isDataRoamingEnabled();
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
        ServiceState state = null;
        if (!isMultiSIM) {
            state = telephonyManager.getServiceState();
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
                state = t.getServiceState();
            }
        }

        if (state == null) {
            return new UIObject(context.getResources().getString(R.string.service_state),
                    context.getResources().getString(R.string.not_available_info));
        }

        return new UIObject(context.getResources().getString(R.string.service_state), getVoiceServiceState(state.getState()));
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

            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            duplexMode = t.getServiceState().getDuplexMode();
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

    private static String getDataStateString(int value) {
        switch (value) {
            case TelephonyManager.DATA_DISCONNECTED:
                return "Disconnected";
            case TelephonyManager.DATA_CONNECTING:
                return "Connecting";
            case TelephonyManager.DATA_CONNECTED:
                return "Connected";
            case TelephonyManager.DATA_SUSPENDED:
                return "Suspended";
        }
        return "Unknown";
    }

    private static String getDataActivityString(int value) {
        switch (value) {
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                return "DORMANT";
            case TelephonyManager.DATA_ACTIVITY_IN:
                return "RECEIVING";
            case TelephonyManager.DATA_ACTIVITY_OUT:
                return "TRANSMITTING";
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                return "TRANSMITTING & RECEIVING";
        }
        return "NONE";
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

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    public static void get5GStatus(Context context, TelephonyManager telephonyManager, List<UIObject> list, int slotID, boolean isMultiSIM) {
        ServiceState s = null;
        if (isMultiSIM) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                if (subscriptionManager == null) {
                    return;
                }
                SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
                if (subscriptionInfo == null) {
                    return;
                }

                TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
                s = t.getServiceState();
            }
        } else {
            s = telephonyManager.getServiceState();
        }

        list.add(new UIObject(context.getResources().getString(R.string.ends_status), getENDCStatus(s, context)));
        list.add(new UIObject(context.getResources().getString(R.string.nr_status), get5GStatus(s, context)));
        list.add(new UIObject(context.getResources().getString(R.string.nr_frequency), getNRFrequency(s, context)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public static UIObject getRejectCause(Context context, TelephonyManager telephonyManager, int slotID, boolean isMultiSIM) {
        ServiceState s = null;

        if (isMultiSIM) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subscriptionManager == null) {
                return new UIObject(context.getResources().getString(R.string.cs_reject_cause),
                        context.getResources().getString(R.string.not_available_info));
            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID);
            if (subscriptionInfo == null) {
                return new UIObject(context.getResources().getString(R.string.cs_reject_cause),
                        context.getResources().getString(R.string.not_available_info));
            }

            TelephonyManager t = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            s = t.getServiceState();

        } else {
            s = telephonyManager.getServiceState();
        }

        try {
            final String p1 = "domain=CS[^/]*?rejectCause=(.\\w*)";
            final String p2 = "domain=CS[^/]*?reasonForDenial=(.\\w*)";
            Pattern pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(s.toString());

            if (!m.find(0)) {
                pattern = Pattern.compile(p2, Pattern.CASE_INSENSITIVE);
                m = pattern.matcher(s.toString());
            }

            if (m.find(0)) {
                String rejectCauseAsString = m.group(1);

                if (rejectCauseAsString != null) {
                    return new UIObject(context.getResources().getString(R.string.cs_reject_cause),
                            getRejectReasonString(Integer.parseInt(rejectCauseAsString)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UIObject(context.getResources().getString(R.string.cs_reject_cause),
                context.getResources().getString(R.string.not_available_info));
    }


    private static String getNRFrequency(ServiceState serviceState, Context context) {

        try {
            for (Method method : serviceState.getClass().getDeclaredMethods()) {
                if (method.getName().equals("getNrFrequencyRange")) {
                    method.setAccessible(true);
                    final Integer freq = (Integer) method.invoke(serviceState, new Object[0]);
                    if (freq != null) {
                        switch (freq) {
                            case 1:
                                return "Below 1GHz";
                            case 2:
                                return "1GHz - 3GHz";
                            case 3:
                                return "3GHz - 6GHz";
                            case 4:
                                return "millimeter Wave";
                        }
                        return context.getResources().getString(R.string.not_available_info);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parseNRFreqRangeFromString(serviceState.toString(), context);
    }

    private static String parseNRFreqRangeFromString(String serviceStateString, Context context) {
        if (serviceStateString != null) {
            try {
                final String p1 = "mNrFrequencyRange=(-?[0-9])";
                Pattern pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE);
                Matcher m = pattern.matcher(serviceStateString);
                if (m.find(0)) {
                    String g1 = m.group(1);
                    if (g1 != null) {
                        int freq = Integer.parseInt(g1);

                        switch (freq) {
                            case 1:
                                return "Below 1GHz";
                            case 2:
                                return "1GHz - 3GHz";
                            case 3:
                                return "3GHz - 6GHz";
                            case 4:
                                return "Millimeter Wave";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return context.getResources().getString(R.string.not_available_info);
    }

    /**
     * @param serviceState
     * @return nr state as integer.
     */
    private static String get5GStatus(ServiceState serviceState, Context context) {
        try {
            for (Method method : serviceState.getClass().getDeclaredMethods()) {
                // getnrstat will cover all known cases getNrStatus and getNrState
                if (method.getName().toLowerCase().contains("getnrstat")) {
                    method.setAccessible(true);
                    final Integer invoke = (Integer) method.invoke(serviceState, new Object[0]);
                    if (invoke != null) {
                        switch (invoke) {
                            case 0:
                                return "NONE";
                            case 1:
                                return "RESTRICTED";
                            case 2:
                                return "NOT RESTRICTED";
                            case 3:
                                return "CONNECTED";
                        }
                        return context.getResources().getString(R.string.not_available_info);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // WILL ATTEMPT TO PARSE STRING AND SEARCH FOR KNOWN PATTERNS
        return parseNRStatusFromString(serviceState.toString(), context);
    }

    private static String parseNRStatusFromString(String serviceStateString, Context context) {
        String status = context.getResources().getString(R.string.not_available_info);

        if (serviceStateString != null) {
            try {
                final String p1 = "domain=PS[^/]*?nrStat\\w+=(\\w+)";
                Pattern pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE);
                Matcher m = pattern.matcher(serviceStateString);
                if (m.find(0)) {
                    status = getNrStatusIntFromString(m.group(1), context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    private static String getNrStatusIntFromString(String statusString, Context context) {
        if (statusString == null) {
            return context.getResources().getString(R.string.not_available_info);
        }
        final String RESTRICTED = "RESTRICTED";
        final String NOT_RESTRICTED = "NOT_RESTRICTED";
        final String CONNECTED = "CONNECTED";
        switch (statusString) {
            case NOT_RESTRICTED:
                return "NOT RESTRICTED";
            case CONNECTED:
                return "CONNECTED";
            case RESTRICTED:
                return "RESTRICTED";
            default:
                return "NONE";
        }
    }

    /**
     * @param serviceState
     * @return nr state as integer.
     */
    private static String getENDCStatus(ServiceState serviceState, Context context) {
        // TODO if this throws exception in Q then we should only parse the string.
        try {
            for (Method method : serviceState.getClass().getDeclaredMethods()) {
                if (method.getName().toLowerCase().contains("getendcstat")) {
                    method.setAccessible(true);
                    final Integer invoke = (Integer) method.invoke(serviceState, new Object[0]);
                    if (invoke != null) {
                        if (invoke < 0) {
                            return context.getResources().getString(R.string.not_available_info);
                        }
                        return invoke == 1 ? "SUPPORTED" : "NOT SUPPORTED";
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // WILL ATTEMPT TO PARSE STRING AND SEARCH FOR KNOWN PATTERNS
        return parseENDCStatusFromString(serviceState.toString(), context);
    }


    private static String parseENDCStatusFromString(String serviceStateString, Context context) {
        if (serviceStateString != null) {
            try {
                final String p1 = "[^/]*?EndcStatus=(\\w+)\\s";
                final String p2 = "domain=PS[^/]*?endcAvailable\\s=\\s(\\w+)";
                final String p3 = "domain=PS[^/]*?endcAvailable=(\\w+)\\s";

                Pattern pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE);
                Matcher m = pattern.matcher(serviceStateString);

                if (!m.find(0)) {
                    pattern = Pattern.compile(p2, Pattern.CASE_INSENSITIVE);
                    m = pattern.matcher(serviceStateString);
                }
                if (!m.find(0)) {
                    pattern = Pattern.compile(p3, Pattern.CASE_INSENSITIVE);
                    m = pattern.matcher(serviceStateString);
                }
                if (m.find(0)) {
                    String g1 = m.group(1);
                    if (g1 != null) {
                        boolean result = g1.equals("1") || g1.equals("true");
                        return result ? "SUPPORTED" : "NOT SUPPORTED";
                    }
                }
            } catch (Exception e) {
            }
        }
        return context.getResources().getString(R.string.not_available_info);
    }


    private static String getRejectReasonString(int cause) {

        switch (cause) {
            case 0:
                return "General";
            case 1:
                return "Authentication Failure";
            case 2:
                return "IMSI unknown in HLR";
            case 3:
                return "Illegal MS";
            case 4:
                return "Illegal ME";
            case 5:
                return "PLMN not allowed";
            case 6:
                return "Location area not allowed";
            case 7:
                return "Roaming not allowed";
            case 8:
                return "No Suitable Cells in this Location Area";
            case 9:
                return "Network failure";
            case 10:
                return "Persistent location update reject";
            case 11:
                return "PLMN not allowed";
            case 12:
                return "Location area not allowed";
            case 13:
                return "Roaming not allowed in this Location Area";
            case 15:
                return "No Suitable Cells in this Location Area";
            case 17:
                return "Network Failure";
            case 20:
                return "MAC Failure";
            case 21:
                return "Sync Failure";
            case 22:
                return "Congestion";
            case 23:
                return "GSM Authentication unacceptable";
            case 25:
                return "Not Authorized for this CSG";
            case 32:
                return "Service option not supported";
            case 33:
                return "Requested service option not subscribed";
            case 34:
                return "Service option temporarily out of order";
            case 38:
                return "Call cannot be identified";
            case 95:
                return "Semantically incorrect message";
            case 96:
                return "Invalid mandatory information";
            case 97:
                return "Message type non-existent or not implemented";
            case 98:
                return "Message type not compatible with protocol state";
            case 99:
                return "Information element non-existent or not implemented";
            case 100:
                return "Conditional IE error";
            case 101:
                return "Message not compatible with protocol state";
            case 111:
                return "Protocol erro";
        }
        if (cause >= 48 && cause <= 63) {
            return "Retry upon entry into a new cell";
        }
        return "No Rejection Detected";
    }


    @SuppressLint("MissingPermission")
    public static List<CellInfo> getAllCellInfo(TelephonyManager telephonyManager) {
        if (telephonyManager == null)
            return null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return telephonyManager.getAllCellInfo();
            } else {
                return CellInfoFutureTask.getAllCellInfoBlocking(telephonyManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<UIObject> getCellTowerInfo(Context context, List<CellInfo> cellInfos) {
        int i = 0;
        List<UIObject> uiList = new ArrayList<>();
        for (CellInfo cell : cellInfos) {

            if (cell.isRegistered()) {
                uiList.add(new UIObject("Cell", String.valueOf(i + 1), 1));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    int cellConnectionStatus = cell.getCellConnectionStatus();
                    String status = context.getResources().getString(R.string.not_available_info);
                    switch (cellConnectionStatus) {
                        case 0:
                            status = context.getString(R.string.none);
                            break;
                        case 1:
                            status = context.getString(R.string.primary_cell);
                            break;
                        case 2:
                            status = context.getString(R.string.secondary_cell);
                            break;
                    }
                    uiList.add(new UIObject(context.getResources().getString(R.string.connection_status), status));
                }


                if (cell instanceof CellInfoLte) {

                    uiList.add(new UIObject(context.getResources().getString(R.string.cell_type), "LTE"));

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                                String.valueOf(((CellInfoLte) cell).getCellIdentity().getMccString())));
                        uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                                String.valueOf(((CellInfoLte) cell).getCellIdentity().getMncString())));
                    } else {
                        uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                                String.valueOf(((CellInfoLte) cell).getCellIdentity().getMcc())));
                        uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                                String.valueOf(((CellInfoLte) cell).getCellIdentity().getMnc())));
                    }


                    int cellId = ((CellInfoLte) cell).getCellIdentity().getCi();
                    int tac = ((CellInfoLte) cell).getCellIdentity().getTac();
                    int physCellId = ((CellInfoLte) cell).getCellIdentity().getPci();

                    uiList.add(new UIObject(context.getResources().getString(R.string.cid),
                            String.valueOf((cellId == Integer.MAX_VALUE || cellId == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : cellId)));

                    uiList.add(new UIObject(context.getResources().getString(R.string.tracking_area_code),
                            String.valueOf((tac == Integer.MAX_VALUE || tac == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : tac)));


                    uiList.add(new UIObject(context.getResources().getString(R.string.pci),
                            String.valueOf((physCellId == Integer.MAX_VALUE || physCellId == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : physCellId)));


                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        int rfcn = ((CellInfoLte) cell).getCellIdentity().getEarfcn();
                        if (rfcn != Integer.MAX_VALUE && rfcn != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.earfcn),
                                    String.valueOf((rfcn)), "kHz"));
                            uiList.add(new UIObject(context.getResources().getString(R.string.eutran_band), getMobileBandForLTE(rfcn)));
                        }
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                        int bandwidth = ((CellInfoLte) cell).getCellIdentity().getBandwidth();
                        if (bandwidth != Integer.MAX_VALUE && bandwidth != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.bandwidth),
                                    String.valueOf((bandwidth))));
                        }
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        int rssi = ((CellInfoLte) cell).getCellSignalStrength().getRssi();
                        if (rssi != Integer.MAX_VALUE && rssi != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.lte_rssi),
                                    String.format(Locale.ENGLISH, "%d", rssi), "dBm"));
                        }
                    }

                    int rsrp = ((CellInfoLte) cell).getCellSignalStrength().getDbm();
                    if (rsrp != Integer.MAX_VALUE && rsrp != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.lte_rsrp),
                                String.format(Locale.ENGLISH, "%d", rsrp), "dBm"));
                    }

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        int rsrq = ((CellInfoLte) cell).getCellSignalStrength().getRsrq();
                        if (rsrq != Integer.MAX_VALUE && rsrq != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.lte_rsrq), String.valueOf((rsrq))));
                        }

                        int rssnr = ((CellInfoLte) cell).getCellSignalStrength().getRssnr();
                        if (rssnr != Integer.MAX_VALUE && rssnr != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.lte_rssnr), String.valueOf((rssnr))));
                        }
                        int cqi = ((CellInfoLte) cell).getCellSignalStrength().getCqi();
                        if (cqi != Integer.MAX_VALUE && cqi >= 0) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.lte_cqi), String.valueOf((cqi))));
                        }
                    }
                    int ta = ((CellInfoLte) cell).getCellSignalStrength().getTimingAdvance();
                    if (ta >= 0 && ta != Integer.MAX_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.ta), String.valueOf((ta))));
                    }

                    int asuLevelINT = ((CellInfoLte) cell).getCellSignalStrength().getAsuLevel();
                    if (asuLevelINT != Integer.MAX_VALUE && asuLevelINT != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.asu), String.valueOf((asuLevelINT))));
                    }
                    int signalLevelINT = ((CellInfoLte) cell).getCellSignalStrength().getLevel();
                    if (signalLevelINT != Integer.MAX_VALUE && signalLevelINT != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.sig_level), String.valueOf((signalLevelINT))));
                    }

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cell instanceof CellInfoWcdma) {

                    uiList.add(new UIObject(context.getResources().getString(R.string.cell_type), "WCDMA"));

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                                String.valueOf(((CellInfoWcdma) cell).getCellIdentity().getMccString())));
                        uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                                String.valueOf(((CellInfoWcdma) cell).getCellIdentity().getMncString())));
                    } else {
                        uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                                String.valueOf(((CellInfoWcdma) cell).getCellIdentity().getMcc())));
                        uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                                String.valueOf(((CellInfoWcdma) cell).getCellIdentity().getMnc())));
                    }

                    int cellId = ((CellInfoWcdma) cell).getCellIdentity().getCid();
                    uiList.add(new UIObject(context.getResources().getString(R.string.cid),
                            String.valueOf((cellId == Integer.MAX_VALUE || cellId == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : cellId)));

                    int lac = ((CellInfoWcdma) cell).getCellIdentity().getLac();
                    uiList.add(new UIObject(context.getResources().getString(R.string.lac),
                            String.valueOf((lac == Integer.MAX_VALUE || lac == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : lac)));

                    int psc = ((CellInfoWcdma) cell).getCellIdentity().getPsc();
                    uiList.add(new UIObject(context.getResources().getString(R.string.psc),
                            String.valueOf((psc == Integer.MAX_VALUE || psc == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : psc)));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        int uarfcn = ((CellInfoWcdma) cell).getCellIdentity().getUarfcn();
                        uiList.add(new UIObject(context.getResources().getString(R.string.uarfcn),
                                String.valueOf((uarfcn == Integer.MAX_VALUE || uarfcn == Integer.MIN_VALUE) ?
                                        context.getResources().getString(R.string.not_available_info) : uarfcn)));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        CharSequence spn = ((CellInfoWcdma) cell).getCellIdentity().getOperatorAlphaLong();
                        uiList.add(new UIObject(context.getResources().getString(R.string.spn),
                                spn == null ? context.getResources().getString(R.string.not_available_info) : spn.toString()));
                    }

                    int rssi = ((CellInfoWcdma) cell).getCellSignalStrength().getDbm();
                    if (rssi != Integer.MAX_VALUE && rssi != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.rssi), String.valueOf((rssi)), "dBm"));
                    }
                    int asuLevelINT = ((CellInfoWcdma) cell).getCellSignalStrength().getAsuLevel();
                    if (asuLevelINT != Integer.MAX_VALUE && asuLevelINT != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.asu), String.valueOf((asuLevelINT))));
                    }

                    int signalLevelINT = ((CellInfoWcdma) cell).getCellSignalStrength().getLevel();
                    if (signalLevelINT != Integer.MAX_VALUE && signalLevelINT != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.sig_level), String.valueOf((signalLevelINT))));
                    }
                } else if (cell instanceof CellInfoGsm) {

                    uiList.add(new UIObject(context.getResources().getString(R.string.cell_type), "GSM"));

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                                String.valueOf(((CellInfoGsm) cell).getCellIdentity().getMccString())));
                        uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                                String.valueOf(((CellInfoGsm) cell).getCellIdentity().getMncString())));
                    } else {
                        uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                                String.valueOf(((CellInfoGsm) cell).getCellIdentity().getMcc())));
                        uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                                String.valueOf(((CellInfoGsm) cell).getCellIdentity().getMnc())));
                    }

                    int cellId = ((CellInfoGsm) cell).getCellIdentity().getCid();
                    uiList.add(new UIObject(context.getResources().getString(R.string.cid),
                            String.valueOf((cellId == Integer.MAX_VALUE || cellId == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : cellId)));


                    int lac = ((CellInfoGsm) cell).getCellIdentity().getLac();
                    uiList.add(new UIObject(context.getResources().getString(R.string.lac),
                            String.valueOf((lac == Integer.MAX_VALUE || lac == Integer.MIN_VALUE) ?
                                    context.getResources().getString(R.string.not_available_info) : lac)));


                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        int rfcn = ((CellInfoGsm) cell).getCellIdentity().getArfcn();
                        if (rfcn != Integer.MAX_VALUE && rfcn != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.arfcn), String.valueOf((rfcn))));
                        }
                        int bsic = ((CellInfoGsm) cell).getCellIdentity().getBsic();
                        if (bsic != Integer.MAX_VALUE && bsic != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.bsic), String.valueOf((bsic))));
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        CharSequence spn = ((CellInfoGsm) cell).getCellIdentity().getOperatorAlphaLong();
                        uiList.add(new UIObject(context.getResources().getString(R.string.spn),
                                spn == null ? context.getResources().getString(R.string.not_available_info) : spn.toString()));
                    }

                    int dbm = ((CellInfoGsm) cell).getCellSignalStrength().getDbm();
                    if (dbm != Integer.MAX_VALUE && dbm != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.rssi), String.valueOf((dbm)), "dBm"));
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        int ta = ((CellInfoGsm) cell).getCellSignalStrength().getTimingAdvance();
                        if (ta != Integer.MAX_VALUE && ta != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.ta), String.valueOf((ta))));
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        int ber = ((CellInfoGsm) cell).getCellSignalStrength().getBitErrorRate();
                        if (ber != Integer.MAX_VALUE && ber != Integer.MIN_VALUE) {
                            uiList.add(new UIObject(context.getResources().getString(R.string.ber), String.valueOf((ber))));
                        }
                    }

                    int asu = ((CellInfoGsm) cell).getCellSignalStrength().getAsuLevel();
                    if (asu != Integer.MAX_VALUE && asu != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.asu), String.valueOf((asu))));
                    }

                    int level = ((CellInfoGsm) cell).getCellSignalStrength().getLevel();
                    if (level != Integer.MAX_VALUE && level != Integer.MIN_VALUE) {
                        uiList.add(new UIObject(context.getResources().getString(R.string.sig_level), String.valueOf((level))));
                    }
                }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell instanceof CellInfoTdscdma) {
                uiList.add(new UIObject(context.getResources().getString(R.string.cell_type), "TD-SCDMA"));

                uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                        String.valueOf(((CellInfoTdscdma) cell).getCellIdentity().getMccString())));
                uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                        String.valueOf(((CellInfoTdscdma) cell).getCellIdentity().getMncString())));


                int cellId = ((CellInfoTdscdma) cell).getCellIdentity().getCid();
                uiList.add(new UIObject(context.getResources().getString(R.string.cid),
                        String.valueOf((cellId == Integer.MAX_VALUE || cellId == Integer.MIN_VALUE) ?
                                context.getResources().getString(R.string.not_available_info) : cellId)));


                int lac = ((CellInfoTdscdma) cell).getCellIdentity().getLac();
                uiList.add(new UIObject(context.getResources().getString(R.string.lac),
                        String.valueOf((lac == Integer.MAX_VALUE || lac == Integer.MIN_VALUE) ?
                                context.getResources().getString(R.string.not_available_info) : lac)));


                int cpid = ((CellInfoTdscdma) cell).getCellIdentity().getCpid();
                if (cpid != Integer.MAX_VALUE && cpid != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.cpid), String.valueOf((cpid))));
                }
                int uarfcn = ((CellInfoTdscdma) cell).getCellIdentity().getUarfcn();
                if (uarfcn != Integer.MAX_VALUE && uarfcn != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.uarfcn), String.valueOf((uarfcn))));
                }

                CharSequence spn = ((CellInfoTdscdma) cell).getCellIdentity().getOperatorAlphaLong();
                uiList.add(new UIObject(context.getResources().getString(R.string.spn),
                        spn == null ? context.getResources().getString(R.string.not_available_info) : spn.toString()));

                int dbm = ((CellInfoTdscdma) cell).getCellSignalStrength().getDbm();
                if (dbm != Integer.MAX_VALUE && dbm != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.rscp), String.valueOf((dbm)), "dBm"));
                }

                int asu = ((CellInfoTdscdma) cell).getCellSignalStrength().getAsuLevel();
                if (asu != Integer.MAX_VALUE && asu != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.asu), String.valueOf((asu))));
                }

                int level = ((CellInfoTdscdma) cell).getCellSignalStrength().getLevel();
                if (level != Integer.MAX_VALUE && level != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.sig_level), String.valueOf((level))));
                }


            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell instanceof CellInfoNr) {
                uiList.add(new UIObject(context.getResources().getString(R.string.cell_type), "5G"));

                uiList.add(new UIObject(context.getResources().getString(R.string.mcc),
                        String.valueOf(((CellIdentityNr) ((CellInfoNr) cell).getCellIdentity()).getMccString())));
                uiList.add(new UIObject(context.getResources().getString(R.string.mnc),
                        String.valueOf(((CellIdentityNr) ((CellInfoNr) cell).getCellIdentity()).getMncString())));


                long nci = ((CellIdentityNr) ((CellInfoNr) cell).getCellIdentity()).getNci();
                uiList.add(new UIObject(context.getResources().getString(R.string.nci),
                        String.valueOf((nci == Integer.MAX_VALUE || nci == Integer.MIN_VALUE) ?
                                context.getResources().getString(R.string.not_available_info) : nci)));


                int tac = ((CellIdentityNr) ((CellInfoNr) cell).getCellIdentity()).getTac();
                uiList.add(new UIObject(context.getResources().getString(R.string.tac),
                        String.valueOf((tac == Integer.MAX_VALUE || tac == Integer.MIN_VALUE) ?
                                context.getResources().getString(R.string.not_available_info) : tac)));


                int pci = ((CellIdentityNr) ((CellInfoNr) cell).getCellIdentity()).getPci();
                if (pci != Integer.MAX_VALUE && pci != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.pci), String.valueOf((pci))));
                }

                int nrarfcn = ((CellIdentityNr) ((CellInfoNr) cell).getCellIdentity()).getNrarfcn();
                if (nrarfcn != Integer.MAX_VALUE && nrarfcn != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.nrarfcn), String.valueOf((nrarfcn))));
                }

                CharSequence spn = (((CellInfoNr) cell).getCellIdentity()).getOperatorAlphaLong();
                uiList.add(new UIObject(context.getResources().getString(R.string.spn),
                        spn == null ? context.getResources().getString(R.string.not_available_info) : spn.toString()));

                CellSignalStrengthNr nrSignal = ((CellSignalStrengthNr) ((CellInfoNr) cell).getCellSignalStrength());

                int csiRSRP = nrSignal.getCsiRsrp();
                if (csiRSRP != Integer.MAX_VALUE && csiRSRP != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.csi_rsrp), String.valueOf((csiRSRP)), "dBm"));
                }
                int csiRSRQ = nrSignal.getCsiRsrq();
                if (csiRSRQ != Integer.MAX_VALUE && csiRSRQ != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.csi_rsrq), String.valueOf((csiRSRQ))));
                }
                int csiSINR = nrSignal.getCsiSinr();
                if (csiSINR != Integer.MAX_VALUE && csiSINR != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.csi_sinr), String.valueOf((csiSINR)), "dB"));
                }
                int ssRSRP = nrSignal.getSsRsrp();
                if (ssRSRP != Integer.MAX_VALUE && ssRSRP != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.ss_rsrp), String.valueOf((ssRSRP)), "dBm"));
                }
                int ssRSRQ = nrSignal.getSsRsrq();
                if (ssRSRQ != Integer.MAX_VALUE && ssRSRQ != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.ss_rsrq), String.valueOf((ssRSRQ))));
                }
                int ssSINR = nrSignal.getSsSinr();
                if (ssSINR != Integer.MAX_VALUE && ssSINR != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.ss_sinr), String.valueOf((ssSINR)), "dB"));
                }
                int asu = nrSignal.getAsuLevel();
                if (asu != Integer.MAX_VALUE && asu != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.asu), String.valueOf((asu))));
                }
                int level = nrSignal.getLevel();
                if (level != Integer.MAX_VALUE && level != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.sig_level), String.valueOf((level))));
                }

            } else if (cell instanceof CellInfoCdma) {

                uiList.add(new UIObject(context.getResources().getString(R.string.cell_type), "CDMA"));

                uiList.add(new UIObject(context.getResources().getString(R.string.networkID),
                        String.valueOf(((CellInfoCdma) cell).getCellIdentity().getNetworkId())));
                uiList.add(new UIObject(context.getResources().getString(R.string.systemID),
                        String.valueOf(((CellInfoCdma) cell).getCellIdentity().getSystemId())));
                uiList.add(new UIObject(context.getResources().getString(R.string.baseStation),
                        String.valueOf(((CellInfoCdma) cell).getCellIdentity().getBasestationId())));
                uiList.add(new UIObject(context.getResources().getString(R.string.latitude),
                        String.valueOf(((CellInfoCdma) cell).getCellIdentity().getLatitude())));
                uiList.add(new UIObject(context.getResources().getString(R.string.longitude),
                        String.valueOf(((CellInfoCdma) cell).getCellIdentity().getLongitude())));

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    CharSequence spn = (((CellInfoCdma) cell).getCellIdentity()).getOperatorAlphaLong();
                    uiList.add(new UIObject(context.getResources().getString(R.string.spn),
                            spn == null ? context.getResources().getString(R.string.not_available_info) : spn.toString()));
                }

                CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cell).getCellSignalStrength();

                int rssi = cellSignalStrengthCdma.getDbm();
                if (rssi != Integer.MAX_VALUE && rssi != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.rssi), String.valueOf((rssi)), "dBm"));
                }
                int ecioCDMA = cellSignalStrengthCdma.getCdmaEcio();
                if (ecioCDMA != Integer.MAX_VALUE && ecioCDMA != Integer.MIN_VALUE && ecioCDMA != 0) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.ecio), String.valueOf((ecioCDMA))));
                }
                int ecioEVDO = cellSignalStrengthCdma.getCdmaEcio();
                if (ecioEVDO != Integer.MAX_VALUE && ecioEVDO != Integer.MIN_VALUE && ecioEVDO != 0) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.ecio), String.valueOf((ecioEVDO))));
                }
                int evdoSnr = cellSignalStrengthCdma.getEvdoSnr();
                if (evdoSnr != Integer.MAX_VALUE && evdoSnr != Integer.MIN_VALUE && evdoSnr != 0) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.snr), String.valueOf((evdoSnr))));
                }
                int asu = cellSignalStrengthCdma.getAsuLevel();
                if (asu != Integer.MAX_VALUE && asu != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.asu), String.valueOf((asu))));
                }
                int level = cellSignalStrengthCdma.getLevel();
                if (level != Integer.MAX_VALUE && level != Integer.MIN_VALUE) {
                    uiList.add(new UIObject(context.getResources().getString(R.string.sig_level), String.valueOf((level))));
                }
            }
        }
        return uiList;
    }

    public static String getMobileBandForLTE(int earfcn) {

        if (earfcn > 67535) {
            return "ERR";
        } else if (earfcn >= 67366) {
            return "67CA"; // band 67 only for CarrierAgg
        } else if (earfcn >= 66436) {
            return "66";
        } else if (earfcn >= 65536) {
            return "65";
        } else if (earfcn > 54339) {
            return "ERR";
        } else if (earfcn >= 46790 /* inferred from the end range of BAND_45 */) {
            return "46";
        } else if (earfcn >= 46590) {
            return "45";
        } else if (earfcn >= 45590) {
            return "44";
        } else if (earfcn >= 43590) {
            return "43";
        } else if (earfcn >= 41590) {
            return "42";
        } else if (earfcn >= 39650) {
            return "41";
        } else if (earfcn >= 38650) {
            return "40";
        } else if (earfcn >= 38250) {
            return "39";
        } else if (earfcn >= 37750) {
            return "38";
        } else if (earfcn >= 37550) {
            return "37";
        } else if (earfcn >= 36950) {
            return "36";
        } else if (earfcn >= 36350) {
            return "35";
        } else if (earfcn >= 36200) {
            return "34";
        } else if (earfcn >= 36000) {
            return "33";
        } else if (earfcn > 10359) {
            return "ERR";
        } else if (earfcn >= 9920) {
            return "32CA";
        } else if (earfcn >= 9870) {
            return "31";
        } else if (earfcn >= 9770) {
            return "30";
        } else if (earfcn >= 9660) {
            return "29CA";
        } else if (earfcn >= 9210) {
            return "28";
        } else if (earfcn >= 9040) {
            return "27";
        } else if (earfcn >= 8690) {
            return "26";
        } else if (earfcn >= 8040) {
            return "25";
        } else if (earfcn >= 7700) {
            return "24";
        } else if (earfcn >= 7500) {
            return "23";
        } else if (earfcn >= 6600) {
            return "22";
        } else if (earfcn >= 6450) {
            return "21";
        } else if (earfcn >= 6150) {
            return "20";
        } else if (earfcn >= 6000) {
            return "19";
        } else if (earfcn >= 5850) {
            return "18";
        } else if (earfcn >= 5730) {
            return "17";
        } else if (earfcn > 5379) {
            return "ERR";
        } else if (earfcn >= 5280) {
            return "14";
        } else if (earfcn >= 5180) {
            return "13";
        } else if (earfcn >= 5010) {
            return "12";
        } else if (earfcn >= 4750) {
            return "11";
        } else if (earfcn >= 4150) {
            return "10";
        } else if (earfcn >= 3800) {
            return "9";
        } else if (earfcn >= 3450) {
            return "8";
        } else if (earfcn >= 2750) {
            return "7";
        } else if (earfcn >= 2650) {
            return "6";
        } else if (earfcn >= 2400) {
            return "5";
        } else if (earfcn >= 1950) {
            return "4";
        } else if (earfcn >= 1200) {
            return "3";
        } else if (earfcn >= 600) {
            return "2";
        } else if (earfcn >= 0) {
            return "1";
        }
        return "ERR";
    }
}
