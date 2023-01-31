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

    public static final String EXPORT_FILE_NAME = "display_info";

    static UIObject getDensity(Context context, DisplayMetrics metrics) {
        return new UIObject(context.getString(R.string.display_density), String.format(Locale.ENGLISH, "%d", metrics.densityDpi), "dpi");
    }

    static UIObject getScaleFactor(Context context, DisplayMetrics metrics) {
        return new UIObject(context.getString(R.string.display_scale_factor), String.format(Locale.ENGLISH, "%.1f", metrics.density));
    }

    static UIObject getRefreshRate(Context context, Display display) {
        return new UIObject(context.getString(R.string.display_refresh_rate), String.format(Locale.ENGLISH, "%.1f", display.getRefreshRate()), "fps");
    }

    static List<UIObject> getResolution(Context context, Display display, DisplayMetrics metrics) {
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

        list.add(new UIObject(context.getString(R.string.display_resolution), String.format(Locale.ENGLISH, "%dx%d", screenWidth, screenHeight), "px"));
        list.add(new UIObject(context.getString(R.string.display_dimension), String.format(Locale.ENGLISH, "%.2fx%.2f", widthInch, heightInch), "in"));
        list.add(new UIObject(context.getString(R.string.display_dimension) + " [dp]", String.format(Locale.ENGLISH, "%.2fx%.2f",
                (160.0 * (float) screenWidth / metrics.densityDpi), (160.0 * (float) screenHeight / metrics.densityDpi)), "dp"));
        list.add(new UIObject(context.getString(R.string.display_diagonal), String.format(Locale.ENGLISH, "%.2f", diagonal), "in"));

        return list;
    }

    static UIObject getXYDpi(Context context, DisplayMetrics metrics) {
        return new UIObject(context.getString(R.string.display_xy_dpi), String.format(Locale.ENGLISH, "%.2fx%.2f", metrics.xdpi, metrics.ydpi));
    }

    static UIObject getOrientation(Context context, Display display) {
        return new UIObject(context.getString(R.string.display_orientation), String.format(Locale.ENGLISH, "%s",
                getOrient(context, display.getRotation())), "°");
    }

    static UIObject getLayoutSize(Context context) {
        return new UIObject(context.getString(R.string.display_size), String.format(Locale.ENGLISH, "%s", getLayoutQualifier(context)));
    }

    static UIObject getType(Context context, Display display) {
        return new UIObject(context.getString(R.string.display_type), String.format(Locale.ENGLISH, "%s", display.getName()));
    }

    static UIObject getDrawType(Context context, DisplayMetrics metrics) {
        return new UIObject(context.getString(R.string.display_draw_size), String.format(Locale.ENGLISH, "%s", densityQualifier(context, metrics.densityDpi)));
    }


    private static String getLayoutQualifier(Context context) {
        Configuration config = context.getResources().getConfiguration();

        switch (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return context.getString(R.string.display_small);
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return context.getString(R.string.display_normal);
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return context.getString(R.string.display_large);
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return context.getString(R.string.display_x_large);
            default:
                return context.getResources().getString(R.string.unknown);
        }
    }

    private static String densityQualifier(Context context, int densityDPI) {

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
                return context.getResources().getString(R.string.unknown);
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
