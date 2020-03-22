package com.pacmac.devinfo.wifi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.ThreeState;
import com.pacmac.devinfo.UIObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NetworkUtils {

    private static ScanResult scanResult;
    private static String bssidTemp = "";


    public static List<UIObject> getRadiosState(Context context) {

        List<UIObject> list = new ArrayList<>();
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkInfo;

        if (connMgr == null) {
            return list;
        }

        list.add(new UIObject(context.getString(R.string.network_radio_state), "", 1));

        // check WIFI state and if present in device
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            String wifiState = context.getResources().getString(R.string.not_available_info);
            if (networkInfo.isConnected()) {
                wifiState = context.getResources().getString(R.string.connected_info);
            } else if (networkInfo.isAvailable()) {
                wifiState = context.getResources().getString(R.string.available_info);
            }
            list.add(new UIObject(context.getString(R.string.network_wifi), wifiState));
        } else {
            list.add(new UIObject(context.getString(R.string.network_wifi), context.getResources().getString(R.string.not_present)));
        }

        // check WAN state and if present in device
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {

            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            String mobileState = context.getResources().getString(R.string.not_available_info);
            if (networkInfo != null && networkInfo.isConnected()) {
                mobileState = context.getResources().getString(R.string.connected_info);
            } else if (networkInfo != null && networkInfo.isAvailable()) {
                mobileState = context.getResources().getString(R.string.available_info);
            }
            list.add(new UIObject(context.getString(R.string.network_mobile_data), mobileState));
        } else {
            list.add(new UIObject(context.getString(R.string.network_mobile_data), context.getResources().getString(R.string.not_present)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Network network = connMgr.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connMgr.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    list.add(new UIObject(context.getString(R.string.network_link_down_bandwidth), String.valueOf(networkCapabilities.getLinkDownstreamBandwidthKbps()), "kbps"));
                    list.add(new UIObject(context.getString(R.string.network_up_bandwidth), String.valueOf(networkCapabilities.getLinkUpstreamBandwidthKbps()), "kbps"));
                }
            }
        }
        return list;
    }

    public static List<UIObject> getWifiFeatures(Context context) {
        // show WiFi detail

        List<UIObject> list = new ArrayList<>();


        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            return list;
        }

        if (Build.VERSION.SDK_INT >= 21) {

            list.add(new UIObject(context.getString(R.string.network_supported_features), "", 1));

            list.add(new UIObject(context.getString(R.string.network_five_ghz_band), wifiManager.is5GHzBandSupported() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));

            list.add(new UIObject(context.getString(R.string.network_ap_rtt), wifiManager.isDeviceToApRttSupported() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));

            list.add(new UIObject(context.getString(R.string.network_power_reporting), wifiManager.isEnhancedPowerReportingSupported() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));

            list.add(new UIObject(context.getString(R.string.network_wifi_direct), wifiManager.isP2pSupported() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));

            list.add(new UIObject(context.getString(R.string.network_offloaded_conn_scan), wifiManager.isPreferredNetworkOffloadSupported() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));

            list.add(new UIObject(context.getString(R.string.network_scan_always_available), wifiManager.isScanAlwaysAvailable() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));

            list.add(new UIObject(context.getString(R.string.network_tdls), wifiManager.isTdlsSupported() ?
                    ThreeState.YES : ThreeState.MAYBE, 2));


            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                list.add(new UIObject(context.getString(R.string.network_dpp), ThreeState.NO, 2));
                list.add(new UIObject(context.getString(R.string.network_owe), ThreeState.NO, 2));
                list.add(new UIObject(context.getString(R.string.network_sae), ThreeState.NO, 2));
                list.add(new UIObject(context.getString(R.string.network_enterprise_suite_b), ThreeState.NO, 2));

            } else {
                list.add(new UIObject(context.getString(R.string.network_dpp), wifiManager.isEasyConnectSupported() ?
                        ThreeState.YES : ThreeState.NO, 2));
                list.add(new UIObject(context.getString(R.string.network_owe), wifiManager.isEnhancedOpenSupported() ?
                        ThreeState.YES : ThreeState.NO, 2));
                list.add(new UIObject(context.getString(R.string.network_sae), wifiManager.isWpa3SaeSupported() ?
                        ThreeState.YES : ThreeState.NO, 2));
                list.add(new UIObject(context.getString(R.string.network_enterprise_suite_b), wifiManager.isWpa3SuiteBSupported() ?
                        ThreeState.YES : ThreeState.NO, 2));
            }
        }
        return list;
    }


    public static List<UIObject> getWifiInformation(Context context, boolean isLocationPermissionEnabled) {

        List<UIObject> list = new ArrayList<>();


        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            return list;
        }

        WifiInfo wifiInfo = null;
        try {
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }

        list.add(new UIObject(context.getString(R.string.network_wifi_info), "", 1));

        // WIFI Connected info
        String ssid = wifiInfo.getSSID().replaceAll("\"", "");
        if (!ssid.equals("0x")) {
            list.add(new UIObject(context.getString(R.string.network_ssid), ssid));
        } else {
            list.add(new UIObject(context.getString(R.string.network_ssid), context.getResources().getString(R.string.not_available_info)));
        }
        list.add(new UIObject(context.getString(R.string.network_bssid), String.valueOf(wifiInfo.getBSSID())));


        if (Build.VERSION.SDK_INT < 23) {
            list.add(new UIObject(context.getString(R.string.network_mac), String.valueOf(wifiInfo.getMacAddress())));
        }

        list.add(new UIObject(context.getString(R.string.network_rssi), String.valueOf(wifiInfo.getRssi()), "dBm"));

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            if (wifiInfo.getTxLinkSpeedMbps() > 0) {
                list.add(new UIObject(context.getString(R.string.network_tx_link_speed), String.valueOf(wifiInfo.getTxLinkSpeedMbps()), WifiInfo.LINK_SPEED_UNITS));
            }
            if (wifiInfo.getRxLinkSpeedMbps() > 0) {
                list.add(new UIObject(context.getString(R.string.network_rx_link_speed), String.valueOf(wifiInfo.getRxLinkSpeedMbps()), WifiInfo.LINK_SPEED_UNITS));
            }
        } else if (wifiInfo.getLinkSpeed() > 0) {
            list.add(new UIObject(context.getString(R.string.network_link_speed), String.valueOf(wifiInfo.getLinkSpeed()), WifiInfo.LINK_SPEED_UNITS));

        }
        list.add(new UIObject(context.getString(R.string.network_wifi_freq), String.valueOf(getFrequency(context, wifiInfo.getBSSID())), "MHz"));

        list.add(new UIObject(context.getString(R.string.network_supplicant_state), wifiInfo.getSupplicantState().name()));

        if (isLocationPermissionEnabled) {
            scanResult = getWiFiScanResult(context, wifiInfo.getBSSID());
        } else {
            scanResult = null;
        }
        if (scanResult != null) {
            list.add(new UIObject(context.getString(R.string.network_ap_capabilities), scanResult.capabilities));

            if (Build.VERSION.SDK_INT > 22) {
                if (scanResult.centerFreq0 > 0) {
                    list.add(new UIObject(context.getString(R.string.network_center_f0), String.valueOf(scanResult.centerFreq0), "MHz"));
                }
                if (scanResult.centerFreq1 > 0) {
                    list.add(new UIObject(context.getString(R.string.network_center_f1), String.valueOf(scanResult.centerFreq1), "MHz"));
                }
                if (!scanResult.operatorFriendlyName.equals("")) {
                    list.add(new UIObject(context.getString(R.string.network_passpoint_name), String.valueOf(scanResult.operatorFriendlyName)));
                }
                if (!scanResult.venueName.equals("")) {
                    list.add(new UIObject(context.getString(R.string.network_venue_name), String.valueOf(scanResult.venueName)));
                }
                list.add(new UIObject(context.getString(R.string.network_responder), scanResult.is80211mcResponder() ?
                        context.getString(R.string.yes_string) : context.getString(R.string.no_string)));
                list.add(new UIObject(context.getString(R.string.network_is_passpoint_network), scanResult.isPasspointNetwork() ?
                        context.getString(R.string.yes_string) : context.getString(R.string.no_string)));
                if (Build.VERSION.SDK_INT > 28) {
                    if (wifiInfo.getPasspointProviderFriendlyName() != null) {
                        list.add(new UIObject(context.getString(R.string.network_passpoint_friendly_name), wifiInfo.getPasspointProviderFriendlyName()));
                    }
                    if (wifiInfo.getPasspointFqdn() != null) {
                        list.add(new UIObject(context.getString(R.string.network_passpoint_domain_name), wifiInfo.getPasspointFqdn()));
                    }
                }
            }
        }

        String lastRoaming = "";
        if (bssidTemp != null && !bssidTemp.equals(wifiInfo.getBSSID())) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            bssidTemp = wifiInfo.getBSSID();
            lastRoaming = hour + ":" + String.format(Locale.ENGLISH, "%02d:%02d", minute, second);
        } else {
            bssidTemp = wifiInfo.getBSSID();
        }
        list.add(new UIObject(context.getString(R.string.network_roaming), lastRoaming));


        //dhcp address
        DhcpInfo dhcpInformation = wifiManager.getDhcpInfo();

        list.add(new UIObject(context.getString(R.string.network_dhcp), "", 1));

        list.add(new UIObject(context.getString(R.string.network_ip_address), intToInetAddress(dhcpInformation.ipAddress).getHostAddress()));

        list.add(new UIObject(context.getString(R.string.network_gateway_ip), intToInetAddress(dhcpInformation.gateway).getHostAddress()));
        list.add(new UIObject(context.getString(R.string.network_netmask), intToInetAddress(dhcpInformation.netmask).getHostAddress()));
        list.add(new UIObject(context.getString(R.string.network_dns1), intToInetAddress(dhcpInformation.dns1).getHostAddress()));
        list.add(new UIObject(context.getString(R.string.network_dns2), intToInetAddress(dhcpInformation.dns2).getHostAddress()));
        list.add(new UIObject(context.getString(R.string.network_dhcp_ip), intToInetAddress(dhcpInformation.serverAddress).getHostAddress()));
        list.add(new UIObject(context.getString(R.string.network_lease_duration), String.valueOf(dhcpInformation.leaseDuration), "s"));

        return list;
    }


    private static String getChannelWidth(Context context, int width) {
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
                return context.getResources().getString(R.string.not_available_info);
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

    private static int getFrequency(Context context, String bssid) {
        if (bssid == null)
            return -1;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    public static ScanResult getWiFiScanResult(Context context, String bssid) {
        if (bssid == null)
            return null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
}
