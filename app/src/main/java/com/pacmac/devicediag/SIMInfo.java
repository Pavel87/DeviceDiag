package com.pacmac.devicediag;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private String cell;

    private List<CellInfo> cellInfo;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.siminfo);


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

                mccSpn.setText("SPN-MCC:" + telephonyManager.getSimOperator().substring(0, 3));
                mncSpn.setText("SPN-MNC:" + telephonyManager.getSimOperator().substring(3));

                networkName.setText(telephonyManager.getNetworkOperatorName());

                mcc.setText("MCC:" + telephonyManager.getNetworkOperator().substring(0, 3));
                mnc.setText("MNC:" + telephonyManager.getNetworkOperator().substring(3));

                providerCountry.setText(telephonyManager.getNetworkCountryIso().toUpperCase());
                phoneNumber.setText(telephonyManager.getLine1Number());
                networkType.setText(networkType(telephonyManager.getNetworkType()));
                cellInfo = telephonyManager.getAllCellInfo();
                if (cellInfo != null) {
                    if (cellInfo.get(0).isRegistered()) {

                        cell = cellInfo.get(0).toString();
                        int start = cell.indexOf("mLac=");
                        int stop = cell.indexOf("mCid");
                        String lac = cell.substring(start + 5, stop);

                        start = stop;
                        stop = cell.indexOf("mPsc=");
                        String cid = cell.substring(start + 5, stop);

                        start = cell.indexOf("ss=");
                        stop = cell.indexOf(" ber=");
                        String ss = cell.substring(start + 3, stop);

                        start = stop;
                        stop = cell.indexOf("}", start);
                        String ber = cell.substring(start + 5, stop);
                        cellInformation.setText("LAC: " + lac + "\n" + "Cell ID: " + cid + "\n" +
                                "Signal Stregth[dBm]: " + ss +
                                "\n" + "BER: " + ber);
                    }
                }
            }

            imeiNumber.setText(telephonyManager.getDeviceId());
            dataActivity.setText(dataActivityQuery(telephonyManager.getDataActivity()));
            dataState.setText(dataConnState(telephonyManager.getDataState()));
            phoneRadio.setText(getPhoneRadio(telephonyManager.getPhoneType()));
        }
        else {
            Toast.makeText(getApplicationContext(), "There is no WAN radio available", Toast.LENGTH_LONG).show();
            imeiNumber.setTextColor(Color.RED);
            imeiNumber.setText("No WAN DETECTED");
            simInfo.setTextColor(Color.RED);
            simInfo.setText("NO SIM CARD DETECTED");
        }
    }

    private String simState(int value) {

        switch (value) {

            case TelephonyManager.SIM_STATE_UNKNOWN:
                //TODO sometimes it occurs on unit which doesn't have SIM needs to be false as well
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
                dataState.setTextColor(Color.BLACK);
                return "Connected: IP traffic should be available";
            case TelephonyManager.DATA_DISCONNECTED:
                dataState.setTextColor(Color.RED);
                return "Disconnected: IP traffic not available";
            case TelephonyManager.DATA_CONNECTING:
                return "Setting up data connection";
            case TelephonyManager.DATA_SUSPENDED:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sim, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateData();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
