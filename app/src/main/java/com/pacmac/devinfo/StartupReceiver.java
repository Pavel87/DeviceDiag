package com.pacmac.devinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tutelatechnologies.sdk.framework.TutelaSDKFactory;


/**
 * Created by pacmac on 2019-05-14.
 */

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            TutelaSDKFactory.getTheSDK().initializeWithApiKey(DeviceDiagApp.REG_KEY, context.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
