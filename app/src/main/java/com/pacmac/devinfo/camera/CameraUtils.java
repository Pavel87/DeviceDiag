package com.pacmac.devinfo.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.ThreeState;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraUtils {


    public static boolean hasAutoFocus(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }

    public static boolean hasFlash(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_FLASH);
    }

    public static boolean hasFrontFacingCamera(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_FRONT);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static boolean supportsExternalCamera(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_EXTERNAL);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean supportsAR(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_AR);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasManualPostProcessing(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasManualSensor(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasCapabilityRaw(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_CAPABILITY_RAW);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasFulHWCapabilityLevel(Context context) {
        return checkCameraFeature(context, PackageManager.FEATURE_CAMERA_LEVEL_FULL);
    }

    public static boolean checkCameraFeature(Context context, String feature) {
        if (context.getPackageManager().hasSystemFeature(feature))
            return true;
        else
            return false;
    }


    public static List<UIObject> getCameraSpecParams(Context context, Camera.Parameters parameters, Camera.CameraInfo cameraInfo) {

        List<UIObject> uList = new ArrayList<>();

        int camOrientation = cameraInfo.orientation;

        float vertAngle = parameters.getVerticalViewAngle();
        float horizontalAngle = parameters.getHorizontalViewAngle();
        float focalLen = parameters.getFocalLength();
        float step = parameters.getExposureCompensationStep();
        int min = Math.round(step * parameters.getMinExposureCompensation());
        int max = Math.round(step * parameters.getMaxExposureCompensation());
        int jpegQ = parameters.getJpegQuality();
        int faces = parameters.getMaxNumDetectedFaces();
        String sMinMaxEv, sSmoothZoom, maxZoomRatio;

        String position = "Rear Facing";
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            position = "Front Facing";
        }

        uList.add(new UIObject("Camera Position", position));
        uList.add(new UIObject("Vertical View Angle", String.format(Locale.ENGLISH, "%.02f", vertAngle), "°"));
        uList.add(new UIObject("Horizontal View Angle", String.format(Locale.ENGLISH, "%.02f", horizontalAngle), "°"));
        uList.add(new UIObject("Focal Length", String.format(Locale.ENGLISH, "%.02f", focalLen), "mm"));
        if (min != 0 || max != 0) {
            sMinMaxEv = min + "/" + max;
        } else {
            sMinMaxEv = context.getResources().getString(R.string.not_available_info);
        }
        uList.add(new UIObject("EV Min/Max", String.format(Locale.ENGLISH, "%s", sMinMaxEv)));

        if (parameters.isZoomSupported()) {
            maxZoomRatio = getMaxZoomRatio(parameters);
            if (parameters.isSmoothZoomSupported())
                sSmoothZoom = context.getResources().getString(R.string.yes_string);
            else
                sSmoothZoom = context.getResources().getString(R.string.no_string);
        } else {
            maxZoomRatio = context.getResources().getString(R.string.no_string);
            sSmoothZoom = context.getResources().getString(R.string.no_string);
        }
        uList.add(new UIObject("Maximum Zoom", maxZoomRatio, "x"));
        uList.add(new UIObject("Smooth Zoom", sSmoothZoom));
        uList.add(new UIObject("Camera Orientation", String.valueOf(camOrientation), "°"));
        uList.add(new UIObject("Face Detection", (faces != 0) ?
                String.valueOf(faces) : context.getResources().getString(R.string.not_supported), "max"));

        uList.add(new UIObject("Focus Areas", String.valueOf(parameters.getMaxNumFocusAreas()), "max"));

        uList.add(new UIObject("Video Snapshot Support", parameters.isVideoSnapshotSupported()
                ? ThreeState.YES : ThreeState.NO, 2));
        uList.add(new UIObject("Video Stabilization Support", parameters.isVideoStabilizationSupported()
                ? ThreeState.YES : ThreeState.NO, 2));
        uList.add(new UIObject("Auto-Exposure Lock Support", parameters.isAutoExposureLockSupported()
                ? ThreeState.YES : ThreeState.NO, 2));
        uList.add(new UIObject("Auto-White Balance Locking Support", parameters.isAutoWhiteBalanceLockSupported()
                ? ThreeState.YES : ThreeState.NO, 2));
        uList.add(new UIObject("JPEG Quality", String.valueOf(jpegQ), "%"));


//        picSizes.setText(getPicDetail(parameters));
//        videoSizes.setText(getVidDetail(parameters));
        return uList;
    }

    public static String getMaxZoomRatio(Camera.Parameters parameters) {
        List<Integer> zoomRatList = parameters.getZoomRatios();
        int zoom = zoomRatList.get(zoomRatList.size() - 1);
        return String.format(Locale.ENGLISH, "%.1f", zoom / 100.0);
    }


    public static List<ResolutionObject> getPictureResolutions(Camera.Parameters parameters) {

        List<ResolutionObject> picResList = new ArrayList<>();

        List<Camera.Size> picSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : picSizeList) {
            picResList.add(new ResolutionObject(size.width, size.height));
        }
        return picResList;
    }

    public static List<ResolutionObject> getVideoResolutions(Camera.Parameters parameters) {

        List<ResolutionObject> videoResList = new ArrayList<>();

        List<Camera.Size> vidSizeList = parameters.getSupportedVideoSizes();
        for (Camera.Size size : vidSizeList) {
            videoResList.add(new ResolutionObject(size.width, size.height));
        }
        return videoResList;
    }

}
