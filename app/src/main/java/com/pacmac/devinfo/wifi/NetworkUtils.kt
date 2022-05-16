package com.pacmac.devinfo.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*

/**
 * @Author: Pavel Machala
 * @Date: 2022-05-15
 */
object NetworkUtils {

    private var scanResult: ScanResult? = null
    private var bssidTemp: String? = ""


    fun getRadiosState(context: Context): List<UIObject>? {
        val list: MutableList<UIObject> = ArrayList()
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo?
        if (connMgr == null) {
            return list
        }
        list.add(UIObject(context.getString(R.string.network_radio_state), "", 1))

        // check WIFI state and if present in device
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            var wifiState = context.resources.getString(R.string.not_available_info)
            if (networkInfo!!.isConnected) {
                wifiState = context.resources.getString(R.string.connected_info)
            } else if (networkInfo.isAvailable) {
                wifiState = context.resources.getString(R.string.available_info)
            }
            list.add(UIObject(context.getString(R.string.network_wifi), wifiState))
        } else {
            list.add(
                UIObject(
                    context.getString(R.string.network_wifi),
                    context.resources.getString(R.string.not_present)
                )
            )
        }


        // check WAN state and if present in device
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            var mobileState = context.resources.getString(R.string.not_available_info)
            if (networkInfo != null && networkInfo.isConnected) {
                mobileState = context.resources.getString(R.string.connected_info)
            } else if (networkInfo != null && networkInfo.isAvailable) {
                mobileState = context.resources.getString(R.string.available_info)
            }
            list.add(UIObject(context.getString(R.string.network_mobile_data), mobileState))
        } else {
            list.add(
                UIObject(
                    context.getString(R.string.network_mobile_data),
                    context.resources.getString(R.string.not_present)
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connMgr.activeNetwork
            if (network != null) {
                val networkCapabilities = connMgr.getNetworkCapabilities(network)
                if (networkCapabilities != null) {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_link_down_bandwidth),
                            networkCapabilities.linkDownstreamBandwidthKbps.toString(),
                            "kbps"
                        )
                    )
                    list.add(
                        UIObject(
                            context.getString(R.string.network_up_bandwidth),
                            networkCapabilities.linkUpstreamBandwidthKbps.toString(),
                            "kbps"
                        )
                    )
                }
            }
        }
        return list
    }

    fun getWifiFeatures(context: Context): List<UIObject>? {
        // show WiFi detail
        val list: MutableList<UIObject> = ArrayList()
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager == null || !wifiManager.isWifiEnabled) {
            return list
        }
        if (Build.VERSION.SDK_INT >= 21) {
            list.add(UIObject(context.getString(R.string.network_supported_features), "", 1))
            list.add(
                UIObject(
                    context.getString(R.string.network_five_ghz_band),
                    if (wifiManager.is5GHzBandSupported) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.network_ap_rtt),
                    if (wifiManager.isDeviceToApRttSupported) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.network_power_reporting),
                    if (wifiManager.isEnhancedPowerReportingSupported) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.network_wifi_direct),
                    if (wifiManager.isP2pSupported) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.network_offloaded_conn_scan),
                    if (wifiManager.isPreferredNetworkOffloadSupported) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.network_scan_always_available),
                    if (wifiManager.isScanAlwaysAvailable) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.network_tdls),
                    if (wifiManager.isTdlsSupported) ThreeState.YES else ThreeState.MAYBE,
                    2
                )
            )
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                list.add(UIObject(context.getString(R.string.network_dpp), ThreeState.NO, 2))
                list.add(UIObject(context.getString(R.string.network_owe), ThreeState.NO, 2))
                list.add(UIObject(context.getString(R.string.network_sae), ThreeState.NO, 2))
                list.add(
                    UIObject(
                        context.getString(R.string.network_enterprise_suite_b),
                        ThreeState.NO,
                        2
                    )
                )
            } else {
                list.add(
                    UIObject(
                        context.getString(R.string.network_dpp),
                        if (wifiManager.isEasyConnectSupported) ThreeState.YES else ThreeState.NO,
                        2
                    )
                )
                list.add(
                    UIObject(
                        context.getString(R.string.network_owe),
                        if (wifiManager.isEnhancedOpenSupported) ThreeState.YES else ThreeState.NO,
                        2
                    )
                )
                list.add(
                    UIObject(
                        context.getString(R.string.network_sae),
                        if (wifiManager.isWpa3SaeSupported) ThreeState.YES else ThreeState.NO,
                        2
                    )
                )
                list.add(
                    UIObject(
                        context.getString(R.string.network_enterprise_suite_b),
                        if (wifiManager.isWpa3SuiteBSupported) ThreeState.YES else ThreeState.NO,
                        2
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_wifi_6ghz_band),
                            if (wifiManager.is6GHzBandSupported) ThreeState.YES else ThreeState.NO,
                            2
                        )
                    )
                    list.add(
                        UIObject(
                            context.getString(R.string.network_wifi_sta_ap_concurrency),
                            if (wifiManager.isStaApConcurrencySupported) ThreeState.YES else ThreeState.NO,
                            2
                        )
                    )
                    list.add(
                        UIObject(
                            context.getString(R.string.network_wifi_wapi_support),
                            if (wifiManager.isWapiSupported) ThreeState.YES else ThreeState.NO,
                            2
                        )
                    )
                    list.add(
                        UIObject(
                            context.getString(R.string.network_wifi_scan_throttling),
                            if (wifiManager.isScanThrottleEnabled) ThreeState.YES else ThreeState.NO,
                            2
                        )
                    )
                }
            }
        }
        return list
    }


    fun getDHCPInfo(context: Context): List<UIObject>? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager == null || !wifiManager.isWifiEnabled) {
            return null
        }
        val list: MutableList<UIObject> = ArrayList()

        //dhcp address
        val dhcpInformation = wifiManager.dhcpInfo
        list.add(UIObject(context.getString(R.string.network_dhcp), "", 1))
        list.add(
            UIObject(
                context.getString(R.string.network_ip_address),
                intToInetAddress(dhcpInformation.ipAddress).hostAddress
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.network_gateway_ip),
                intToInetAddress(dhcpInformation.gateway).hostAddress
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.network_netmask),
                intToInetAddress(dhcpInformation.netmask).hostAddress
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.network_dns1),
                intToInetAddress(dhcpInformation.dns1).hostAddress
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.network_dns2),
                intToInetAddress(dhcpInformation.dns2).hostAddress
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.network_dhcp_ip),
                intToInetAddress(dhcpInformation.serverAddress).hostAddress
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.network_lease_duration),
                dhcpInformation.leaseDuration.toString(),
                "s"
            )
        )
        return list
    }

    @SuppressLint("MissingPermission")
    fun getWifiInformation(
        context: Context,
        isLocationPermissionEnabled: Boolean
    ): List<UIObject>? {
        val list: MutableList<UIObject> = ArrayList()
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager == null || !wifiManager.isWifiEnabled) {
            if (wifiManager != null) {
                list.add(
                    UIObject(
                        context.getString(R.string.wifi_state),
                        getWifiStateString(context, wifiManager.wifiState)
                    )
                )
            }
            return list
        }
        var wifiInfo: WifiInfo? = null
        wifiInfo = try {
            wifiManager.connectionInfo
        } catch (e: Exception) {
            e.printStackTrace()
            return list
        }
        list.add(UIObject(context.getString(R.string.network_wifi_info), "", 1))

        // WIFI Connected info
        list.add(
            UIObject(
                context.getString(R.string.wifi_state),
                getWifiStateString(context, wifiManager.wifiState)
            )
        )

        val ssid = wifiInfo?.ssid?.replace("\"".toRegex(), "") ?: ""
        if (ssid != "0x") {
            list.add(UIObject(context.getString(R.string.network_ssid), ssid))
        } else {
            list.add(
                UIObject(
                    context.getString(R.string.network_ssid),
                    context.resources.getString(R.string.not_available_info)
                )
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            list.add(
                UIObject(
                    context.getString(R.string.wifi_standard),
                    getWifiStandardString(context, wifiInfo?.wifiStandard ?: 0)
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(
                UIObject(
                    context.getString(R.string.wifi_security_type),
                    getWifiSecurityType(context, wifiInfo?.currentSecurityType ?: -1)
                )
            )
        }

        list.add(
            UIObject(
                context.getString(R.string.network_bssid),
                wifiInfo?.bssid.toString()
            )
        )
        if (Build.VERSION.SDK_INT < 23) {
            list.add(
                UIObject(
                    context.getString(R.string.network_mac),
                    wifiInfo?.macAddress.toString()
                )
            )
        }
        list.add(
            UIObject(
                context.getString(R.string.network_rssi),
                wifiInfo?.rssi.toString(),
                "dBm"
            )
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                list.add(
                    UIObject(
                        context.getString(R.string.max_supported_tx_link_speed),
                        wifiInfo?.maxSupportedTxLinkSpeedMbps.toString(),
                        WifiInfo.LINK_SPEED_UNITS
                    )
                )
            }
            if ((wifiInfo?.txLinkSpeedMbps ?: 0) > 0) {
                list.add(
                    UIObject(
                        context.getString(R.string.network_tx_link_speed),
                        wifiInfo?.txLinkSpeedMbps.toString(),
                        WifiInfo.LINK_SPEED_UNITS
                    )
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                list.add(
                    UIObject(
                        context.getString(R.string.max_supported_rx_link_speed),
                        wifiInfo?.maxSupportedRxLinkSpeedMbps.toString(),
                        WifiInfo.LINK_SPEED_UNITS
                    )
                )
            }
            if ((wifiInfo?.rxLinkSpeedMbps ?: 0) > 0) {
                list.add(
                    UIObject(
                        context.getString(R.string.network_rx_link_speed),
                        wifiInfo?.rxLinkSpeedMbps.toString(),
                        WifiInfo.LINK_SPEED_UNITS
                    )
                )
            }
        } else if ((wifiInfo?.linkSpeed ?: 0) > 0) {
            list.add(
                UIObject(
                    context.getString(R.string.network_link_speed),
                    wifiInfo?.linkSpeed.toString(),
                    WifiInfo.LINK_SPEED_UNITS
                )
            )
        }
        val wifiFrequency = getFrequency(context, wifiInfo?.bssid)
        if (wifiFrequency > 0) {
            val channel = WifiChannel.getChannel(wifiFrequency)
            list.add(
                UIObject(
                    context.getString(R.string.network_wifi_freq),
                    getFrequency(context, wifiInfo?.bssid).toString(),
                    "MHz"
                )
            )
            if (channel != WifiChannel.UNKNOWN) {
                list.add(
                    UIObject(
                        context.getString(R.string.network_wifi_channel),
                        channel.channel.toString()
                    )
                )
            }
        }
        list.add(
            UIObject(
                context.getString(R.string.network_supplicant_state),
                wifiInfo?.supplicantState?.name
            )
        )
        scanResult = if (isLocationPermissionEnabled) {
            getWiFiScanResult(context, wifiInfo?.bssid)
        } else {
            null
        }
        if (scanResult != null) {
            list.add(
                UIObject(
                    context.getString(R.string.network_ap_capabilities),
                    scanResult!!.capabilities
                )
            )
            if (Build.VERSION.SDK_INT > 22) {
                list.add(
                    UIObject(
                        context.getString(R.string.wifi_channel_width),
                        getChannelWidth(context, scanResult!!.channelWidth)
                    )
                )
                if (scanResult!!.centerFreq0 > 0) {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_center_f0),
                            scanResult!!.centerFreq0.toString(),
                            "MHz"
                        )
                    )
                }
                if (scanResult!!.centerFreq1 > 0) {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_center_f1),
                            scanResult!!.centerFreq1.toString(),
                            "MHz"
                        )
                    )
                }
                if (scanResult!!.operatorFriendlyName != "") {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_passpoint_name),
                            scanResult!!.operatorFriendlyName.toString()
                        )
                    )
                }
                if (scanResult!!.venueName != "") {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_venue_name),
                            scanResult!!.venueName.toString()
                        )
                    )
                }
                list.add(
                    UIObject(
                        context.getString(R.string.network_responder),
                        if (scanResult!!.is80211mcResponder) context.getString(R.string.yes_string) else context.getString(
                            R.string.no_string
                        )
                    )
                )
                list.add(
                    UIObject(
                        context.getString(R.string.network_is_passpoint_network),
                        if (scanResult!!.isPasspointNetwork) context.getString(R.string.yes_string) else context.getString(
                            R.string.no_string
                        )
                    )
                )
                if (Build.VERSION.SDK_INT > 28) {
                    if (wifiInfo?.passpointProviderFriendlyName != null) {
                        list.add(
                            UIObject(
                                context.getString(R.string.network_passpoint_friendly_name),
                                wifiInfo.passpointProviderFriendlyName
                            )
                        )
                    }
                    if (wifiInfo?.passpointFqdn != null) {
                        list.add(
                            UIObject(
                                context.getString(R.string.network_passpoint_domain_name),
                                wifiInfo.passpointFqdn
                            )
                        )
                    }
                }
            }
        }
        var lastRoaming = ""
        if (bssidTemp != null && bssidTemp != wifiInfo?.bssid) {
            val cal = Calendar.getInstance()
            val hour = cal[Calendar.HOUR_OF_DAY]
            val minute = cal[Calendar.MINUTE]
            val second = cal[Calendar.SECOND]
            bssidTemp = wifiInfo?.bssid
            lastRoaming =
                hour.toString() + ":" + String.format(Locale.ENGLISH, "%02d:%02d", minute, second)
        } else {
            bssidTemp = wifiInfo?.bssid
        }
        list.add(UIObject(context.getString(R.string.network_roaming), lastRoaming))
        return list
    }


    private fun getWifiStateString(context: Context, state: Int): String? {
        return when (state) {
            WifiManager.WIFI_STATE_DISABLING -> context.getString(R.string.wifi_disabling)
            WifiManager.WIFI_STATE_DISABLED -> context.getString(R.string.wifi_disabled)
            WifiManager.WIFI_STATE_ENABLING -> context.getString(R.string.wifi_enabling)
            WifiManager.WIFI_STATE_ENABLED -> context.getString(R.string.wifi_enabled)
            else -> context.resources.getString(R.string.unknown)
        }
    }

    private fun getWifiStandardString(context: Context, standard: Int): String? {
        return when (standard) {
            ScanResult.WIFI_STANDARD_LEGACY -> context.getString(R.string.wifi_standard_abg)
            ScanResult.WIFI_STANDARD_11N -> context.getString(R.string.wifi_standard_n)
            ScanResult.WIFI_STANDARD_11AC -> context.getString(R.string.wifi_standard_ac)
            ScanResult.WIFI_STANDARD_11AX -> context.getString(R.string.wifi_standard_ax)
            else -> context.resources.getString(R.string.unknown)
        }
    }

    private fun getWifiSecurityType(context: Context, type: Int): String {
        context.resources.apply {
            return when (type) {
                WifiInfo.SECURITY_TYPE_OPEN -> getString(R.string.w_secirty_open)
                WifiInfo.SECURITY_TYPE_WEP -> getString(R.string.w_secirty_wep)
                WifiInfo.SECURITY_TYPE_PSK -> getString(R.string.w_secirty_psk)
                WifiInfo.SECURITY_TYPE_EAP -> getString(R.string.w_secirty_eap)
                WifiInfo.SECURITY_TYPE_SAE -> getString(R.string.w_secirty_sae)
                WifiInfo.SECURITY_TYPE_OWE -> getString(R.string.w_secirty_owe)
                WifiInfo.SECURITY_TYPE_WAPI_PSK -> getString(R.string.w_secirty_wapi_psk)
                WifiInfo.SECURITY_TYPE_WAPI_CERT -> getString(R.string.w_secirty_wapi_cert)
                WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE -> getString(R.string.w_secirty_eap_wpa_ent)
                WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE_192_BIT -> getString(R.string.w_secirty_eap_wpa_ent_192_bit)
                WifiInfo.SECURITY_TYPE_PASSPOINT_R1_R2 -> getString(R.string.w_secirty_pass_r1_r2)
                WifiInfo.SECURITY_TYPE_PASSPOINT_R3 -> getString(R.string.w_secirty_pass_r3)
//                WifiInfo.SECURITY_TYPE_DPP -> getString(R.string.w_secirty_dpp) // API 33 (TIRAMISU)
                13 -> getString(R.string.w_secirty_dpp) // API 33 (TIRAMISU)
                else -> context.resources.getString(R.string.unknown)
            }
        }
    }



    private fun getChannelWidth(context: Context, width: Int): String? {
        return when (width) {
            ScanResult.CHANNEL_WIDTH_20MHZ -> "20 MHz"
            ScanResult.CHANNEL_WIDTH_40MHZ -> "40 MHz"
            ScanResult.CHANNEL_WIDTH_80MHZ -> "80 MHz"
            ScanResult.CHANNEL_WIDTH_160MHZ -> "160 MHz"
            ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> "80 + 80 MHz"
            else -> context.resources.getString(R.string.unknown)
        }
    }


    //  conversion taken from stackoverflow: http://stackoverflow.com/questions/6345597/human-readable-dhcpinfo-ipaddress
    fun intToInetAddress(hostAddress: Int): InetAddress {
        val addressBytes = byteArrayOf(
            (0xff and hostAddress).toByte(),
            (0xff and (hostAddress shr 8)).toByte(),
            (0xff and (hostAddress shr 16)).toByte(),
            (0xff and (hostAddress shr 24)).toByte()
        )
        return try {
            InetAddress.getByAddress(addressBytes)
        } catch (e: UnknownHostException) {
            throw AssertionError()
        }
    }

    private fun getFrequency(context: Context, bssid: String?): Int {
        if (bssid == null) return -1
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                ?: return -1
        val wifiInfo = wifiManager.connectionInfo ?: return -1
        val frequency = -1

        // API 21+ has method to pull frequency channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // should we check for permissions in Manifest here ??
            return wifiInfo.frequency
            // Older android versions have to use getScanResults to get frequency
        } else {
            val wifiScanList = wifiManager.scanResults
            if (wifiScanList == null || wifiScanList.size == 0) return -1
            for (i in wifiScanList.indices) {
                if (bssid == wifiScanList[i].BSSID) {
                    return wifiScanList[i].frequency
                }
            }
        }
        return frequency
    }

    fun getWiFiScanResult(context: Context, bssid: String?): ScanResult? {
        if (bssid == null) return null
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ?: return null
        val wifiInfo = wifiManager.connectionInfo ?: return null
        val wifiScanList = wifiManager.scanResults
        if (wifiScanList == null || wifiScanList.size == 0) return null
        for (i in wifiScanList.indices) {
            if (bssid == wifiScanList[i].BSSID) {
                return wifiScanList[i]
            }
        }
        return null
    }

}