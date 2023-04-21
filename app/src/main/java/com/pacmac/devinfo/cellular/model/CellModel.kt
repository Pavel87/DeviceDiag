package com.pacmac.devinfo.cellular.model

import android.content.Context
import android.os.Build
import android.telephony.CellIdentityNr
import android.telephony.CellInfo
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoTdscdma
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthNr
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.cellular.MobileNetworkUtilKt
import java.util.Arrays
import java.util.Locale

data class CellModel(
    val index: Int,
    val cellConnectionStatus: Int?,
    val cell: CellInfo
) {


    companion object {

        fun toUIModelList(context: Context, cells: List<CellInfo>?): List<UIObject> {
            cells ?: return emptyList()



            return getCellTowerInfo(context, cellInfos = cells)

        }

        fun getCellTowerInfo(context: Context, cellInfos: List<CellInfo>): List<UIObject> {
            val uiList = ArrayList<UIObject>()
            cellInfos.forEachIndexed { i, cell ->
                if (cell.isRegistered) {
                    uiList.add(UIObject("Cell", (i + 1).toString(), ListType.TITLE))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val cellConnectionStatus = cell.cellConnectionStatus
                        var status = context.resources.getString(R.string.not_available_info)
                        when (cellConnectionStatus) {
                            0 -> status = context.getString(R.string.none)
                            1 -> status = context.getString(R.string.primary_cell)
                            2 -> status = context.getString(R.string.secondary_cell)
                        }
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.connection_status),
                                status
                            )
                        )
                    }
                    if (cell is CellInfoLte) {
                        uiList.add(UIObject(context.resources.getString(R.string.cell_type), "LTE"))
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mcc),
                                    cell.cellIdentity.mccString.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mnc),
                                    cell.cellIdentity.mncString.toString()
                                )
                            )
                        } else {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mcc),
                                    cell.cellIdentity.mcc.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mnc),
                                    cell.cellIdentity.mnc.toString()
                                )
                            )
                        }
                        val cellId = cell.cellIdentity.ci
                        val tac = cell.cellIdentity.tac
                        val physCellId = cell.cellIdentity.pci
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.cid),
                                (if (cellId == Int.MAX_VALUE || cellId == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else cellId).toString()
                            )
                        )
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.tracking_area_code),
                                (if (tac == Int.MAX_VALUE || tac == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else tac).toString()
                            )
                        )
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.pci),
                                (if (physCellId == Int.MAX_VALUE || physCellId == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else physCellId).toString()
                            )
                        )
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            val rfcn = cell.cellIdentity.earfcn
                            if (rfcn != Int.MAX_VALUE && rfcn != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.earfcn),
                                        rfcn.toString(),
                                        "kHz"
                                    )
                                )
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.eutran_band),
                                        MobileNetworkUtilKt.getMobileBandForLTE(rfcn)
                                    )
                                )
                            }
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                            val bandwidth = cell.cellIdentity.bandwidth
                            if (bandwidth != Int.MAX_VALUE && bandwidth != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.bandwidth),
                                        bandwidth.toString()
                                    )
                                )
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val bands = cell.cellIdentity.bands
                            val plmnList = cell.cellIdentity.additionalPlmns
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.cell_bands),
                                    Arrays.toString(bands)
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.plmn_list),
                                    plmnList.toString()
                                )
                            )
                            val csg = cell.cellIdentity.closedSubscriberGroupInfo
                            if (csg != null) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.csg_identity),
                                        csg.csgIdentity.toString()
                                    )
                                )
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.csg_restriction),
                                        if (csg.csgIndicator) ThreeState.YES else ThreeState.NO,
                                        ListType.ICON
                                    )
                                )
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.csg_hnb_name),
                                        csg.homeNodebName
                                    )
                                )
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val rssi = cell.cellSignalStrength.rssi
                            if (rssi != Int.MAX_VALUE && rssi != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.lte_rssi),
                                        String.format(
                                            Locale.ENGLISH, "%d", rssi
                                        ),
                                        "dBm"
                                    )
                                )
                            }
                        }
                        val rsrp = cell.cellSignalStrength.dbm
                        if (rsrp != Int.MAX_VALUE && rsrp != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.lte_rsrp), String.format(
                                        Locale.ENGLISH, "%d", rsrp
                                    ), "dBm"
                                )
                            )
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                            val rsrq = cell.cellSignalStrength.rsrq
                            if (rsrq != Int.MAX_VALUE && rsrq != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.lte_rsrq),
                                        rsrq.toString()
                                    )
                                )
                            }
                            val rssnr = cell.cellSignalStrength.rssnr
                            if (rssnr != Int.MAX_VALUE && rssnr != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.lte_rssnr),
                                        rssnr.toString()
                                    )
                                )
                            }
                            val cqi = cell.cellSignalStrength.cqi
                            if (cqi != Int.MAX_VALUE && cqi >= 0) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.lte_cqi),
                                        cqi.toString()
                                    )
                                )
                            }
                        }
                        val ta = cell.cellSignalStrength.timingAdvance
                        if (ta >= 0 && ta != Int.MAX_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.ta),
                                    ta.toString()
                                )
                            )
                        }
                        val asuLevelINT = cell.cellSignalStrength.asuLevel
                        if (asuLevelINT != Int.MAX_VALUE && asuLevelINT != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.asu),
                                    asuLevelINT.toString()
                                )
                            )
                        }
                        val signalLevelINT = cell.cellSignalStrength.level
                        if (signalLevelINT != Int.MAX_VALUE && signalLevelINT != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.sig_level),
                                    signalLevelINT.toString()
                                )
                            )
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cell is CellInfoWcdma) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.cell_type),
                                "WCDMA"
                            )
                        )
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mcc),
                                    cell.cellIdentity.mccString.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mnc),
                                    cell.cellIdentity.mncString.toString()
                                )
                            )
                        } else {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mcc),
                                    cell.cellIdentity.mcc.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mnc),
                                    cell.cellIdentity.mnc.toString()
                                )
                            )
                        }
                        val cellId = cell.cellIdentity.cid
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.cid),
                                (if (cellId == Int.MAX_VALUE || cellId == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else cellId).toString()
                            )
                        )
                        val lac = cell.cellIdentity.lac
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.lac),
                                (if (lac == Int.MAX_VALUE || lac == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else lac).toString()
                            )
                        )
                        val psc = cell.cellIdentity.psc
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.psc),
                                (if (psc == Int.MAX_VALUE || psc == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else psc).toString()
                            )
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val uarfcn = cell.cellIdentity.uarfcn
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.uarfcn),
                                    (if (uarfcn == Int.MAX_VALUE || uarfcn == Int.MIN_VALUE) context.resources.getString(
                                        R.string.not_available_info
                                    ) else uarfcn).toString()
                                )
                            )
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val spn = cell.cellIdentity.operatorAlphaLong
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.spn),
                                    spn?.toString()
                                        ?: context.resources.getString(R.string.not_available_info)
                                )
                            )
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val plmnList = cell.cellIdentity.additionalPlmns
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.plmn_list),
                                    plmnList.toString()
                                )
                            )
                            val csg = cell.cellIdentity.closedSubscriberGroupInfo
                            if (csg != null) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.csg_identity),
                                        csg.csgIdentity.toString()
                                    )
                                )
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.csg_restriction),
                                        if (csg.csgIndicator) ThreeState.YES else ThreeState.NO,
                                        ListType.ICON
                                    )
                                )
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.csg_hnb_name),
                                        csg.homeNodebName
                                    )
                                )
                            }
                        }
                        val rssi = cell.cellSignalStrength.dbm
                        if (rssi != Int.MAX_VALUE && rssi != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.rssi),
                                    rssi.toString(),
                                    "dBm"
                                )
                            )
                        }
                        val asuLevelINT = cell.cellSignalStrength.asuLevel
                        if (asuLevelINT != Int.MAX_VALUE && asuLevelINT != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.asu),
                                    asuLevelINT.toString()
                                )
                            )
                        }
                        val signalLevelINT = cell.cellSignalStrength.level
                        if (signalLevelINT != Int.MAX_VALUE && signalLevelINT != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.sig_level),
                                    signalLevelINT.toString()
                                )
                            )
                        }
                    } else if (cell is CellInfoGsm) {
                        uiList.add(UIObject(context.resources.getString(R.string.cell_type), "GSM"))
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mcc),
                                    cell.cellIdentity.mccString.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mnc),
                                    cell.cellIdentity.mncString.toString()
                                )
                            )
                        } else {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mcc),
                                    cell.cellIdentity.mcc.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.mnc),
                                    cell.cellIdentity.mnc.toString()
                                )
                            )
                        }
                        val cellId = cell.cellIdentity.cid
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.cid),
                                (if (cellId == Int.MAX_VALUE || cellId == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else cellId).toString()
                            )
                        )
                        val lac = cell.cellIdentity.lac
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.lac),
                                (if (lac == Int.MAX_VALUE || lac == Int.MIN_VALUE) context.resources.getString(
                                    R.string.not_available_info
                                ) else lac).toString()
                            )
                        )
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            val rfcn = cell.cellIdentity.arfcn
                            if (rfcn != Int.MAX_VALUE && rfcn != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.arfcn),
                                        rfcn.toString()
                                    )
                                )
                            }
                            val bsic = cell.cellIdentity.bsic
                            if (bsic != Int.MAX_VALUE && bsic != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.bsic),
                                        bsic.toString()
                                    )
                                )
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val spn = cell.cellIdentity.operatorAlphaLong
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.spn),
                                    spn?.toString()
                                        ?: context.resources.getString(R.string.not_available_info)
                                )
                            )
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val plmnList = cell.cellIdentity.additionalPlmns
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.plmn_list),
                                    plmnList.toString()
                                )
                            )
                        }
                        val dbm = cell.cellSignalStrength.dbm
                        if (dbm != Int.MAX_VALUE && dbm != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.rssi),
                                    dbm.toString(),
                                    "dBm"
                                )
                            )
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                            val ta = cell.cellSignalStrength.timingAdvance
                            if (ta != Int.MAX_VALUE && ta != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.ta),
                                        ta.toString()
                                    )
                                )
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val ber = cell.cellSignalStrength.bitErrorRate
                            if (ber != Int.MAX_VALUE && ber != Int.MIN_VALUE) {
                                uiList.add(
                                    UIObject(
                                        context.resources.getString(R.string.ber),
                                        ber.toString()
                                    )
                                )
                            }
                        }
                        val asu = cell.cellSignalStrength.asuLevel
                        if (asu != Int.MAX_VALUE && asu != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.asu),
                                    asu.toString()
                                )
                            )
                        }
                        val level = cell.cellSignalStrength.level
                        if (level != Int.MAX_VALUE && level != Int.MIN_VALUE) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.sig_level),
                                    level.toString()
                                )
                            )
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell is CellInfoTdscdma) {
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.cell_type),
                            "TD-SCDMA"
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.mcc),
                            cell.cellIdentity.mccString.toString()
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.mnc),
                            cell.cellIdentity.mncString.toString()
                        )
                    )
                    val cellId = cell.cellIdentity.cid
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.cid),
                            (if (cellId == Int.MAX_VALUE || cellId == Int.MIN_VALUE) context.resources.getString(
                                R.string.not_available_info
                            ) else cellId).toString()
                        )
                    )
                    val lac = cell.cellIdentity.lac
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.lac),
                            (if (lac == Int.MAX_VALUE || lac == Int.MIN_VALUE) context.resources.getString(
                                R.string.not_available_info
                            ) else lac).toString()
                        )
                    )
                    val cpid = cell.cellIdentity.cpid
                    if (cpid != Int.MAX_VALUE && cpid != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.cpid),
                                cpid.toString()
                            )
                        )
                    }
                    val uarfcn = cell.cellIdentity.uarfcn
                    if (uarfcn != Int.MAX_VALUE && uarfcn != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.uarfcn),
                                uarfcn.toString()
                            )
                        )
                    }
                    val spn = cell.cellIdentity.operatorAlphaLong
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.spn),
                            spn?.toString()
                                ?: context.resources.getString(R.string.not_available_info)
                        )
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val plmnList = cell.cellIdentity.additionalPlmns
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.plmn_list),
                                plmnList.toString()
                            )
                        )
                        val csg = cell.cellIdentity.closedSubscriberGroupInfo
                        if (csg != null) {
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.csg_identity),
                                    csg.csgIdentity.toString()
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.csg_restriction),
                                    if (csg.csgIndicator) ThreeState.YES else ThreeState.NO,
                                    ListType.ICON
                                )
                            )
                            uiList.add(
                                UIObject(
                                    context.resources.getString(R.string.csg_hnb_name),
                                    csg.homeNodebName
                                )
                            )
                        }
                    }
                    val dbm = cell.cellSignalStrength.dbm
                    if (dbm != Int.MAX_VALUE && dbm != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.rscp),
                                dbm.toString(),
                                "dBm"
                            )
                        )
                    }
                    val asu = cell.cellSignalStrength.asuLevel
                    if (asu != Int.MAX_VALUE && asu != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.asu),
                                asu.toString()
                            )
                        )
                    }
                    val level = cell.cellSignalStrength.level
                    if (level != Int.MAX_VALUE && level != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.sig_level),
                                level.toString()
                            )
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell is CellInfoNr) {
                    uiList.add(UIObject(context.resources.getString(R.string.cell_type), "5G"))
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.mcc),
                            (cell.cellIdentity as CellIdentityNr).mccString.toString()
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.mnc),
                            (cell.cellIdentity as CellIdentityNr).mncString.toString()
                        )
                    )
                    val nci = (cell.cellIdentity as CellIdentityNr).nci
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.nci),
                            (if (nci == Int.MAX_VALUE.toLong() || nci == Int.MIN_VALUE.toLong()) context.resources.getString(
                                R.string.not_available_info
                            ) else nci).toString()
                        )
                    )
                    val tac = (cell.cellIdentity as CellIdentityNr).tac
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.tac),
                            (if (tac == Int.MAX_VALUE || tac == Int.MIN_VALUE) context.resources.getString(
                                R.string.not_available_info
                            ) else tac).toString()
                        )
                    )
                    val pci = (cell.cellIdentity as CellIdentityNr).pci
                    if (pci != Int.MAX_VALUE && pci != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.pci),
                                pci.toString()
                            )
                        )
                    }
                    val nrarfcn = (cell.cellIdentity as CellIdentityNr).nrarfcn
                    if (nrarfcn != Int.MAX_VALUE && nrarfcn != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.nrarfcn),
                                nrarfcn.toString()
                            )
                        )
                    }
                    val spn = cell.cellIdentity.operatorAlphaLong
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.spn),
                            spn?.toString()
                                ?: context.resources.getString(R.string.not_available_info)
                        )
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val bands = (cell.cellIdentity as CellIdentityNr).bands
                        val plmnList = (cell.cellIdentity as CellIdentityNr).additionalPlmns
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.cell_bands),
                                Arrays.toString(bands)
                            )
                        )
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.plmn_list),
                                plmnList.toString()
                            )
                        )
                    }
                    val nrSignal = cell.cellSignalStrength as CellSignalStrengthNr
                    val csiRSRP = nrSignal.csiRsrp
                    if (csiRSRP != Int.MAX_VALUE && csiRSRP != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.csi_rsrp),
                                csiRSRP.toString(),
                                "dBm"
                            )
                        )
                    }
                    val csiRSRQ = nrSignal.csiRsrq
                    if (csiRSRQ != Int.MAX_VALUE && csiRSRQ != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.csi_rsrq),
                                csiRSRQ.toString()
                            )
                        )
                    }
                    val csiSINR = nrSignal.csiSinr
                    if (csiSINR != Int.MAX_VALUE && csiSINR != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.csi_sinr),
                                csiSINR.toString(),
                                "dB"
                            )
                        )
                    }
                    val ssRSRP = nrSignal.ssRsrp
                    if (ssRSRP != Int.MAX_VALUE && ssRSRP != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.ss_rsrp),
                                ssRSRP.toString(),
                                "dBm"
                            )
                        )
                    }
                    val ssRSRQ = nrSignal.ssRsrq
                    if (ssRSRQ != Int.MAX_VALUE && ssRSRQ != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.ss_rsrq),
                                ssRSRQ.toString()
                            )
                        )
                    }
                    val ssSINR = nrSignal.ssSinr
                    if (ssSINR != Int.MAX_VALUE && ssSINR != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.ss_sinr),
                                ssSINR.toString(),
                                "dB"
                            )
                        )
                    }
                    val asu = nrSignal.asuLevel
                    if (asu != Int.MAX_VALUE && asu != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.asu),
                                asu.toString()
                            )
                        )
                    }
                    val level = nrSignal.level
                    if (level != Int.MAX_VALUE && level != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.sig_level),
                                level.toString()
                            )
                        )
                    }
                } else if (cell is CellInfoCdma) {
                    uiList.add(UIObject(context.resources.getString(R.string.cell_type), "CDMA"))
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.networkID),
                            cell.cellIdentity.networkId.toString()
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.systemID),
                            cell.cellIdentity.systemId.toString()
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.baseStation),
                            cell.cellIdentity.basestationId.toString()
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.latitude),
                            cell.cellIdentity.latitude.toString()
                        )
                    )
                    uiList.add(
                        UIObject(
                            context.resources.getString(R.string.longitude),
                            cell.cellIdentity.longitude.toString()
                        )
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val spn = cell.cellIdentity.operatorAlphaLong
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.spn),
                                spn?.toString()
                                    ?: context.resources.getString(R.string.not_available_info)
                            )
                        )
                    }
                    val cellSignalStrengthCdma = cell.cellSignalStrength
                    val rssi = cellSignalStrengthCdma.dbm
                    if (rssi != Int.MAX_VALUE && rssi != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.rssi),
                                rssi.toString(),
                                "dBm"
                            )
                        )
                    }
                    val ecioCDMA = cellSignalStrengthCdma.cdmaEcio
                    if (ecioCDMA != Int.MAX_VALUE && ecioCDMA != Int.MIN_VALUE && ecioCDMA != 0) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.ecio),
                                ecioCDMA.toString()
                            )
                        )
                    }
                    val ecioEVDO = cellSignalStrengthCdma.cdmaEcio
                    if (ecioEVDO != Int.MAX_VALUE && ecioEVDO != Int.MIN_VALUE && ecioEVDO != 0) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.ecio),
                                ecioEVDO.toString()
                            )
                        )
                    }
                    val evdoSnr = cellSignalStrengthCdma.evdoSnr
                    if (evdoSnr != Int.MAX_VALUE && evdoSnr != Int.MIN_VALUE && evdoSnr != 0) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.snr),
                                evdoSnr.toString()
                            )
                        )
                    }
                    val asu = cellSignalStrengthCdma.asuLevel
                    if (asu != Int.MAX_VALUE && asu != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.asu),
                                asu.toString()
                            )
                        )
                    }
                    val level = cellSignalStrengthCdma.level
                    if (level != Int.MAX_VALUE && level != Int.MIN_VALUE) {
                        uiList.add(
                            UIObject(
                                context.resources.getString(R.string.sig_level),
                                level.toString()
                            )
                        )
                    }
                }
            }
            return uiList
        }
    }
}
