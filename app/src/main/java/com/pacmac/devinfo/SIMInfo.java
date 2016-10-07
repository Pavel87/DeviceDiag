package com.pacmac.devinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SIMInfo extends ActionBarActivity {

    private TextView simInfo;
    private TextView serialN;
    private TextView imeiNumber;
    private TextView imsiNumber;
    private TextView networkName;
    private TextView mcc;
    private TextView mnc;
    private TextView spnName;
    private TextView mccSpn;
    private TextView mncSpn;
    private TextView phoneNumber;
    private TextView providerCountry;
    private TextView networkType;
    private TextView dataActivity;
    private TextView dataState;
    private TextView phoneRadio;
    private TextView cellInformation;

    private boolean isSIMInside = false;

    private List<CellInfo> cellInfo;
    private ShareActionProvider mShareActionProvider;
    private final Handler mHandler = new Handler();
    private Runnable timer;

    boolean isPermissionEnabled = true;
    private static final String PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.siminfo);

        // Check if user disabled CAMERA permission at some point
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), PHONE_PERMISSION);
        }

        if (!isPermissionEnabled) {
            Utility.displayExplanationForPermission(this, getResources().getString(R.string.phone_permission_msg), PHONE_PERMISSION);
        }

        simInfo = (TextView) findViewById(R.id.simInfo);
        serialN = (TextView) findViewById(R.id.serialN);
        imeiNumber = (TextView) findViewById(R.id.imei);
        imsiNumber = (TextView) findViewById(R.id.imsiNumber);
        networkName = (TextView) findViewById(R.id.networkName);
        mcc = (TextView) findViewById(R.id.mccCode);
        mnc = (TextView) findViewById(R.id.mncCode);
        spnName = (TextView) findViewById(R.id.operatorName);
        mccSpn = (TextView) findViewById(R.id.mccSPN);
        mncSpn = (TextView) findViewById(R.id.mncSPN);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        providerCountry = (TextView) findViewById(R.id.networkCountry);
        networkType = (TextView) findViewById(R.id.networkType);
        dataActivity = (TextView) findViewById(R.id.dataActivity);
        dataState = (TextView) findViewById(R.id.dataState);
        phoneRadio = (TextView) findViewById(R.id.phoneRadio);
        cellInformation = (TextView) findViewById(R.id.cellInformation);


        // update view with phone data
        updateData();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateData() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        simInfo.setText(simState(telephonyManager.getSimState()));

        if (telephonyManager.getDeviceId() != "" && telephonyManager.getDeviceId() != null) {

            if (isSIMInside) {
                serialN.setText(telephonyManager.getSimSerialNumber());
                imsiNumber.setText(telephonyManager.getSubscriberId());
                spnName.setText(telephonyManager.getSimOperatorName());

                mccSpn.setText("SPN-MCC: " + telephonyManager.getSimOperator().substring(0, 3));
                mncSpn.setText("SPN-MNC: " + telephonyManager.getSimOperator().substring(3));

                networkName.setText(telephonyManager.getNetworkOperatorName());

                if (telephonyManager.getNetworkOperator().length() > 2) {
                    mcc.setText("MCC: " + telephonyManager.getNetworkOperator().substring(0, 3));
                    mnc.setText("MNC: " + telephonyManager.getNetworkOperator().substring(3));
                } else {
                    mcc.setText("MCC: " + "N/A");
                    mnc.setText("MNC: " + "N/A");
                }
                providerCountry.setText(telephonyManager.getNetworkCountryIso().toUpperCase());
                phoneNumber.setText(telephonyManager.getLine1Number());
                networkType.setText(networkType(telephonyManager.getNetworkType()));
                cellInfo = telephonyManager.getAllCellInfo();
                String currentCellInfo = "Unknown";
                // getAllCellInfo may return null in old phones!
                if (cellInfo != null && cellInfo.size() > 0) {
                    for (CellInfo cell : cellInfo) {
                        if (cell instanceof CellInfoGsm && cell.isRegistered()) {
                            int lac = ((CellInfoGsm) cell).getCellIdentity().getLac();
                            int cid = ((CellInfoGsm) cell).getCellIdentity().getCid();
                            int psc = ((CellInfoGsm) cell).getCellIdentity().getPsc();
                            int dbm = ((CellInfoGsm) cell).getCellSignalStrength().getDbm();
                            int asuLevel = ((CellInfoGsm) cell).getCellSignalStrength().getAsuLevel();
                            currentCellInfo = "LAC: " + lac + "\n" + "Cell ID: " + cid + "\n" + "PSC: " + psc + "\n"
                                    + "Signal Strength[dBm]: " + dbm + "\n" + "Asu Level: " + asuLevel + "\n"
                                    + "Type: GSM";

                        } else if (cell instanceof CellInfoCdma && cell.isRegistered()) {
                            int baseStationId = ((CellInfoCdma) cell).getCellIdentity().getBasestationId();
                            int netId = ((CellInfoCdma) cell).getCellIdentity().getNetworkId();
                            int sysId = ((CellInfoCdma) cell).getCellIdentity().getSystemId();
                            int dbm = ((CellInfoCdma) cell).getCellSignalStrength().getDbm();
                            int asuLevel = ((CellInfoCdma) cell).getCellSignalStrength().getAsuLevel();
                            currentCellInfo = "Base Station ID: " + baseStationId + "\n" + "Network ID: " + netId + "\n" + "System ID: " + sysId + "\n"
                                    + "Signal Strength[dBm]: " + dbm + "\n" + "Asu Level: " + asuLevel + "\n"
                                    + "Type: CDMA";

                        } else if (Build.VERSION.SDK_INT > 17 && cell instanceof CellInfoWcdma && cell.isRegistered()) {   // FROM API 18+ supported
                            int lac = ((CellInfoWcdma) cell).getCellIdentity().getLac();
                            int cid = ((CellInfoWcdma) cell).getCellIdentity().getCid();
                            int psc = ((CellInfoWcdma) cell).getCellIdentity().getPsc();
                            int dbm = ((CellInfoWcdma) cell).getCellSignalStrength().getDbm();
                            int asuLevel = ((CellInfoWcdma) cell).getCellSignalStrength().getAsuLevel();
                            currentCellInfo = "LAC: " + lac + "\n" + "Cell ID: " + cid + "\n" + "PSC: " + psc + "\n"
                                    + "Signal Strength[dBm]: " + dbm + "\n" + "Asu Level: " + asuLevel + "\n"
                                    + "Type: UMTS";

                        } else if (cell instanceof CellInfoLte && cell.isRegistered()) {
                            int cellId = ((CellInfoLte) cell).getCellIdentity().getCi();
                            int physCellId = ((CellInfoLte) cell).getCellIdentity().getPci();
                            int tac = ((CellInfoLte) cell).getCellIdentity().getTac();
                            int dbm = ((CellInfoLte) cell).getCellSignalStrength().getDbm();
                            int asuLevel = ((CellInfoLte) cell).getCellSignalStrength().getAsuLevel();
                            currentCellInfo = "Cell Identity: " + cellId + "\n" + "Physical Cell ID: " + physCellId + "\n" + "TAC: " + tac + "\n"
                                    + "Signal Strength[dBm]: " + dbm + "\n" + "Asu Level: " + asuLevel + "\n"
                                    + "Type: LTE";
                        }
                    }
                } else {
                    CellLocation cell = telephonyManager.getCellLocation();

                    if (cell instanceof GsmCellLocation) {
                        int lac = ((GsmCellLocation) cell).getLac();
                        int cid = ((GsmCellLocation) cell).getCid();
                        int psc = ((GsmCellLocation) cell).getPsc();
                        currentCellInfo = "LAC: " + lac + "\n" + "Cell ID: " + cid + "\n" + "PSC: " + psc + "\n"
                                + "Type: GSM";
                    } else if (cell instanceof CdmaCellLocation) {
                        int baseStationId = ((CdmaCellLocation) cell).getBaseStationId();
                        int sysId = ((CdmaCellLocation) cell).getSystemId();
                        int netId = ((CdmaCellLocation) cell).getNetworkId();
                        int longitude = ((CdmaCellLocation) cell).getBaseStationLongitude();
                        int latitude = ((CdmaCellLocation) cell).getBaseStationLatitude();
                        currentCellInfo = "Base Station ID: " + baseStationId + "\n" + "Network ID: " + netId + "\n" + "System ID: " + sysId + "\n"
                                + "BS Longitude: " + longitude + "\n" + "BS Latitude: " + latitude + "\n"
                                + "Type: CDMA";
                    } else {
                        currentCellInfo = getResources().getString(R.string.not_available_info);
                    }


                }
                cellInformation.setText(currentCellInfo);
            }

            imeiNumber.setText(telephonyManager.getDeviceId());
            dataActivity.setText(dataActivityQuery(telephonyManager.getDataActivity()));
            dataState.setText(dataConnState(telephonyManager.getDataState()));
            phoneRadio.setText(getPhoneRadio(telephonyManager.getPhoneType()));
        } else if (!isPermissionEnabled) {
            imeiNumber.setTextColor(Color.RED);
            imeiNumber.setText("No WAN DETECTED");
            simInfo.setTextColor(Color.RED);
            simInfo.setText("NO SIM CARD DETECTED");
        } else {
            Toast.makeText(getApplicationContext(), "There is no WAN radio available", Toast.LENGTH_LONG).show();
            imeiNumber.setTextColor(Color.RED);
            imeiNumber.setText("No WAN DETECTED");
            simInfo.setTextColor(Color.RED);
            simInfo.setText("NO SIM CARD DETECTED");
        }

        updateShareIntent();

    }

    private String simState(int value) {

        switch (value) {

            case TelephonyManager.SIM_STATE_UNKNOWN:
                isSIMInside = false;
                simInfo.setTextColor(Color.RED);
                return "Unknown - SIM might be in transition between states";

            case TelephonyManager.SIM_STATE_ABSENT:
                isSIMInside = false;
                simInfo.setTextColor(Color.RED);
                return "No SIM available in device";


            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                isSIMInside = true;
                simInfo.setTextColor(Color.RED);
                return "SIM Locked - requires a SIM PIN";


            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                isSIMInside = true;
                simInfo.setTextColor(Color.RED);
                return "SIM Locked - requires a SIM PUK";


            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                isSIMInside = true;
                simInfo.setTextColor(Color.RED);
                return "SIM Locked - requires a network PIN";

            case TelephonyManager.SIM_STATE_READY:
                isSIMInside = true;
                simInfo.setTextColor(Color.BLACK);
                return "SIM is ready";
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
        sb.append("SIM SN: " + serialN.getText().toString());
        sb.append("\n");
        sb.append("IMEI: " + imeiNumber.getText().toString());
        sb.append("\n");
        sb.append("IMSI: " + imsiNumber.getText().toString());
        sb.append("\n");
        sb.append("Phone Radio: " + phoneRadio.getText().toString());
        sb.append("\n");
        sb.append("SPN Name: " + spnName.getText().toString());
        sb.append("\n");
        sb.append(mccSpn.getText().toString());
        sb.append("\n");
        sb.append(mncSpn.getText().toString());
        sb.append("\n");
        sb.append("Network Name: " + networkName.getText().toString());
        sb.append("\n");
        sb.append(mcc.getText().toString());
        sb.append("\n");
        sb.append(mnc.getText().toString());
        sb.append("\n");
        sb.append("Provider Country: " + providerCountry.getText().toString());
        sb.append("\n");
        sb.append("Network Type: " + networkType.getText().toString());
        sb.append("\n");
        sb.append("SIM Number: " + phoneNumber.getText().toString());
        sb.append("\n");
        sb.append("Cell Info: " + cellInformation.getText().toString());
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
        if (isPermissionEnabled) {
            timer = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(this, 5000);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            updateData();
                        }
                    });
                }
            };
            mHandler.postDelayed(timer, 5000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeCallbacks(timer);
        }
    }

}
