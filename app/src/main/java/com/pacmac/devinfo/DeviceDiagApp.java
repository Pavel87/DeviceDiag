package com.pacmac.devinfo;

import android.app.Application;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.tutelatechnologies.sdk.framework.TutelaSDKFactory;

/**
 * Created by pacmac on 2016-12-08.
 */
public class DeviceDiagApp extends Application {
    public final static String REG_KEY = "ieg9ioa9qhlbnff2714e6s1a8n";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            TutelaSDKFactory.getTheSDK().initializeWithApiKey(REG_KEY, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    if (adInfo != null) {
                        if(!adInfo.isLimitAdTrackingEnabled()) {
                            TutelaSDKFactory.getTheSDK().setAaid(adInfo.getId(), getApplicationContext());

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}