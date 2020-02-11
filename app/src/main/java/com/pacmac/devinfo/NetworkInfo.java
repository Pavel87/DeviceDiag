package com.pacmac.devinfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

public class NetworkInfo extends AppCompatActivity {

    private TextView wifiConnected, wanConnected;
    private TextView ssidField, bssidField, macField, rssiField, linkSpeedField, frequencyField, roaming;
    private TextView ipAddressField, netMaskField, gatewayField, dns1Field, dns2Field, dhcpField, leaseField;
    private TextView upstreamBandwidth, downstreamBandwidth;
    private ImageView ghzBand, powerReport, devToAp, p2p, offloadedConn, scanAlways, tdlsSupport, dppSupport, oweSupport, wpa3SAESupport, wpa3SuiteBSSupport;
    private TextView supplicantState, apCapabilities, centerFreq0, centerFreq1, channelWidth, passpointNetwork, mcResponder, operatorName, venueName;

    private boolean _ghzBand = false;
    private boolean _powerReport = false;
    private boolean _devToAp = false;
    private boolean _p2p = false;
    private boolean _offloadedConn = false;
    private boolean _scanAlways = false;
    private boolean _tdlsSupport = false;
    private boolean _dppSupport = false;
    private boolean _oweSupport = false;
    private boolean _wpa3SAESupport = false;
    private boolean _wpa3SuiteBSSupport = false;

    private LinearLayout wifiDetail, supportedFeatures, addressView, downstreamBandwidthView, upstreamBandwidthView;

