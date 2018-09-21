package com.pacmac.devinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

public class NetworkInfo extends AppCompatActivity implements InterfaceASTask {

    private EditText pingUrl;
    private TextView pingOut, wifiConnected, wanConnected;
    private TextView ssidField, bssidField, macField, rssiField, linkSpeedField, frequencyField, roaming;
    private TextView ipAddressField, netMaskField, gatewayField, dns1Field, dns2Field, dhcpField, leaseField;
    private ImageView ghzBand, powerReport, devToAp, p2p, offloadedConn, scanAlways, tdlsSupport;
    private TextView supplicantState, apCapabilities, centerFreq0, centerFreq1, channelWidth, passpointNetwork, mcResponder, operatorName, venueName;


    private Button pingBtn;
    private LinearLayout wifiDetail, supportedFeatures;

    private AsyncPingTask asyncPingTask;
    private boolean isWiFi = false;
    private String url = null;
    AlertDialog progress = null;
    private final Handler mHandler = new Handler();
    private Runnable timer;
    private String roamingStr = null;
    private String bssidTemp = null;
    private ShareActionProvider mShareActionProvider;
    private ScanResult scanResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_info);

        if (savedInstanceState != null) {
            roamingStr = savedInstanceState.getString("lastRoaming");
        } else
            roamingStr = getResources().getString(R.string.no_string);

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

        supplicantState = (TextView) findViewById(R.id.supplicantState);
        apCapabilities = (TextView) findViewById(R.id.apCapabilities);
        channelWidth = (TextView) findViewById(R.id.channelWidth);
        centerFreq0 = (TextView) findViewById(R.id.centerFreq0);
        centerFreq1 = (TextView) findViewById(R.id.centerFreq1);
        operatorName = (TextView) findViewById(R.id.operatorFriendlyName);
        venueName = (TextView) findViewById(R.id.venueName);
        mcResponder = (TextView) findViewById(R.id.mcResponder);
        passpointNetwork = (TextView) findViewById(R.id.passPointNetwork);


        roaming = (TextView) findViewById(R.id.roaming);
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
                //disable SIP on button press
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (pingUrl.hasFocus() & getResources().getBoolean(R.bool.dual_pane) != true)
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                url = pingUrl.getText().toString();
                pingUrl.clearFocus();
                // run ping in backround
                asyncPingTask = new AsyncPingTask();
                asyncPingTask.asynResp = NetworkInfo.this;
                asyncPingTask.execute(url);
            }
        });


        checkRadioStates();


        // show WiFi detail

        if (isWiFi) {

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            updateView(wifiManager);
            wifiDetail.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {

                supportedFeatures.setVisibility(View.VISIBLE);

                ghzBand = (ImageView) findViewById(R.id.bandSupport);
                devToAp = (ImageView) findViewById(R.id.deviceToApRtt);
                p2p = (ImageView) findViewById(R.id.wifiDirectSupport);
                offloadedConn = (ImageView) findViewById(R.id.offLoadConn);
                powerReport = (ImageView) findViewById(R.id.powerReport);
                scanAlways = (ImageView) findViewById(R.id.scanAlwaysAvailable);
                tdlsSupport = (ImageView) findViewById(R.id.tdlsSupported);

                if (wifiManager.is5GHzBandSupported()) {
                    ghzBand.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                }
                if (wifiManager.isEnhancedPowerReportingSupported())
                    powerReport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (wifiManager.isDeviceToApRttSupported())
                    devToAp.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (wifiManager.isP2pSupported())
                    p2p.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (wifiManager.isPreferredNetworkOffloadSupported())
                    offloadedConn.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (wifiManager.isScanAlwaysAvailable())
                    scanAlways.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (wifiManager.isTdlsSupported())
                    tdlsSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
            }
        }

        progress = new CenterProgress(this);
    }

    public void showProgressBar(boolean isHidden) {

        if (isHidden) {
            progress.setCancelable(false);
            progress.show();
        } else
            //progress.hide();
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
    }

    public int getFrequency(String bssid) {
        if(bssid == null)
            return -1;

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return -1;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null)
            return -1;

        int frequency = -1;

        // API 21+ has method to pull frequency channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // should we check for permissions in Manifest here ??
            return wifiInfo.getFrequency();
            // Older android versions have to use getScanResults to get frequency
        } else {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            if (wifiScanList == null || wifiScanList.size() == 0)
                return -1;
            for (int i = 0; i < wifiScanList.size(); i++) {
                if (bssid.equals(wifiScanList.get(i).BSSID)) {
                    return wifiScanList.get(i).frequency;
                }
            }
        }
        return frequency;
    }

    public ScanResult getWiFiScanResult(String bssid) {
        if(bssid == null)
            return null;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null)
            return null;
        List<ScanResult> wifiScanList = wifiManager.getScanResults();
        if (wifiScanList == null || wifiScanList.size() == 0)
            return null;
        for (int i = 0; i < wifiScanList.size(); i++) {
            if (bssid.equals(wifiScanList.get(i).BSSID)) {
                return wifiScanList.get(i);
            }
        }
        return null;
    }

    @Override
    public void showPingResponse(String result) {
        showProgressBar(false);
        if (result != null && result.length() > 0)
            pingOut.setText(result);
        else
            pingOut.setText(getResources().getString(R.string.ping_error_3));
    }

    @Override
    public void startingPingCommand() {
        pingOut.setText("Waiting for response from: " + url);
        showProgressBar(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 5000);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        updateView(wifiManager);
                        checkRadioStates();
                        updateShareIntent();
                    }
                });
            }
        };
        mHandler.postDelayed(timer, 10000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timer);
    }


    public void updateView(WifiManager wifiManager) {


        WifiInfo wifiInfo = null;
        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO if wifiInfo == null

        if (bssidTemp != null && !bssidTemp.equals(wifiInfo.getBSSID())) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            bssidTemp = wifiInfo.getBSSID();
            roamingStr = hour + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        } else {
            bssidTemp = wifiInfo.getBSSID();
        }


        // WIFI Connected info
        String ssid = wifiInfo.getSSID().replaceAll("\"", "");
        if (!ssid.equals("0x"))
            ssidField.setText(ssid);
        else
            ssidField.setText(getResources().getString(R.string.not_available_info));
        bssidField.setText(wifiInfo.getBSSID() + "");
        if (Build.VERSION.SDK_INT < 23) {
            macField.setText(wifiInfo.getMacAddress() + "");
        } else {
            macField.setText(getResources().getString(R.string.not_available_in_API) + Build.VERSION.SDK_INT);
        }
        rssiField.setText(wifiInfo.getRssi() + "");
        linkSpeedField.setText(wifiInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS);
        frequencyField.setText(getFrequency(wifiInfo.getBSSID()) + " MHz");
        supplicantState.setText(wifiInfo.getSupplicantState().name());
        roaming.setText(roamingStr);

        scanResult = getWiFiScanResult(wifiInfo.getBSSID());

        if (scanResult != null) {

            apCapabilities.setText(scanResult.capabilities);

            if (Build.VERSION.SDK_INT > 22) {
                channelWidth.setText(getChannelWidth(scanResult.channelWidth));
                if (scanResult.centerFreq0 > 0)
                    centerFreq0.setText(scanResult.centerFreq0 + WifiInfo.FREQUENCY_UNITS);
                if (scanResult.centerFreq1 > 0)
                    centerFreq1.setText(scanResult.centerFreq1 + WifiInfo.FREQUENCY_UNITS);
                if (!scanResult.operatorFriendlyName.equals(""))
                    operatorName.setText(scanResult.operatorFriendlyName + "");
                if (!scanResult.venueName.equals(""))
                    venueName.setText(scanResult.venueName + "");
                mcResponder.setText((scanResult.is80211mcResponder()) ? "YES" : "NO");
                passpointNetwork.setText((scanResult.isPasspointNetwork()) ? "YES" : "NO");
            } else {
                findViewById(R.id.textView11).setVisibility(View.GONE);
                findViewById(R.id.channelWidth).setVisibility(View.GONE);

                findViewById(R.id.textView10).setVisibility(View.GONE);
                findViewById(R.id.centerFreq0).setVisibility(View.GONE);

                findViewById(R.id.textView12).setVisibility(View.GONE);
                findViewById(R.id.centerFreq1).setVisibility(View.GONE);

                findViewById(R.id.textView13).setVisibility(View.GONE);
                findViewById(R.id.operatorFriendlyName).setVisibility(View.GONE);

                findViewById(R.id.textView14).setVisibility(View.GONE);
                findViewById(R.id.venueName).setVisibility(View.GONE);

                findViewById(R.id.textView15).setVisibility(View.GONE);
                findViewById(R.id.mcResponder).setVisibility(View.GONE);


                findViewById(R.id.textView16).setVisibility(View.GONE);
                findViewById(R.id.passPointNetwork).setVisibility(View.GONE);
            }
        }


        //dhcp address
        DhcpInfo dhcpInformation = wifiManager.getDhcpInfo();
        ipAddressField.setText(intToInetAddress(dhcpInformation.ipAddress).getHostAddress());
        gatewayField.setText(intToInetAddress(dhcpInformation.gateway).getHostAddress());
        netMaskField.setText(intToInetAddress(dhcpInformation.netmask).getHostAddress());
        dns1Field.setText(intToInetAddress(dhcpInformation.dns1).getHostAddress());
        dns2Field.setText(intToInetAddress(dhcpInformation.dns2).getHostAddress());
        dhcpField.setText(intToInetAddress(dhcpInformation.serverAddress).getHostAddress());
        leaseField.setText(dhcpInformation.leaseDuration + " " + "s");

    }


    private String getChannelWidth(int width) {
        switch (width) {
            case ScanResult.CHANNEL_WIDTH_20MHZ:
                return "20 MHz";
            case ScanResult.CHANNEL_WIDTH_40MHZ:
                return "40 MHz";
            case ScanResult.CHANNEL_WIDTH_80MHZ:
                return "80 MHz";
            case ScanResult.CHANNEL_WIDTH_160MHZ:
                return "160 MHz";
            case ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ:
                return "80 + 80 MHz";
            default:
                return getResources().getString(R.string.not_available_info);
        }
    }


    //  conversion taken from stackoverflow: http://stackoverflow.com/questions/6345597/human-readable-dhcpinfo-ipaddress
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    public void checkRadioStates() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkInfo;

        // check WIFI state and if present in device
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            isWiFi = true;

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                wifiDetail.setVisibility(View.VISIBLE);
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
                isWiFi = false;
                wifiDetail.setVisibility(View.GONE);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("lastRoaming", roamingStr);
        super.onSaveInstanceState(outState);
    }

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
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_network_info));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");

        if (isWiFi) {

            //body
            sb.append("SSID:\t\t" + ssidField.getText().toString());
            sb.append("\n");
            sb.append("BSSID:\t\t" + bssidField.getText().toString());
            sb.append("\n");
            sb.append("MAC:\t\t" + macField.getText().toString());
            sb.append("\n");
            sb.append("RSSI:\t\t" + rssiField.getText().toString());
            sb.append("\n");
            sb.append("Link Speed:\t\t" + linkSpeedField.getText().toString());
            sb.append("\n");
            sb.append("Frequency:\t\t" + frequencyField.getText().toString() + " MHz");
            sb.append("\n");
            sb.append("Last Roaming:\t\t" + roaming.getText().toString());
            sb.append("\n");
            sb.append("IP Address:\t\t" + ipAddressField.getText().toString());
            sb.append("\n");
            sb.append("Default Gateway:\t\t" + gatewayField.getText().toString());
            sb.append("\n");
            sb.append("Net Mask:\t\t" + netMaskField.getText().toString());
            sb.append("\n");
            sb.append("DNS1:\t\t" + dns1Field.getText().toString());
            sb.append("\n");
            sb.append("DNS2:\t\t" + dns2Field.getText().toString());
            sb.append("\n");
            sb.append("DHCP Address:\t\t" + dhcpField.getText().toString());
            sb.append("\n");
            sb.append("DHCP Lease:\t\t" + leaseField.getText().toString());
            sb.append("\n");
            sb.append("Supplicant State:\t\t" + supplicantState.getText().toString());
            sb.append("\n");
            sb.append("AP Capabilities:\t\t" + apCapabilities.getText().toString());

            if (Build.VERSION.SDK_INT > 22) {
                sb.append("\n");
                sb.append("Channel Width:\t\t" + channelWidth.getText().toString());
                sb.append("\n");
                sb.append("Center Frequency 0:\t\t" + centerFreq0.getText().toString());
                sb.append("\n");
                sb.append("Center Frequency 1:\t\t" + centerFreq1.getText().toString());
                sb.append("\n");
                sb.append("Operator Friendly Name:\t\t" + operatorName.getText().toString());
                sb.append("\n");
                sb.append("Venue Name:\t\t" + venueName.getText().toString());
                sb.append("\n");
                sb.append("Is 802.11mc Responder:\t\t" + mcResponder.getText().toString());
                sb.append("\n");
                sb.append("Is Passpoint Network:\t\t" + passpointNetwork.getText().toString());
                sb.append("\n\n");
            }

        } else {
            sb.append("WIFI INFO NOT AVAILABLE");
            sb.append("\n\n");
        }

        sb.append(getResources().getString(R.string.shareTextTitle1));
        setShareIntent(createShareIntent(sb));
    }
}

