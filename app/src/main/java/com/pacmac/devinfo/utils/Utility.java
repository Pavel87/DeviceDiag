package com.pacmac.devinfo.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.UpToDateEnum;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by pacmac on 2016-10-04.
 */


public class Utility {

    public final static int MY_PERMISSIONS_REQUEST = 8;
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    public static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;


    public static String[] getLocationPermissions() {
        return new String[]{ACCESS_FINE_LOCATION};
    }

    /**
     * This method will check if permission is granted at runtime
     */
    public static boolean checkPermission(Context context, String permission) {

        int status = context.checkCallingOrSelfPermission(permission);
        if (status == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public static void requestPermissions(Activity activity, String[] permissions) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, permissions, MY_PERMISSIONS_REQUEST);
    }

    public static void displayExplanationForPermission(Activity act, String msg, final String[] permissions) {

        final Activity mActivity = act;
        AlertDialog.Builder builder = new AlertDialog.Builder(act, 0)
                .setCancelable(true).setMessage(msg).setTitle(R.string.missing_permission)
                .setPositiveButton((act.getResources().getString(R.string.request_perm)), (dialog, which) -> requestPermissions(mActivity, permissions))
                .setNegativeButton(act.getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static String getDeviceProperty(String key) throws Exception {
        String result = "";
        Class<?> systemPropClass = Class.forName("android.os.SystemProperties");

        Class<?>[] parameter = new Class[1];
        parameter[0] = String.class;
        Method getString = systemPropClass.getMethod("get", parameter);
        Object[] obParameter = new Object[1];
        obParameter[0] = key;

        Object output;
        if (getString != null) {
            output = getString.invoke(systemPropClass, obParameter);
            if (output != null) {
                result = output.toString();
            }
        }
        return result;
    }


    public static List<UIObject> getBuildPropsList(Context context) {

        List<UIObject> list = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec("getprop");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuilder sb = new StringBuilder();
            while ((i = reader.read(buffer)) > 0) {
                String line = new String(buffer, 0, i);
                sb.append(line);
            }

            String[] props = sb.toString().split("\n");

            for (String propRaw : props) {
                String[] propRawSplitted = propRaw.split(": ");
                String key = propRawSplitted[0].substring(1, propRawSplitted[0].length() - 1);
                String value = (propRawSplitted[1].length() > 2 ? propRawSplitted[1].substring(1, propRawSplitted[1].length() - 1) : context.getResources().getString(R.string.not_available_info));
                list.add(new UIObject(key, value));
            }
        } catch (Exception e) {
            // This can happen if timeout triggers
            e.printStackTrace();
        }
        return list;
    }

    public static boolean hasGPS(Context context) {
        PackageManager packageManager = context.getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        return hasGPS;
    }

    public static void launchPlayStore(Context context) {
        String appPackage = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
            context.startActivity(intent);
        }
    }

    public static void showUpdateAppDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        Button yesButton = dialog.findViewById(R.id.yesExit);
        yesButton.setOnClickListener(view -> {
            Utility.launchPlayStore(context);
            dialog.dismiss();
        });

        Button noButton = dialog.findViewById(R.id.noExit);
        noButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    private final static boolean isFreeOfAds = false;

    public static void showBannerAdView(Activity activity, View view, Context context, final int bannerID) {
        View adContainer = view.findViewById(R.id.adContainer);

        if (isFreeOfAds) {
            adContainer.setVisibility(View.GONE);
            return;
        }

        AdView mAdView = new AdView(context);

        AdSize adSize = getAdSize(activity, context, adContainer);
        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(context.getResources().getString(bannerID));
        ((LinearLayout) adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private static AdSize getAdSize(Activity activity, Context context, View view) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = view.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static UpToDateEnum hasVersionIncreased(String[] installedVersion, String serverAppVersionString) {
        String[] serverVersions = serverAppVersionString.split("\\.");
        for (int i = 0; i < serverVersions.length; i++) {
            if (Integer.parseInt(installedVersion[i]) < Integer.parseInt(serverVersions[i])) {
                return UpToDateEnum.NO;
            } else if (Integer.parseInt(installedVersion[i]) > Integer.parseInt(serverVersions[i])) {
                return UpToDateEnum.YES;
            }
        }
        return UpToDateEnum.YES;
    }

    public static int calculateNoOfColumns(Context context) { // For example columnWidthdp=180
        float columnWidthDp = 110f;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) ((screenWidthDp - 100f) / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }



    public static String formatTimeForNMEA(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS ", Locale.getDefault());
        return simpleDateFormat.format(new Date(timestamp));
    }
}


