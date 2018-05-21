package com.pacmac.devinfo;

import android.app.Application;
import android.os.Build;

import com.tutelatechnologies.sdk.framework.TutelaSDKFactory;


/**
 * Created by pacmac on 2016-12-08.
 */


public class DeviceDiagApp extends Application {
    private final static String REG_KEY = "ieg9ioa9qhlbnff2714e6s1a8n";

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            try {
                TutelaSDKFactory.getTheSDK().initializeWithApiKey(REG_KEY, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}