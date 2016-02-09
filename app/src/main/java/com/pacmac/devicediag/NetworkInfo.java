package com.pacmac.devicediag;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetworkInfo extends AppCompatActivity implements InterfaceASTask {

    private EditText pingUrl;
    private TextView pingOut, wifiConnected, wanConnected;
    private TextView ssidField, bssidField, macField, rssiField, linkSpeedField, frequencyField;
    private TextView ipAddressField, netMaskField, gatewayField, dns1Field, dns2Field, dhcpField, leaseField;
    private TextView ghzBand, powerReport, devToAp, p2p, offloadedConn, scanAlways, tdlsSupport;
    private Button pingBtn;
    private LinearLayout wifiDetail, supportedFeatures;

    private AsyncPingTask asyncPingTask;
    private boolean isWiFi = false;
    private String url = null;
    AlertDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_info);

        pingUrl = (EditText) findViewById(R.id.pingAddress);
        pingOut = (TextView) findViewById(R.id.pingOut);
        wifiConnected = (TextView) findViewById(R.id.wifiConn);
        wanConnected = (TextView) findViewById(R.id.wanConn);

        //WiFi fields

        ssidField = (TextView) findViewById(R.id.ssidField);
        bssidField = (TextView) findViewById(R.id.bssidField);
        macField = (TextView) findViewById(R.id.macField);
        rssiField = (TextView) findViewById(R.id.rssiField);
        linkSpeedField = (TextView) findViewById(R.id.linkSpeedField);
        frequencyField = (TextView) findViewById(R.id.freqField);
        ipAddressField = (TextView) findViewById(R.id.ipAddress);
        gatewayField = (TextView) findViewById(R.id.gateway);
        netMaskField = (TextView) findViewById(R.id.netMask);
        dns1Field = (TextView) findViewById(R.id.dns1);
        dns2Field = (TextView) findViewById(R.id.dns2);
        dhcpField = (TextView) findViewById(R.id.dhcp);
        leaseField = (TextView) findViewById(R.id.dhcpLease);
        wifiDetail = (LinearLayout) findViewById(R.id.detailWifi);
        supportedFeatures = (LinearLayout) findViewById(R.id.supportedFeatures);
        pingBtn = (Button) findViewById(R.id.pingBtn);


        //delete pingURL onclick
        pingUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pingUrl.hasFocus()) {
                    pingUrl.setText("");
                }
            }
        });


        pingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                url = pingUrl.getText().toString();
                asyncPingTask = new AsyncPingTask();
                asyncPingTask.asynResp = NetworkInfo.this;
                asyncPingTask.execute(url);
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkInfo;

        // check WIFI state and if present in device
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            isWiFi = true;

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                wifiConnected.setTextColor(getResources().getColor(R.color.connected_clr));
                wifiConnected.setText(getString(R.string.connected_info));
            } else if (networkInfo.isAvailable()) {
                wifiConnected.setTextColor(Color.BLUE);
                wifiConnected.setText(getString(R.string.available_info));
            } else {
                wifiConnected.setTextColor(Color.RED);
                wifiConnected.setText(getString(R.string.not_available_info));
            }
        } else {
            wifiConnected.setTextColor(Color.RED);
            wifiConnected.setText(getString(R.string.not_present));
        }

        // check WAN state and if present in device
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo.isConnected()) {
                wanConnected.setTextColor(getResources().getColor(R.color.connected_clr));
                wanConnected.setText(getString(R.string.connected_info));
            } else if (networkInfo.isAvailable()) {
                wanConnected.setTextColor(Color.BLUE);
                wanConnected.setText(getString(R.string.available_info));
            } else {
                wanConnected.setTextColor(Color.RED);
                wanConnected.setText(getString(R.string.not_available_info));
            }
        } else {
            wanConnected.setTextColor(Color.RED);
            wanConnected.setText(getString(R.string.not_present));
        }


        // show WiFi detail

        if (isWiFi) {


            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


            // wifi info
            String[] connInformation = wifiManager.getConnectionInfo().toString().split(",");
            ssidField.setText(connInformation[0].substring(6));
            bssidField.setText(connInformation[1].substring(7));
            macField.setText(connInformation[2].substring(5));
            rssiField.setText(connInformation[4].substring(6));
            linkSpeedField.setText(connInformation[5].substring(12));
            frequencyField.setText(connInformation[6].substring(11));

            //dhcp address

            String[] dhcpInformation = wifiManager.getDhcpInfo().toString().split(" ");
            ipAddressField.setText(dhcpInformation[1]);
            gatewayField.setText(dhcpInformation[3]);
            netMaskField.setText(dhcpInformation[5]);
            dns1Field.setText(dhcpInformation[7]);
            dns2Field.setText(dhcpInformation[9]);
            dhcpField.setText(dhcpInformation[12]);
            leaseField.setText(dhcpInformation[14] + " seconds");

            wifiDetail.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {

                supportedFeatures.setVisibility(View.VISIBLE);

                ghzBand = (TextView) findViewById(R.id.bandSupport);
                devToAp = (TextView) findViewById(R.id.deviceToApRtt);
                p2p = (TextView) findViewById(R.id.wifiDirectSupport);
                offloadedConn = (TextView) findViewById(R.id.offLoadConn);
                powerReport = (TextView) findViewById(R.id.powerReport);
                scanAlways = (TextView) findViewById(R.id.scanAlwaysAvailable);
                tdlsSupport = (TextView) findViewById(R.id.tdlsSupported);

                if (wifiManager.is5GHzBandSupported()) {
                    ghzBand.setTextColor(getResources().getColor(R.color.connected_clr));
                    ghzBand.setText("YES");
                }

                if (wifiManager.isEnhancedPowerReportingSupported()) {
                    powerReport.setTextColor(getResources().getColor(R.color.connected_clr));
                    powerReport.setText("YES");
                }
                if (wifiManager.isDeviceToApRttSupported()) {
                    devToAp.setTextColor(getResources().getColor(R.color.connected_clr));
                    devToAp.setText("YES");
                }
                if (wifiManager.isP2pSupported()) {
                    p2p.setTextColor(getResources().getColor(R.color.connected_clr));
                    p2p.setText("YES");
                }
                if (wifiManager.isPreferredNetworkOffloadSupported()) {
                    offloadedConn.setTextColor(getResources().getColor(R.color.connected_clr));
                    offloadedConn.setText("YES");
                }
                if (wifiManager.isScanAlwaysAvailable()) {
                    scanAlways.setTextColor(getResources().getColor(R.color.connected_clr));
                    scanAlways.setText("YES");
                }

                if (wifiManager.isTdlsSupported()) {
                    tdlsSupport.setTextColor(getResources().getColor(R.color.connected_clr));
                    tdlsSupport.setText("YES");
                }


            }
        }


        progress = new CenterProgress(this);
    }

    public void showProgressBar(boolean isHidden) {

        if (isHidden) {
            progress.setCancelable(false);
            progress.show();
        } else
            progress.hide();

    }


    @Override
    public void showPingResponse(String result) {
        showProgressBar(false);
        if (result.length() > 0)
            pingOut.setText(result);
        else
            pingOut.setText(getResources().getString(R.string.ping_error_3));
    }

    @Override
    public void startingPingCommand() {
        pingOut.setText("Waiting for response from: " + url);
        showProgressBar(true);
    }


}
