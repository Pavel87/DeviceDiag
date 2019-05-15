package com.pacmac.devinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

public class SIMInfo extends AppCompatActivity {

    private TextView simState, simState2;
    private TextView serialN, serialN2;
    private TextView imeiNumber, imeiNumber2;
    private TextView networkName, networkName2;
    private TextView mcc, mnc, cid, lac, pciOrPsc, rfChannel, bsicOrBandwidth, networkClass, signalLevel, asuLevel;
    private TextView spnName, spnName2;
    private TextView simCountryCode, simCountryCode2;
    private TextView mccSpn, mccSpn2;
    private TextView mncSpn, mncSpn2;
    private TextView phoneNumber, phoneNumber2;
    private TextView providerCountry, providerCountry2;
    private TextView networkType, networkType2;
    private TextView sigStrength2G, sigStrength3G, timingAdvance2G, signalStrengthCDMA, ecio, snr;
    private TextView meid, meid2;
    private LinearLayout signalStrengthTempView;


    private TextView simCount;
    private TextView imsiNumber;
    private TextView dataState;
    private TextView dataActivity;
    private TextView nai;
    private TextView phoneRadio;

    private TextView mccLabel, mncLabel, cidLabel, lacLabel, pciAndPscLabel, rfChannelLabel, bwOrBSICLabel;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean isSIMInside = false;

    private ShareActionProvider mShareActionProvider;
    private final Handler mHandler = new Handler();
    private Runnable timer;

    boolean isLocPermissionEnabled = true;
    boolean isPermissionEnabled = true;
    private static final String PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    private static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sim_info_new);


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager.getPhoneType() == 0) {
            Toast.makeText(getApplicationContext(), "There is no WAN radio available", Toast.LENGTH_LONG).show();
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), PHONE_PERMISSION);
            isLocPermissionEnabled = Utility.checkPermission(getApplicationContext(), LOCATION_PERMISSION);
            if (!isPermissionEnabled) {
                Utility.displayExplanationForPermission(this, getResources().getString(R.string.phone_permission_msg), PHONE_PERMISSION);
            }
        }

        signalStrengthTempView = findViewById(R.id.signalStrength2GView);

        simCount = (TextView) findViewById(R.id.simCount);
        imsiNumber = (TextView) findViewById(R.id.imsiNumber);
        dataActivity = (TextView) findViewById(R.id.dataActivity);
        dataState = (TextView) findViewById(R.id.dataState);
        phoneRadio = (TextView) findViewById(R.id.phoneRadio);
        nai = (TextView) findViewById(R.id.nai);

        mccLabel = findViewById(R.id.mccLabel);
        mncLabel = findViewById(R.id.mncLabel);
        cidLabel = findViewById(R.id.cidLabel);
        lacLabel = findViewById(R.id.lacLabel);
        pciAndPscLabel = findViewById(R.id.pciAndPscLabel);
        rfChannelLabel = findViewById(R.id.rfChannelLabel);
        bwOrBSICLabel = findViewById(R.id.bwOrBSICLabel);
        networkClass = findViewById(R.id.networkClass);
        signalLevel = findViewById(R.id.signalLevel);
        asuLevel = findViewById(R.id.asuLevel);
        sigStrength2G = findViewById(R.id.sigStrength2G);
        timingAdvance2G = findViewById(R.id.timingAdvance2G);
        sigStrength3G = findViewById(R.id.sigStrength3G);
        signalStrengthCDMA = findViewById(R.id.signalStrengthCDMA);
        ecio = findViewById(R.id.ecio);
        snr = findViewById(R.id.snr);


        simState = findViewById(R.id.simState);
        serialN = findViewById(R.id.serialN);
        imeiNumber = findViewById(R.id.imei);
        networkName = findViewById(R.id.networkName);
        mcc = findViewById(R.id.mccCode);
        mnc = findViewById(R.id.mncCode);
        cid = findViewById(R.id.cid);
        lac = findViewById(R.id.lac);
        pciOrPsc = findViewById(R.id.pciAndPsc);
        rfChannel = findViewById(R.id.rfChannel);
        bsicOrBandwidth = findViewById(R.id.bsicOrBandwidth);


        spnName = findViewById(R.id.operatorName);
        simCountryCode = findViewById(R.id.simCountryCode);
        mccSpn = findViewById(R.id.mccSPN);
        mncSpn = findViewById(R.id.mncSPN);
        phoneNumber = findViewById(R.id.phoneNumber);
        providerCountry = findViewById(R.id.networkCountry);
        networkType = findViewById(R.id.networkType);
        meid = findViewById(R.id.meid);

        simState2 = findViewById(R.id.simState2);
        serialN2 = findViewById(R.id.serialN2);
        imeiNumber2 = findViewById(R.id.imei2);
        spnName2 = findViewById(R.id.operatorName2);
        simCountryCode2 = findViewById(R.id.simCountryCode2);
        mccSpn2 = findViewById(R.id.mccSPN2);
        mncSpn2 = findViewById(R.id.mncSPN2);
        phoneNumber2 = findViewById(R.id.phoneNumber2);
        meid2 = findViewById(R.id.meid2);


