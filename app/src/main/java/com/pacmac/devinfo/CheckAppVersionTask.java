package com.pacmac.devinfo;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by pacmac on 2020-02-22.
 */
public class CheckAppVersionTask {

    private final String[] versions;
    private UpToDateEnum status = UpToDateEnum.UNKNOWN;

    private final static String APP_VERSION_DEFAULT = "0.0.0";
    private String appVersion = APP_VERSION_DEFAULT;

    public UpToDateEnum getStatus() {
        return status;
    }

    public CheckAppVersionTask(Context context) {
        this.appVersion = getCurrentAppVersion(context);
        this.versions = appVersion.split("\\.");
    }


    public void checkIfAppUpToDate() {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                String serverAppVersionString = downloadHttpContent();
                if (serverAppVersionString == null) {
                    return;
                }
                if (appVersion.equals(serverAppVersionString)) {
                    status = UpToDateEnum.YES;
                } else {
                    String[] serverVersions = serverAppVersionString.split("\\.");
                    for (int i = 0; i < serverVersions.length; i++) {
                        if (Integer.valueOf(versions[i]) < Integer.valueOf(serverVersions[i])) {
                            status = UpToDateEnum.NO;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private final static String APP_VERSION_CHECK_URL = "https://deviceinfo-23048.firebaseapp.com/version.html";


    /**
     * Method to retrieve last available APP version
     *
     * @return APP version in format x.x.x (expected format from server) or NULL if error
     */
    public String downloadHttpContent() {
        HttpsURLConnection httpsURLConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(APP_VERSION_CHECK_URL);

            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();

            InputStream inputStream = httpsURLConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            if ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getCurrentAppVersion(Context context) {

        if (appVersion.equals(APP_VERSION_DEFAULT)) {
            try {
                appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appVersion;
    }
}
