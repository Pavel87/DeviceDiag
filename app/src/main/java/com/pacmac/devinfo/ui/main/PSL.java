package com.pacmac.devinfo.ui.main;

import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;

import java.util.List;

public class PSL extends PhoneStateListener {


    public PSL() {
        super();
    }


    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
    }

    @Override
    public void onDataActivity(int direction) {
        super.onDataActivity(direction);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
    }

    @Override
    public void onUserMobileDataStateChanged(boolean enabled) {
        super.onUserMobileDataStateChanged(enabled);
    }

    @Override
    public void onActiveDataSubscriptionIdChanged(int subId) {
        super.onActiveDataSubscriptionIdChanged(subId);
    }
}