//        mRecyclerView = findViewById(R.id.cellList);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new CellInfoAdapter(new ArrayList<CellInfo>());
//        mRecyclerView.setAdapter(mAdapter);


        simCount.setText(String.valueOf(isMultiSIM() ? 2 : 1));

        if (isMultiSIM()) {
            findViewById(R.id.simView2).setVisibility(View.VISIBLE);
        }

        if (isPermissionEnabled) {
            // update view with phone data
            updateData();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateData() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }


        if (isLocPermissionEnabled) {
//            ((CellInfoAdapter) mAdapter).updateData(telephonyManager.getAllCellInfo());
            getCellTower(telephonyManager);
        } else {
            Utility.requestPermissions(this, LOCATION_PERMISSION);
        }


        /** MULTISIM    API 22+ **/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && isMultiSIM()) {

            SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> list = subscriptionManager.getActiveSubscriptionInfoList();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                findViewById(R.id.viewMEID).setVisibility(View.VISIBLE);
                findViewById(R.id.viewMEID2).setVisibility(View.VISIBLE);
            }

            if (list != null && list.size() > 0) {
                for (SubscriptionInfo info : list) {
                    if (info.getSimSlotIndex() == 0) {
                        simState.setText(getSimState(telephonyManager, 0, false));
                        mccSpn.setText("" + info.getMcc());
                        mncSpn.setText("" + info.getMnc());
                        spnName.setText(info.getCarrierName() != null ? info.getCarrierName() : "N/A");
                        phoneNumber.setText(info.getNumber() != null ? info.getNumber() : "N/A");
                        serialN.setText(info.getIccId() != null ? info.getIccId() : "N/A");
                        String imei = getImei(telephonyManager, 0, false);
                        imeiNumber.setText(imei != null ? imei : "N/A");
                        simCountryCode.setText(info.getCountryIso() != null ? info.getCountryIso().toUpperCase() : "N/A");
                        meid.setText(getMEID(telephonyManager, 0));

                    } else if (info.getSimSlotIndex() == 1) {
                        simState2.setText(getSimState(telephonyManager, 1, false));
                        mccSpn2.setText("" + info.getMcc());
                        mncSpn2.setText("" + info.getMnc());
                        spnName2.setText(info.getCarrierName() != null ? info.getCarrierName() : "N/A");
                        phoneNumber2.setText(info.getNumber() != null ? info.getNumber() : "N/A");
                        serialN2.setText(info.getIccId() != null ? info.getIccId() : "N/A");
                        String imei2 = getImei(telephonyManager, 1, false);
                        imeiNumber2.setText(imei2 != null ? imei2 : "N/A");
                        simCountryCode2.setText(info.getCountryIso() != null ? info.getCountryIso().toUpperCase() : "N/A");
                        meid2.setText(getMEID(telephonyManager, 1));
                    }
                }
            }
            /** SINGLE SIM **/
        } else if (!isMultiSIM()) {
            simState.setText(getSimState(telephonyManager, 0, true));
            String imei = getImei(telephonyManager, 0, true);

            String simSerialNumber = getApplicationContext().getResources().getString(R.string.not_available_info);
            String simMCC = getApplicationContext().getResources().getString(R.string.not_available_info);
            String simMNC = getApplicationContext().getResources().getString(R.string.not_available_info);
            String simServiceProvider = getApplicationContext().getResources().getString(R.string.not_available_info);
            String simCountry = getApplicationContext().getResources().getString(R.string.not_available_info);
            String networkServiceProvider = getApplicationContext().getResources().getString(R.string.not_available_info);
            String networkMCC = getApplicationContext().getResources().getString(R.string.not_available_info);
            String networkMNC = getApplicationContext().getResources().getString(R.string.not_available_info);
            String networkCountryCode = getApplicationContext().getResources().getString(R.string.not_available_info);
            String networkTypeString = getApplicationContext().getResources().getString(R.string.not_available_info);
            String telNumber = getApplicationContext().getResources().getString(R.string.not_available_info);

            if (imei != "" && imei != null) {

                imeiNumber.setText(imei);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    findViewById(R.id.viewMEID).setVisibility(View.VISIBLE);
                    String meID = telephonyManager.getMeid();
                    meid.setText(meID != null ? meID : getApplicationContext().getResources().getString(R.string.not_available_info));
                }


                if (isSIMInside) {
                    if (telephonyManager.getSimOperator().length() > 3) {
                        simMCC = "" + telephonyManager.getSimOperator().substring(0, 3);
                        simMNC = "" + telephonyManager.getSimOperator().substring(3);
                    }
                    simCountry = "" + telephonyManager.getSimCountryIso().toUpperCase();
                    simServiceProvider = telephonyManager.getSimOperatorName();


                    networkServiceProvider = telephonyManager.getNetworkOperatorName() != null ? telephonyManager.getNetworkOperatorName() : getApplicationContext().getResources().getString(R.string.not_available_info);
                    networkCountryCode = telephonyManager.getNetworkCountryIso() != null ? telephonyManager.getNetworkCountryIso().toUpperCase() : getApplicationContext().getResources().getString(R.string.not_available_info);


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            simSerialNumber = telephonyManager.getSimSerialNumber() != null ? telephonyManager.getSimSerialNumber() : simSerialNumber;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        telNumber = telephonyManager.getLine1Number();
                    }
                    networkTypeString = networkType(telephonyManager.getNetworkType());

                    spnName.setText(simServiceProvider);
                    simCountryCode.setText(simCountry);
                    mccSpn.setText(simMCC);
                    mncSpn.setText(simMNC);
                    serialN.setText(simSerialNumber);
                    phoneNumber.setText(telNumber);

                    networkName.setText(networkServiceProvider);
                    providerCountry.setText(networkCountryCode);
                    networkType.setText(networkTypeString);
                }
            } else if (!isPermissionEnabled) {
                imeiNumber.setTextColor(Color.RED);
                imeiNumber.setText("No WAN DETECTED");
                simState.setTextColor(Color.RED);
                simState.setText("NO SIM CARD DETECTED");
            } else {
                imeiNumber.setTextColor(Color.RED);
                imeiNumber.setText("No WAN DETECTED");
                simState.setTextColor(Color.RED);
                simState.setText("NO SIM CARD DETECTED");
            }

            /** MULTISIM pre API 22 **/
        } else {


        }


        // GENERAL INFORMATION

        if (isLocPermissionEnabled) {
            dataActivity.setText(dataActivityQuery(telephonyManager.getDataActivity()));
            dataState.setText(dataConnState(telephonyManager.getDataState()));
            phoneRadio.setText(getPhoneRadio(telephonyManager.getPhoneType()));
            imsiNumber.setText(telephonyManager.getSubscriberId());

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                findViewById(R.id.viewNAI).setVisibility(View.VISIBLE);
                String naiString = telephonyManager.getNai();
                nai.setText(naiString != null ? naiString : getApplicationContext().getResources().getString(R.string.not_available_info));
            }
        }


        updateShareIntent();

    }

    private String getSimState(TelephonyManager telephonyManager, int slotID, boolean isSingleSIM) {

        int state = TelephonyManager.SIM_STATE_UNKNOWN;
        if (isSingleSIM) {
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

        switch (state) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                isSIMInside = false;
                simState.setTextColor(Color.RED);
                return "Unknown - SIM might be in transition between states";

            case TelephonyManager.SIM_STATE_ABSENT:
                isSIMInside = false;
                simState.setTextColor(Color.RED);
                return "No SIM available in device";


            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                isSIMInside = true;
                simState.setTextColor(Color.RED);
                return "SIM Locked - requires a SIM PIN";


            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                isSIMInside = true;
                simState.setTextColor(Color.RED);
                return "SIM Locked - requires a SIM PUK";


            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                isSIMInside = true;
                simState.setTextColor(Color.RED);
                return "SIM Locked - requires a network PIN";

            case TelephonyManager.SIM_STATE_READY:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM is ready";
            case 6:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM not ready";
            case 7:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM permanently disabled";
            case 8:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM Permanently Disabled";

            case 9:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM Permanently Disabled";


            case 10:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM Loaded";


            case 11:
                isSIMInside = true;
                simState.setTextColor(Color.BLACK);
                return "SIM Present";
        }
        isSIMInside = false;
        return "error";
    }

    private String networkType(int value) {
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

    private String dataActivityQuery(int value) {
        switch (value) {

            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                return "Connection active but physical link is down";

            case TelephonyManager.DATA_ACTIVITY_IN:
                return "Receiving IP PPP traffic";

            case TelephonyManager.DATA_ACTIVITY_INOUT:
                return "Sending and receiving IP PPP traffic";

            case TelephonyManager.DATA_ACTIVITY_NONE:
                return "No Traffic";

            case TelephonyManager.DATA_ACTIVITY_OUT:
                return "Sending IP PPP traffic";
        }
        return "error";
    }

    private String dataConnState(int value) {
        switch (value) {
            case TelephonyManager.DATA_CONNECTED:
                dataState.setTextColor(Color.GREEN);
                return "Connected:\nIP traffic should be available";
            case TelephonyManager.DATA_DISCONNECTED:
                dataState.setTextColor(Color.RED);
                return "Disconnected:\nIP traffic not available";
            case TelephonyManager.DATA_CONNECTING:
                return "Setting up data connection";
            case TelephonyManager.DATA_SUSPENDED:
                dataState.setTextColor(Color.RED);
                return "Data Suspended";
        }
        return "error";
    }

    private String getPhoneRadio(int value) {
        switch (value) {

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";
            case TelephonyManager.PHONE_TYPE_NONE:
                return "No phone radio";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";

        }
        return "error";
    }


    // SHARE VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shareTextEmpty));
        return shareIntent;
    }

    private Intent createShareIntent(StringBuilder sb) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Build.MODEL + "\t-\t"
                + getResources().getString(R.string.title_activity_siminfo));
        return shareIntent;
    }


    private void updateShareIntent() {

        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_siminfo));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");
        //body
        sb.append("SIM Serial Number: " + serialN.getText().toString());
        sb.append("\n");
        sb.append("IMEI: " + imeiNumber.getText().toString());
        sb.append("\n");
        sb.append("IMSI: " + imsiNumber.getText().toString());
        sb.append("\n");
        sb.append("Phone Radio Type: " + phoneRadio.getText().toString());
        sb.append("\n");
        sb.append("SIM Provider Name: " + spnName.getText().toString());
        sb.append("\n");
        sb.append("SIM MCC: " + mccSpn.getText().toString());
        sb.append("\n");
        sb.append("SIM MNC: " + mncSpn.getText().toString());
        sb.append("\n");
        sb.append("Network Provider Name: " + networkName.getText().toString());
        sb.append("\n");
        sb.append("Network MCC: " + mcc.getText().toString());
        sb.append("\n");
        sb.append("Network MNC: " + mnc.getText().toString());
        sb.append("\n");
        sb.append("Network Provider Country: " + providerCountry.getText().toString());
        sb.append("\n");
        sb.append("Network Type: " + networkType.getText().toString());
        sb.append("\n");
        sb.append("SIM Number: " + phoneNumber.getText().toString());
        sb.append("\n");
        sb.append("Data State: " + dataState.getText().toString());
        sb.append("\n");
        sb.append("Data Activity: " + dataActivity.getText().toString());
        sb.append("\n\n");

        sb.append(getResources().getString(R.string.shareTextTitle1));
        setShareIntent(createShareIntent(sb));
    }

    @Override
    public void onResume() {
        super.onResume();

        timer = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 2500);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (isPermissionEnabled) {
                            updateData();
                        }
                    }
                });
            }
        };
        mHandler.postDelayed(timer, 3000);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeCallbacks(timer);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), PHONE_PERMISSION);
            isLocPermissionEnabled = Utility.checkPermission(getApplicationContext(), LOCATION_PERMISSION);
        }
    }


    private static final String SIM_COUNT_QLC1 = "ro.multisim.simslotcount";
    private static final String SIM_COUNT_QLC2 = "ro.hw.dualsim";
    private static final String SIM_COUNT_MTK = "ro.telephony.sim.count";
    private static final String PHONE_TYPE_2 = "gsm.current.phone-type2";
    private static final String GSM_SIM_STATE = "gsm.sim.state";

    private boolean isMultiSIM() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                try {
                    if (Utility.getDeviceProperty(SIM_COUNT_QLC1).equals("2")
                            || Utility.getDeviceProperty(SIM_COUNT_MTK).equals("2")
                            || Utility.getDeviceProperty(GSM_SIM_STATE).contains(",")
                            || Utility.getDeviceProperty(SIM_COUNT_QLC2).equals("true")
                            || !Utility.getDeviceProperty(PHONE_TYPE_2).equals("")
                    ) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    int count = telephonyManager.getPhoneCount();
                    return count == 2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static String getOutput(TelephonyManager telephonyManager, String methodName,
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

    protected static String getOpByReflection(TelephonyManager telephony,
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

    @SuppressLint("MissingPermission")
    private String getImei(TelephonyManager telephonyManager, int slotID, boolean isSingleSim) {
        String imei = "";
        try {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P || Build.VERSION.RELEASE.contains("Q")) {
                imei = getResources().getString(R.string.not_available_info);
            } else if (isSingleSim) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    imei = telephonyManager.getImei();
                } else {
                    imei = telephonyManager.getDeviceId();
                }
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    imei = telephonyManager.getImei(slotID);
                } else {
                    imei = getOutput(telephonyManager, "getDeviceId", slotID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    @SuppressLint("MissingPermission")
    private String getMEID(TelephonyManager telephonyManager, int slotID) {
        String meid = "N/A";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                findViewById(R.id.viewMEID).setVisibility(View.VISIBLE);
                findViewById(R.id.viewMEID2).setVisibility(View.VISIBLE);
                meid = telephonyManager.getMeid(slotID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meid;
    }






    @SuppressLint("MissingPermission")
    private String getCellTower(TelephonyManager telephonyManager) {
        List<CellInfo> cellInfo = telephonyManager.getAllCellInfo();
        networkClass.setText(getNetworkClass(getApplicationContext(), telephonyManager.getNetworkType()));

        String currentCellInfo = "Unknown";
        // getAllCellInfo may return null in old phones!
        if (cellInfo != null && cellInfo.size() > 0) {
            for (CellInfo cell : cellInfo) {


                if (cell instanceof CellInfoGsm && cell.isRegistered()) {
                    int networkMCC = ((CellInfoGsm) cell).getCellIdentity().getMcc();
                    int networkMNC = ((CellInfoGsm) cell).getCellIdentity().getMnc();
                    int LAC = ((CellInfoGsm) cell).getCellIdentity().getLac();
                    int cID = ((CellInfoGsm) cell).getCellIdentity().getCid();
                    int psc = ((CellInfoGsm) cell).getCellIdentity().getPsc();
                    int rfcn = Integer.MAX_VALUE;
                    int bsic = Integer.MAX_VALUE;
                    String rfcnString = "Unknown";
                    String bsicString = "Unknown";
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        rfcn = ((CellInfoGsm) cell).getCellIdentity().getArfcn();
                        if (rfcn != Integer.MAX_VALUE) {
                            rfcnString = String.valueOf(rfcn);
                        }
                        bsic = ((CellInfoGsm) cell).getCellIdentity().getBsic();
                        if (bsic != Integer.MAX_VALUE) {
                            bsicString = String.valueOf(bsic);
                        }
                    }

                    int dbm = ((CellInfoGsm) cell).getCellSignalStrength().getDbm();
                    int ta = Integer.MAX_VALUE;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        ta = ((CellInfoGsm) cell).getCellSignalStrength().getTimingAdvance();
                    }


                    set2GLabels();
                    mcc.setText(String.valueOf(networkMCC));
                    mnc.setText(String.valueOf(networkMNC));
                    cid.setText(String.valueOf(cID));
                    lac.setText(String.valueOf(LAC));
                    pciOrPsc.setText(cleanValue(psc));
                    bsicOrBandwidth.setText(bsicString);
                    rfChannel.setText(rfcnString);
                    sigStrength2G.setText(cleanValue(dbm));
                    timingAdvance2G.setText(cleanValue(ta));

                    int asuLevelINT = ((CellInfoGsm) cell).getCellSignalStrength().getAsuLevel();
                    int signalLevelINT = ((CellInfoGsm) cell).getCellSignalStrength().getLevel();
                    signalLevel.setText(String.valueOf(signalLevelINT));
                    asuLevel.setText(String.valueOf(asuLevelINT));

                } else if (cell instanceof CellInfoCdma && cell.isRegistered()) {
                    int networkMCC = ((CellInfoCdma) cell).getCellIdentity().getNetworkId();
                    int networkMNC = ((CellInfoCdma) cell).getCellIdentity().getSystemId();
                    int baseStationId = ((CellInfoCdma) cell).getCellIdentity().getBasestationId();
                    int dbm = ((CellInfoCdma) cell).getCellSignalStrength().getDbm();
                    int ecioValue = ((CellInfoCdma) cell).getCellSignalStrength().getCdmaEcio();
                    int snrValue = Integer.MAX_VALUE;
                    if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0
                            || telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A
                            || telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                        ecioValue = ((CellInfoCdma) cell).getCellSignalStrength().getEvdoEcio();
                        snrValue = ((CellInfoCdma) cell).getCellSignalStrength().getEvdoSnr();
                    }

                    setCDMALabels();
                    mcc.setText(String.valueOf(networkMCC));
                    mnc.setText(String.valueOf(networkMNC));
                    cid.setText(String.valueOf(baseStationId));
                    lac.setText(String.valueOf(((CellInfoCdma) cell).getCellIdentity().getLatitude()));
                    pciOrPsc.setText(String.valueOf(((CellInfoCdma) cell).getCellIdentity().getLongitude()));

                    int asuLevelINT = ((CellInfoCdma) cell).getCellSignalStrength().getAsuLevel();
                    int signalLevelINT = ((CellInfoCdma) cell).getCellSignalStrength().getLevel();
                    signalLevel.setText(String.valueOf(signalLevelINT));
                    asuLevel.setText(String.valueOf(asuLevelINT));
                    signalStrengthCDMA.setText(cleanValue(dbm));
                    ecio.setText(cleanValue(ecioValue));
                    snr.setText(cleanValue(snrValue));

                } else if (Build.VERSION.SDK_INT > 17 && cell instanceof CellInfoWcdma && cell.isRegistered()) {   // FROM API 18+ supported
                    int networkMCC = ((CellInfoWcdma) cell).getCellIdentity().getMcc();
                    int networkMNC = ((CellInfoWcdma) cell).getCellIdentity().getMnc();
                    int LAC = ((CellInfoWcdma) cell).getCellIdentity().getLac();
                    int cellId = ((CellInfoWcdma) cell).getCellIdentity().getCid();
                    int psc = ((CellInfoWcdma) cell).getCellIdentity().getPsc();
                    int rfcn = -1;
                    String rfcnString = "Unknown";
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        rfcn = ((CellInfoWcdma) cell).getCellIdentity().getUarfcn();
                        if (rfcn != Integer.MAX_VALUE) {
                            rfcnString = String.valueOf(rfcn);
                        }
                    }
                    int dbm = ((CellInfoWcdma) cell).getCellSignalStrength().getDbm();

                    set3GLabels();
                    mcc.setText(String.valueOf(networkMCC));
                    mnc.setText(String.valueOf(networkMNC));
                    cid.setText(String.valueOf(cellId));
                    lac.setText(String.valueOf(LAC));
                    pciOrPsc.setText(String.valueOf(psc));
                    rfChannel.setText(rfcnString);

                    int asuLevelINT = ((CellInfoWcdma) cell).getCellSignalStrength().getAsuLevel();
                    int signalLevelINT = ((CellInfoWcdma) cell).getCellSignalStrength().getLevel();
                    signalLevel.setText(cleanValue(signalLevelINT));
                    asuLevel.setText(cleanValue(asuLevelINT));
                    sigStrength3G.setText(cleanValue(dbm));


                } else if (cell instanceof CellInfoLte && cell.isRegistered()) {
                    int networkMCC = ((CellInfoLte) cell).getCellIdentity().getMcc();
                    int networkMNC = ((CellInfoLte) cell).getCellIdentity().getMnc();
                    int cellId = ((CellInfoLte) cell).getCellIdentity().getCi();
                    int tac = ((CellInfoLte) cell).getCellIdentity().getTac();
                    int physCellId = ((CellInfoLte) cell).getCellIdentity().getPci();
                    int rfcn = -1;
                    int bandwidth = -1;
                    String rfcnString = "Unknown";
                    String bandwidthString = "Unknown";
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        rfcn = ((CellInfoLte) cell).getCellIdentity().getEarfcn();
                        if (rfcn != Integer.MAX_VALUE) {
                            rfcnString = String.valueOf(rfcn);
                        }
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                        bandwidth = ((CellInfoLte) cell).getCellIdentity().getBandwidth();
                        if (bandwidth != Integer.MAX_VALUE) {
                            bandwidthString = String.valueOf(bandwidth);
                        }
                    }

                    set4GLabels();
                    mcc.setText(String.valueOf(networkMCC));
                    mnc.setText(String.valueOf(networkMNC));
                    cid.setText(String.valueOf(cellId));
                    lac.setText(String.valueOf(tac));
                    pciOrPsc.setText(String.valueOf(physCellId));
                    rfChannel.setText(rfcnString);
                    bsicOrBandwidth.setText(bandwidthString);

                    int rsrp = Integer.MAX_VALUE;
                    int rsrq = Integer.MAX_VALUE;
                    int rssnr = Integer.MAX_VALUE;
                    int cqi = Integer.MAX_VALUE;
                    int ta = Integer.MAX_VALUE;

                    String rsrqString = "N/A";
                    String rssnrString = "N/A";
                    String cqiString = "N/A";
                    String taString = "N/A";

                    TextView rsrpLTE = findViewById(R.id.lte_rsrp);
                    TextView rsrqLTE = findViewById(R.id.lte_rsrq);
                    TextView rssnrLTE = findViewById(R.id.lte_rssnr);
                    TextView cqiLTE = findViewById(R.id.lte_cqi);
                    TextView taLTE = findViewById(R.id.lte_ta);

                    rsrp = ((CellInfoLte) cell).getCellSignalStrength().getDbm();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        rsrq = ((CellInfoLte) cell).getCellSignalStrength().getRsrq();
                        if (rsrq != Integer.MAX_VALUE) {
                            rsrqString = String.valueOf(rsrq);
                        }
                        rssnr = ((CellInfoLte) cell).getCellSignalStrength().getRssnr();
                        if (rssnr != Integer.MAX_VALUE) {
                            rssnrString = String.valueOf(rssnr);
                        }
                        cqi = ((CellInfoLte) cell).getCellSignalStrength().getCqi();
                        if (cqi >= 0 && cqi != Integer.MAX_VALUE) {
                            cqiString = String.valueOf(cqi);
                        }
                    }
                    ta = ((CellInfoLte) cell).getCellSignalStrength().getTimingAdvance();
                    if (ta >= 0 && ta != Integer.MAX_VALUE) {
                        taString = String.valueOf(ta);
                    }

                    int asuLevelINT = ((CellInfoLte) cell).getCellSignalStrength().getAsuLevel();
                    int signalLevelINT = ((CellInfoLte) cell).getCellSignalStrength().getLevel();
                    signalLevel.setText(String.valueOf(signalLevelINT));
                    asuLevel.setText(String.valueOf(asuLevelINT));

                    rsrpLTE.setText(cleanValue(rsrp));
                    rsrqLTE.setText(rsrqString);
                    rssnrLTE.setText(rssnrString);
                    cqiLTE.setText(cqiString);
                    taLTE.setText(taString);
                }
            }
        } else {
            try {
                CellLocation cell = telephonyManager.getCellLocation();
                if (cell instanceof GsmCellLocation) {
                    int lac = ((GsmCellLocation) cell).getLac();
                    int cid = ((GsmCellLocation) cell).getCid();
                    int psc = ((GsmCellLocation) cell).getPsc();
                    currentCellInfo =
                            "Type: GSM"
                                    + "Cell ID: " + cid + "\n"
                                    + "LAC: " + lac + "\n"
                                    + "PSC: " + psc + "\n"
                    ;
                } else if (cell instanceof CdmaCellLocation) {
                    int baseStationId = ((CdmaCellLocation) cell).getBaseStationId();
                    int sysId = ((CdmaCellLocation) cell).getSystemId();
                    int netId = ((CdmaCellLocation) cell).getNetworkId();
                    int longitude = ((CdmaCellLocation) cell).getBaseStationLongitude();
                    int latitude = ((CdmaCellLocation) cell).getBaseStationLatitude();
                    currentCellInfo = "Type: CDMA\n"
                            + "Base Station ID: " + baseStationId + "\n"
                            + "Network ID: " + netId + "\n"
                            + "System ID: " + sysId + "\n"
                            + "Tower Longitude: " + longitude + "\n"
                            + "Tower Latitude: " + latitude;
                } else {
                    currentCellInfo = getResources().getString(R.string.not_available_info);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return currentCellInfo;
    }


    private String cleanValue(int value) {

        if (value == Integer.MAX_VALUE) {
            return "N/A";
        }
        return String.valueOf(value);
    }


    private void set2GLabels() {
        signalStrengthTempView.setVisibility(View.GONE);
        signalStrengthTempView = findViewById(R.id.signalStrength2GView);
        signalStrengthTempView.setVisibility(View.VISIBLE);

        findViewById(R.id.lacView).setVisibility(View.VISIBLE);
        findViewById(R.id.pciOrPscView).setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            findViewById(R.id.bwOrBSICView).setVisibility(View.VISIBLE);
            findViewById(R.id.rfChannelView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.bwOrBSICView).setVisibility(View.GONE);
            findViewById(R.id.rfChannelView).setVisibility(View.GONE);
        }
        mccLabel.setText("MCC");
        mncLabel.setText("MNC");
        cidLabel.setText("CID");
        lacLabel.setText("LAC");
        pciAndPscLabel.setText("PSC");
        rfChannelLabel.setText("ARFCN");
        bwOrBSICLabel.setText("BSIC");
    }

    private void setCDMALabels() {
        signalStrengthTempView.setVisibility(View.GONE);
        signalStrengthTempView = findViewById(R.id.signalStrengthCDMAView);
        signalStrengthTempView.setVisibility(View.VISIBLE);
        lacLabel.setText("Latitude");
        pciAndPscLabel.setText("Longitude");
        findViewById(R.id.rfChannelView).setVisibility(View.GONE);
        findViewById(R.id.bwOrBSICView).setVisibility(View.GONE);
        mccLabel.setText("Network ID");
        mncLabel.setText("System ID");
        cidLabel.setText("Base Station");
        lacLabel.setText("");
        pciAndPscLabel.setText("");
    }

    private void set3GLabels() {
        signalStrengthTempView.setVisibility(View.GONE);
        signalStrengthTempView = findViewById(R.id.signalStrength3GView);
        signalStrengthTempView.setVisibility(View.VISIBLE);
        findViewById(R.id.lacView).setVisibility(View.VISIBLE);
        findViewById(R.id.pciOrPscView).setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            findViewById(R.id.rfChannelView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rfChannelView).setVisibility(View.GONE);
        }
        findViewById(R.id.bwOrBSICView).setVisibility(View.GONE);
        mccLabel.setText("MCC");
        mncLabel.setText("MNC");
        cidLabel.setText("CID");
        lacLabel.setText("LAC");
        pciAndPscLabel.setText("PSC");
        rfChannelLabel.setText("UARFCN");
    }

    private void set4GLabels() {
        signalStrengthTempView.setVisibility(View.GONE);
        signalStrengthTempView = findViewById(R.id.lteSignalStrengthView);
        signalStrengthTempView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            findViewById(R.id.bwOrBSICView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.bwOrBSICView).setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            findViewById(R.id.rfChannelView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rfChannelView).setVisibility(View.GONE);
        }
        findViewById(R.id.lacView).setVisibility(View.VISIBLE);
        findViewById(R.id.pciOrPscView).setVisibility(View.VISIBLE);
        mccLabel.setText("MCC");
        mncLabel.setText("MNC");
        cidLabel.setText("CI");
        lacLabel.setText("TAC");
        pciAndPscLabel.setText("PCI");
        rfChannelLabel.setText("EARFCN");
        bwOrBSICLabel.setText("Bandwidth");
    }


    public static String getNetworkClass(Context context, int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
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
