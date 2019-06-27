package com.pacmac.devinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.widget.ShareActionProvider;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pacmac on 2016-10-04.
 */


public class Utility {


    public final static int MY_PERMISSIONS_REQUEST = 8;
    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * This method will check if permission is granted at runtime
     */
    public static boolean checkPermission(Context context, String permission) {

        int status = PermissionChecker.checkSelfPermission(context, permission);
        if (status == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public static void requestPermissions(Activity activity, String permission) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, new String[]{permission}, MY_PERMISSIONS_REQUEST);
    }

    public static void displayExplanationForPermission(Activity act, String msg, final String permission) {

        final Activity mActivity = act;
        AlertDialog.Builder builder = new AlertDialog.Builder(act, 0)
                .setCancelable(true).setMessage(msg).setTitle("Missing Permission")
                .setPositiveButton((act.getResources().getString(R.string.request_perm)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(mActivity, permission);
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

    protected static String getDeviceProperty(String key) throws Exception {
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


    private static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " Byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " KB";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " MB";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " GB";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " TB";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " PB";
        if (size >= Eb) return floatForm((double) size / Eb) + " EB";

        return "convertion error";
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

        if (is5GPhone() && Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String extras = "If you see any information below this please forward this message to \"pacmac.dev@gmail.com\" as this information will help to " +
                    "improve this application while running on 5G capable phones. \n\n";
            String info5G = get5GExtraData(activity.getApplicationContext());
            ShareCompat.IntentBuilder.from(activity)
                    .setType("message/rfc822")
                    .addEmailTo("")
                    .addEmailBcc("pacmac.dev@gmail.com")
                    .setSubject(Build.MODEL + "\t-\t" + subject)
                    .setText(bodyText + "\n\n\n\n\n\n\n\n\n" + extras + info5G)
                    .setChooserTitle("Share via:")
                    .startChooser();
        } else {
            ShareCompat.IntentBuilder.from(activity)
                    .setType("message/rfc822")
                    .addEmailTo("")
                    .setSubject(Build.MODEL + "\t-\t" + subject)
                    .setText(bodyText)
                    .setChooserTitle("Share via:")
                    .startChooser();
        }
    }

    private static boolean is5GPhone() {
        return Build.MODEL.contains("SM-G977") || Build.MODEL.contains("GM1915");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String get5GExtraData(Context context) {
        StringBuilder sb = new StringBuilder("5G phone - info - only for test purpose\n");
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String allCellInfoString = telephonyManager.getAllCellInfo().toString();
            sb.append(allCellInfoString);

            sb.append("TM: ");
            Method[] tmMethods = telephonyManager.getClass().getDeclaredMethods();
            for (Method method : tmMethods) {
                sb.append(method.getName());
            }
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                Method[] ssMethods = telephonyManager.getServiceState().getClass().getDeclaredMethods();
                Field[] ssFields = telephonyManager.getServiceState().getClass().getDeclaredFields();

                sb.append("SS methods: ");
                for (Method method : ssMethods) {
                    sb.append(method.getName());
                }
                sb.append("SS fields: ");
                for (Field field : ssFields) {
                    sb.append(field.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}


