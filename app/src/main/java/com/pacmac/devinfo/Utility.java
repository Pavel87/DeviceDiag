package com.pacmac.devinfo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Build;
import android.view.Window;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.PermissionChecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pacmac on 2016-10-04.
 */


public class Utility {

    public final static int MY_PERMISSIONS_REQUEST = 8;
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    public static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;


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
                .setCancelable(true).setMessage(msg).setTitle("Missing Permission")
                .setPositiveButton((act.getResources().getString(R.string.request_perm)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(mActivity, permissions);
                    }
                })
                .setNegativeButton(act.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static String getSensorName(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                return "Acccelerometer";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Magnetic Field";
            case Sensor.TYPE_ORIENTATION:
                return "Orientation";
            case Sensor.TYPE_GYROSCOPE:
                return "Gyroscope";
            case Sensor.TYPE_LIGHT:
                return "Light";
            case Sensor.TYPE_PRESSURE:
                return "Pressure";
            case Sensor.TYPE_TEMPERATURE:
                return "Temperature";
            case Sensor.TYPE_PROXIMITY:
                return "Proximity";
            case Sensor.TYPE_GRAVITY:
                return "Gravity";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Linear Acceleration";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Rotation Vector";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Relative Humidity";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Ambient Temperature";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "Mag. Field Uncalibrated";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "Game Rotation Vector";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "Gyroscope Uncalibrated";
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "Significant Motion Trigger";
            case Sensor.TYPE_STEP_DETECTOR:
                return "Step Detector";
            case Sensor.TYPE_STEP_COUNTER:
                return "Step Counter";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "Geomagnetic Rotation";
            case Sensor.TYPE_HEART_RATE:
                return "Heart Rate";
            case Sensor.TYPE_POSE_6DOF:
                return "POSE 6DOF";
            case Sensor.TYPE_STATIONARY_DETECT:
                return "Stationary Detector";
            case Sensor.TYPE_MOTION_DETECT:
                return "Motion Detector";
            case Sensor.TYPE_HEART_BEAT:
                return "Heart Beat";
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return "Low Latency Off-Body";
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return "Accelerometer Uncalibrated";
            case SensorsInfo.TYPE_TILT_DETECTOR:
                return "Tilt Detector";
            case SensorsInfo.TYPE_WAKE_GESTURE:
                return "Wake Gesture Detector";
            case SensorsInfo.TYPE_PICK_UP_GESTURE:
                return "Pick Up Detector";
            case SensorsInfo.TYPE_GLANCE_GESTURE:
                return "Glance Gesture";
            case SensorsInfo.TYPE_WRIST_TILT_GESTURE:
                return "Wrist Tilt";
            case SensorsInfo.TYPE_DEVICE_ORIENTATION:
                return "Device Orientation";
            case SensorsInfo.TYPE_DYNAMIC_SENSOR_META:
                return "Dynamic Sensor Meta";
            case SensorsInfo.TYPE_BASIC_GESTURES:
                return "Basic Gestures";
            case SensorsInfo.TYPE_TAP:
                return "Tap";
            case SensorsInfo.TYPE_FACING:
                return "Facing";
            case SensorsInfo.TYPE_TILT:
                return "Tilt";
            case SensorsInfo.TYPE_ABSOLUTE_MOTION_DETECTOR:
                return "Absolute Motion";
            case SensorsInfo.TYPE_RELATIVE_MOTION_DETECTOR:
                return "Relative Motion";
            case SensorsInfo.TYPE_PEDOMETER:
                return "Pedometer";
            case SensorsInfo.TYPE_PEDESTRIAN_ACTIVITY_MONITOR:
                return "Pedestrian Monitor";
            case SensorsInfo.TYPE_CYWEE_HAND_UP:
                return "Hand Up Sensor";
            case SensorsInfo.TYPE_CYWEE_PICKUP:
                return "Pick Up Sensor";
            case SensorsInfo.TYPE_CYWEE_FLIP:
                return "Flip Sensor";
            case SensorsInfo.TYPE_GOOGLE_TEMPERATURE_BOSH:
                return "Temperature Sensor";
            case SensorsInfo.TYPE_GOOGLE_SENSORS_SYNC:
                return "Sensors Sync";
            case SensorsInfo.TYPE_GOOGLE_DOUBLE_TWIST:
                return "Double Twist";
            case SensorsInfo.TYPE_GOOGLE_DOUBLE_TAP:
                return "Double Tap";
            case SensorsInfo.TYPE_MOTION_ACCEL:
                return "Motion Accel";
            case SensorsInfo.TYPE_COARSE_MOTION_CLASSIFIER:
                return "Coarse Motion Classifier";
            default:
                return String.valueOf(type);
//            default: return "Unknown";
        }
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



    public static List<BuildProperty> getBuildPropsList(Context context) {

        List<BuildProperty> list = new ArrayList<>();

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
                list.add(new BuildProperty(key, value));
            }
        } catch (Exception e) {
            // This can happen if timeout triggers
            e.printStackTrace();
        }
        return list;
    }

    static void exporData(Activity activity, String subject, String bodyText) {
        ShareCompat.IntentBuilder.from(activity)
                .setType("message/rfc822")
                .addEmailTo("")
                .setSubject(Build.MODEL + "\t-\t" + subject)
                .setText(bodyText)
                .setChooserTitle("Share via:")
                .startChooser();
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

}


