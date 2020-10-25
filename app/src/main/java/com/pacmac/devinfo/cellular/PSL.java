package com.pacmac.devinfo.cellular;

import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyDisplayInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

public class PSL extends PhoneStateListener {

    private CellularViewModel cellularViewModel;
    private Context context;

    private static int overrideNetworkType = 0;

    static int getOverrideNetworkType() {
        return overrideNetworkType;
    }

    public PSL(CellularViewModel cellularViewModel, Context context) {
        super();
        this.cellularViewModel = cellularViewModel;
        this.context = context;
        overrideNetworkType = 0;
    }


    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        if (serviceState != null && cellularViewModel != null) {
            cellularViewModel.updateServiceState(serviceState, context);
        }
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        if (location != null) {
            cellularViewModel.getCellInfos(context);
        }
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        if (cellularViewModel != null) {
            cellularViewModel.refreshNetworkInfo(context);
        }
    }

    @Override
    public void onDataActivity(int direction) {
        if (cellularViewModel != null) {
            cellularViewModel.refreshNetworkInfo(context);
        }
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        if (cellInfo != null) {
            cellularViewModel.getCellInfos(context);
        }
    }

    @Override
    public void onUserMobileDataStateChanged(boolean enabled) {
        if (cellularViewModel != null) {
            cellularViewModel.refreshNetworkInfo(context);
        }
    }

    @Override
    public void onActiveDataSubscriptionIdChanged(int subId) {
        if (cellularViewModel != null) {
            cellularViewModel.refreshAll(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onDisplayInfoChanged(@NonNull TelephonyDisplayInfo telephonyDisplayInfo) {
        overrideNetworkType = telephonyDisplayInfo.getOverrideNetworkType();
        if (cellularViewModel != null) {
            cellularViewModel.refreshNetworkInfo(context);
        }
    }
}