    private boolean isWiFi = false;
    private final Handler mHandler = new Handler();
    private Runnable timer;
    private String roamingStr = null;
    private String bssidTemp = null;
    private ScanResult scanResult = null;
    private boolean isLocationPermissionEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_info);

        // Check if user disabled LOCATION permission at some point
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isLocationPermissionEnabled = Utility.checkPermission(getApplicationContext(), Utility.ACCESS_FINE_LOCATION);
        }
        if (!isLocationPermissionEnabled) {
            Utility.requestPermissions(this, Utility.getLocationPermissions());
        }


        if (savedInstanceState != null) {
            roamingStr = savedInstanceState.getString("lastRoaming");
        } else
            roamingStr = getResources().getString(R.string.no_string);

        wifiConnected = findViewById(R.id.wifiConn);
        wanConnected = findViewById(R.id.wanConn);

        ssidField = findViewById(R.id.ssidField);
        bssidField = findViewById(R.id.bssidField);
        macField = findViewById(R.id.macField);
        rssiField = findViewById(R.id.rssiField);
        linkSpeedField = findViewById(R.id.linkSpeedField);
        frequencyField = findViewById(R.id.freqField);

        supplicantState = findViewById(R.id.supplicantState);
        apCapabilities = findViewById(R.id.apCapabilities);
        channelWidth = findViewById(R.id.channelWidth);
        centerFreq0 = findViewById(R.id.centerFreq0);
        centerFreq1 = findViewById(R.id.centerFreq1);
        operatorName = findViewById(R.id.operatorFriendlyName);
        venueName = findViewById(R.id.venueName);
        mcResponder = findViewById(R.id.mcResponder);
        passpointNetwork = findViewById(R.id.passPointNetwork);


        roaming = findViewById(R.id.roaming);
        ipAddressField = findViewById(R.id.ipAddress);
        gatewayField = findViewById(R.id.gateway);
        netMaskField = findViewById(R.id.netMask);
        dns1Field = findViewById(R.id.dns1);
        dns2Field = findViewById(R.id.dns2);
        dhcpField = findViewById(R.id.dhcp);
        leaseField = findViewById(R.id.dhcpLease);
        wifiDetail = findViewById(R.id.detailWifi);
        addressView = findViewById(R.id.addressView);
        supportedFeatures = findViewById(R.id.supportedFeatures);
        downstreamBandwidthView = findViewById(R.id.downstreamBandwidthView);
        upstreamBandwidthView = findViewById(R.id.upstreamBandwidthView);

        upstreamBandwidth = findViewById(R.id.upstreamBandwidth);
        downstreamBandwidth = findViewById(R.id.downstreamBandwidth);


        // Open ICMP PING tool in market store
        findViewById(R.id.icmpPingTool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appPackage = "com.pacmac.pinger";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                    startActivity(intent);
                }
            }
        });

        checkRadioStates();

    }

    public int getFrequency(String bssid) {
        if (bssid == null)
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
        if (bssid == null)
            return null;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null)
            return null;

        if (isLocationPermissionEnabled) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            if (wifiScanList == null || wifiScanList.size() == 0)
                return null;
            for (int i = 0; i < wifiScanList.size(); i++) {
                if (bssid.equals(wifiScanList.get(i).BSSID)) {
                    return wifiScanList.get(i);
                }
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        updateView(wifiManager);
                        checkRadioStates();
                    }
                });
                mHandler.postDelayed(this, 5000);
            }
        };
        mHandler.postDelayed(timer, 500);
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
                findViewById(R.id.channelWidthView).setVisibility(View.GONE);
                findViewById(R.id.channelWidth).setVisibility(View.GONE);

                findViewById(R.id.centerFreq0View).setVisibility(View.GONE);
                findViewById(R.id.centerFreq0).setVisibility(View.GONE);

                findViewById(R.id.centerFreq1View).setVisibility(View.GONE);
                findViewById(R.id.centerFreq1).setVisibility(View.GONE);

                findViewById(R.id.operatorFriendlyNameView).setVisibility(View.GONE);
                findViewById(R.id.operatorFriendlyName).setVisibility(View.GONE);

                findViewById(R.id.venueNameView).setVisibility(View.GONE);
                findViewById(R.id.venueName).setVisibility(View.GONE);

                findViewById(R.id.mcResponderView).setVisibility(View.GONE);
                findViewById(R.id.mcResponder).setVisibility(View.GONE);


                findViewById(R.id.passPointNetworkView).setVisibility(View.GONE);
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

        if (connMgr == null) {
            return;
        }

        // check WIFI state and if present in device
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            isWiFi = true;

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                wifiDetail.setVisibility(View.VISIBLE);
                addressView.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= 21) {
                    supportedFeatures.setVisibility(View.VISIBLE);
                }
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
        getWifiFeatures();

        // check WAN state and if present in device
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null && networkInfo.isConnected()) {
                isWiFi = false;
                wifiDetail.setVisibility(View.GONE);
                addressView.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= 21) {
                    supportedFeatures.setVisibility(View.GONE);
                }
                wanConnected.setTextColor(getResources().getColor(R.color.connected_clr));
                wanConnected.setText(getString(R.string.connected_info));
            } else if (networkInfo != null && networkInfo.isAvailable()) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Network network = connMgr.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connMgr.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    downstreamBandwidthView.setVisibility(View.VISIBLE);
                    upstreamBandwidthView.setVisibility(View.VISIBLE);
                    downstreamBandwidth.setText(String.valueOf(networkCapabilities.getLinkDownstreamBandwidthKbps()));
                    upstreamBandwidth.setText(String.valueOf(networkCapabilities.getLinkUpstreamBandwidthKbps()));
                }
            }
        }
    }

    private void getWifiFeatures() {
        // show WiFi detail

        if (isWiFi) {

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            updateView(wifiManager);
            wifiDetail.setVisibility(View.VISIBLE);
            addressView.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {

                supportedFeatures.setVisibility(View.VISIBLE);

                ghzBand = findViewById(R.id.bandSupport);
                devToAp = findViewById(R.id.deviceToApRtt);
                p2p = findViewById(R.id.wifiDirectSupport);
                offloadedConn = findViewById(R.id.offLoadConn);
                powerReport = findViewById(R.id.powerReport);
                scanAlways = findViewById(R.id.scanAlwaysAvailable);
                tdlsSupport = findViewById(R.id.tdlsSupported);
                dppSupport = findViewById(R.id.dppSupport);
                oweSupport = findViewById(R.id.oweSupport);
                wpa3SAESupport = findViewById(R.id.wpa3SAESupport);
                wpa3SuiteBSSupport = findViewById(R.id.wpa3SuiteBSSupport);

                _ghzBand = wifiManager.is5GHzBandSupported();
                _powerReport = wifiManager.isEnhancedPowerReportingSupported();
                _devToAp = wifiManager.isDeviceToApRttSupported();
                _p2p = wifiManager.isP2pSupported();
                _offloadedConn = wifiManager.isPreferredNetworkOffloadSupported();
                _scanAlways = wifiManager.isScanAlwaysAvailable();
                _tdlsSupport = wifiManager.isTdlsSupported();

                if (_ghzBand) {
                    ghzBand.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                }
                if (_powerReport)
                    powerReport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (_devToAp)
                    devToAp.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (_p2p)
                    p2p.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (_offloadedConn)
                    offloadedConn.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (_scanAlways)
                    scanAlways.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));

                if (_tdlsSupport)
                    tdlsSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));


                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    dppSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    oweSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    wpa3SAESupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    wpa3SuiteBSSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                } else {




                    _dppSupport = wifiManager.isEasyConnectSupported();
                    _oweSupport = wifiManager.isEnhancedOpenSupported();
                    _wpa3SAESupport = wifiManager.isWpa3SaeSupported();
                    _wpa3SuiteBSSupport = wifiManager.isWpa3SuiteBSupported();

                    if (_dppSupport) {
                        dppSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                    } else {
                        dppSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    }
                    if (_oweSupport) {
                        oweSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                    } else {
                        oweSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    }
                    if (_wpa3SAESupport) {
                        wpa3SAESupport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                    } else {
                        wpa3SAESupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    }
                    if (_wpa3SuiteBSSupport) {
                        wpa3SuiteBSSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                    } else {
                        wpa3SuiteBSSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel, null));
                    }

                }

            }
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
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            Utility.exporData(NetworkInfo.this, getResources().getString(R.string.title_activity_network_info), collectNetworkInfoForExport());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String collectNetworkInfoForExport() {

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
                sb.append("Dowsntream Bandwidth:\t\t" + downstreamBandwidth.getText().toString() + "kbps");
                sb.append("\n");
                sb.append("Upstream Bandwidth:\t\t" + upstreamBandwidth.getText().toString() + "kbps");
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
            }
            if (Build.VERSION.SDK_INT >= 21) {
                sb.append("\n");
                sb.append("Supported 5Ghz Band:\t\t" + (_ghzBand ? "YES" : " NO"));
                sb.append("\n");
                sb.append("Power Reporting:\t\t" + (_powerReport ? "YES" : " NO"));
                sb.append("\n");
                sb.append("Device to AP RTT:\t\t" + (_devToAp ? "YES" : " NO"));
                sb.append("\n");
                sb.append("WiFi Direct:\t\t" + (_p2p ? "YES" : " NO"));
                sb.append("\n");
                sb.append("Offloaded Connectivity Scan:\t\t" + (_offloadedConn ? "YES" : " NO"));
                sb.append("\n");
                sb.append("WiFi Scan Always On:\t\t" + (_scanAlways ? "YES" : " NO"));
                sb.append("\n");
                sb.append("Tunnel Directed Link Setup:\t\t" + (_tdlsSupport ? "YES" : " NO"));
                sb.append("\n");
                sb.append("Wi-Fi Easy Connect (DPP):\t\t" + (_dppSupport ? "YES" : " NO"));
                sb.append("\n");
                sb.append("Wi-Fi Enhanced Open (OWE):\t\t" + (_oweSupport ? "YES" : " NO"));
                sb.append("\n");
                sb.append("WPA3-Personal SAE:\t\t" + (_wpa3SAESupport ? "YES" : " NO"));
                sb.append("\n");
                sb.append("WPA3-Enterprise Suite-B-192:\t\t" + (_wpa3SuiteBSSupport ? "YES" : " NO"));
            }

        } else {
            if (Build.VERSION.SDK_INT > 22) {
                sb.append("Dowsntream Bandwidth:\t\t" + downstreamBandwidth.getText().toString() + "kbps");
                sb.append("\n");
                sb.append("Upstream Bandwidth:\t\t" + upstreamBandwidth.getText().toString() + "kbps");
            } else {
                sb.append("WIFI INFO NOT AVAILABLE");
            }
        }
        sb.append("\n\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        return sb.toString();
    }
}

