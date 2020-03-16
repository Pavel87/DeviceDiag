package com.pacmac.devinfo.display;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DisplayUtils {

    static UIObject getDensity(DisplayMetrics metrics) {
        return new UIObject("Density", String.format(Locale.ENGLISH, "%d", metrics.densityDpi), "dpi");
    }

    static UIObject getScaleFactor(DisplayMetrics metrics) {
        return new UIObject("Scale Factor", String.format(Locale.ENGLISH, "%f", metrics.density));
    }

    static UIObject getRefreshRate(Display display) {
        return new UIObject("Refresh Rate", String.format(Locale.ENGLISH, "%.2f", display.getRefreshRate()));
    }

    static List<UIObject> getResolution(Display display, DisplayMetrics metrics) {
        List<UIObject> list = new ArrayList<>();
        Point size = new Point();
//        int screenWidth = metrics.widthPixels;
//        int screenHeight = metrics.heightPixels;
        display.getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        double widthInch = (double) screenWidth / (double) metrics.xdpi;
        double heightInch = (double) screenHeight / (double) metrics.ydpi;
        double widthDP = Math.pow(widthInch, 2);
        double heightDP = Math.pow(heightInch, 2);
        double diagonal = Math.sqrt(widthDP + heightDP);

        list.add(new UIObject("Resolution", String.format(Locale.ENGLISH, "%dx%d", screenWidth, screenHeight), "px"));
        list.add(new UIObject("Dimensions", String.format(Locale.ENGLISH, "%.2fx%.2f", widthInch, heightInch), "in"));
        list.add(new UIObject("Dimensions (DP)", String.format(Locale.ENGLISH, "%.2fx%.2f",
                (160.0 * (float) screenWidth / metrics.densityDpi), (160.0 * (float) screenHeight / metrics.densityDpi)), "dp"));
        list.add(new UIObject("Diagonal", String.format(Locale.ENGLISH, "%.3f", diagonal), "in"));

        return list;
    }

    static UIObject getXYDpi(DisplayMetrics metrics) {
        return new UIObject("X/Y DPI", String.format(Locale.ENGLISH, "%.2fx%.2f", metrics.xdpi, metrics.ydpi));
    }

    static UIObject getOrientation(Context context, Display display) {
        return new UIObject("Orientation", String.format(Locale.ENGLISH, "%s",
                getOrient(context, display.getRotation())), "°");
    }

    static UIObject getLayoutSize(Context context) {
        return new UIObject("Layout Size", String.format(Locale.ENGLISH, "%s", getLayoutQualifier(context)));
    }

    static UIObject getType(Display display) {
        return new UIObject("Type", String.format(Locale.ENGLISH, "%s", display.getName()));
    }

    static UIObject getDrawType(DisplayMetrics metrics) {
        return new UIObject("Draw Size", String.format(Locale.ENGLISH, "%s", densityQualifier(metrics.densityDpi)));
    }


    private static String getLayoutQualifier(Context context) {
        Configuration config = context.getResources().getConfiguration();

        switch (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "Small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "Normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "Large";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "Xlarge";
            default:
                return "Undefined";
        }
    }

    private static String densityQualifier(int densityDPI) {

        switch (densityDPI) {
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "MDPI";
            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_340:
                return "340DPI XHDPI - XXHDPI";
            case DisplayMetrics.DENSITY_360:
                return "360DPI XHDPI - XXHDPI";
            case DisplayMetrics.DENSITY_400:
                return "400DPI XHDPI - XXHDPI";
            case DisplayMetrics.DENSITY_420:
                return "420DPI XHDPI - XXHDPI";
            case DisplayMetrics.DENSITY_440:
                return "440DPI XHDPI - XXHDPI";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
            case DisplayMetrics.DENSITY_560:
                return "440DPI XXHDPI - XXXHDPI";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "XXXHDPI";
            case DisplayMetrics.DENSITY_TV:
                return "TVDPI";
            case DisplayMetrics.DENSITY_260:
                return "260DPI HDPI - XHDPI";
            case DisplayMetrics.DENSITY_280:
                return "280DPI HDPI - XHDPI";
            case DisplayMetrics.DENSITY_300:
                return "300DPI HDPI - XHDPI";
            default:
                return "UNDEFINED";
        }
    }

    private static String getOrient(Context context, int i) {

        switch (i) {
            case 0:
                return "0";
            case 1:
                return "90";
            case 2:
                return "180";
            case 3:
                return "270";
        }
        return context.getResources().getString(R.string.not_available_info);
    }
}
