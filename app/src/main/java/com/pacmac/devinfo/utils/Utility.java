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


}


