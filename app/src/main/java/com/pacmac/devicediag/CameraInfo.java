package com.pacmac.devicediag;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class CameraInfo extends AppCompatActivity {

    private final String TAG = "DIAGPAC";
    TextView autoFocus, manualPP, manualSensor, capRaw, capFull, flashSupport, extSupport;
    TextView vertical, horizontal, focalLength, minMaxEV, zoomRatios, faceDetection, jpegQuality,
            focusAreas, smoothZoom, videoSnapshot, videoStab, autoExposure, autoWhiteBalance,
            picSizes, videoSizes, resPicAmount, resVidAmount;
    TableLayout tabGeneral;
    LinearLayout tabCamSpec;
    Camera camera;
    Spinner spinnner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_info);

        tabCamSpec = (LinearLayout) findViewById(R.id.tabCamSpec);
        tabGeneral = (TableLayout) findViewById(R.id.tabGeneral);

        vertical = (TextView) findViewById(R.id.vertical);
        horizontal = (TextView) findViewById(R.id.horizontal);
        focalLength = (TextView) findViewById(R.id.focalLength);
        minMaxEV = (TextView) findViewById(R.id.minMaxEV);
        zoomRatios = (TextView) findViewById(R.id.zoomRatios);
        faceDetection = (TextView) findViewById(R.id.faceDetection);
        focusAreas = (TextView) findViewById(R.id.focusAreas);
        smoothZoom = (TextView) findViewById(R.id.smoothZoom);
        videoSnapshot = (TextView) findViewById(R.id.videoSnapshot);
        videoStab = (TextView) findViewById(R.id.videoStab);
        autoExposure = (TextView) findViewById(R.id.autoExposure);
        autoWhiteBalance = (TextView) findViewById(R.id.autoWhiteBalance);
        jpegQuality = (TextView) findViewById(R.id.jpegQuality);
        picSizes = (TextView) findViewById(R.id.picSizes);
        videoSizes = (TextView) findViewById(R.id.videoSizes);
        resPicAmount = (TextView) findViewById(R.id.resPicAmount);
        resVidAmount = (TextView) findViewById(R.id.resVidAmount);

        autoFocus = (TextView) findViewById(R.id.autoFocus);
        manualPP = (TextView) findViewById(R.id.manualPP);
        manualSensor = (TextView) findViewById(R.id.manualSensor);
        capRaw = (TextView) findViewById(R.id.capRaw);
        capFull = (TextView) findViewById(R.id.capFull);
        flashSupport = (TextView) findViewById(R.id.flashSupport);
        extSupport = (TextView) findViewById(R.id.extSupport);


        spinnner = (Spinner) findViewById(R.id.spinner);


        if (checkCameraPresent(this)) {
            int amountOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            ArrayList<CharSequence> arrayList = new ArrayList<>();
            arrayList.add("General Information");

            for (int i = 0; i < amountOfCameras; i++) {

                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    arrayList.add((i + 1) + ": Facing Front");
                } else
                    arrayList.add((i + 1) + ": Facing Back");
            }

            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, arrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnner.setAdapter(adapter);
        }

// SPINNER LISTENER
        spinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int item, long l) {

                if (item > 0) { // 1 of the cammera selected
                    tabGeneral.setVisibility(View.GONE);
                    tabCamSpec.setVisibility(View.VISIBLE);
                    //get camera params
                    try {
                        camera = Camera.open(item - 1);
                        Camera.Parameters params = camera.getParameters();
                        camera.release();
                        getCameraSpecParams(params);

                    } catch (Exception ex) {
                        Log.e(TAG, "Camera cannot be aquired");
                    }

                } else {   /// GENERAL TAB SELECTED
                    tabCamSpec.setVisibility(View.GONE);
                    tabGeneral.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //general tab data:
        autoFocus.setText(checkCameraAutofocus(this));
        manualPP.setText(checkCameraPostProc(this));
        manualSensor.setText(checkCameraManSensor(this));
        capRaw.setText(checkCameraCapRaw(this));
        capFull.setText(checkFullLevel(this));
        flashSupport.setText(checkCameraFlash(this));
        extSupport.setText(checkExtCam(this));
    }

    //check if any camera is present

    public boolean checkCameraPresent(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true;
        else
            return false;
    }

    public String checkCameraAutofocus(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }

    public String checkCameraPostProc(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }

    public String checkCameraManSensor(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }

    public String checkCameraCapRaw(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_RAW))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }

    public String checkCameraFlash(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }

    public String checkExtCam(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }

    public String checkFullLevel(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_LEVEL_FULL))
            return getResources().getString(R.string.yes_string);
        else
            return getResources().getString(R.string.no_string);
    }


    //get camera specific params
    public void getCameraSpecParams(Camera.Parameters parameters) {


        float vertAngle = parameters.getVerticalViewAngle();
        float horizontalAngle = parameters.getHorizontalViewAngle();
        float focalLen = parameters.getFocalLength();
        float step = parameters.getExposureCompensationStep();
        int min = Math.round(step * parameters.getMinExposureCompensation());
        int max = Math.round(step * parameters.getMaxExposureCompensation());
        int jpegQ = parameters.getJpegQuality();
        int faces = parameters.getMaxNumDetectedFaces();

        picSizes.setText(getPicDetail(parameters));

        vertical.setText(vertAngle + "°");
        horizontal.setText(horizontalAngle + "°");
        focalLength.setText(focalLen + " mm");
        if (min != 0 || max != 0)
            minMaxEV.setText(min + "/" + max);
        else
            minMaxEV.setText("Not Supported");

        if (parameters.isZoomSupported()) {
            zoomRatios.setText("1x - " + getZoomRatios(parameters));
            if (parameters.isSmoothZoomSupported())
                smoothZoom.setText(getResources().getString(R.string.yes_string));
            else
                smoothZoom.setText(getResources().getString(R.string.no_string));
        }
        if (faces != 0)
            faceDetection.setText(faces + " max");
        else
            faceDetection.setText("Not Supported");

        focusAreas.setText(parameters.getMaxNumFocusAreas() + " max");

        if (parameters.isVideoSnapshotSupported())
            videoSnapshot.setText(getResources().getString(R.string.yes_string));
        else
            videoSnapshot.setText(getResources().getString(R.string.no_string));

        if (parameters.isVideoStabilizationSupported())
            videoStab.setText(getResources().getString(R.string.yes_string));
        else
            videoStab.setText(getResources().getString(R.string.no_string));

        if (parameters.isAutoExposureLockSupported())
            autoExposure.setText(getResources().getString(R.string.yes_string));
        else
            autoExposure.setText(getResources().getString(R.string.no_string));

        if (parameters.isAutoWhiteBalanceLockSupported())
            autoWhiteBalance.setText(getResources().getString(R.string.yes_string));
        else
            autoWhiteBalance.setText(getResources().getString(R.string.no_string));

        jpegQuality.setText(jpegQ + "%");

        videoSizes.setText(getVidDetail(parameters));


    }

    public String getZoomRatios(Camera.Parameters parameters) {

        List<Integer> zoomRatList = parameters.getZoomRatios();
        String zoomRange = (float) zoomRatList.get(parameters.getMaxZoom()) / 100 + "x";
        return zoomRange;
    }

    public String getPicDetail(Camera.Parameters parameters) {

        int thirdCol = 0;
        List<Camera.Size> picSizeList = parameters.getSupportedPictureSizes();
        StringBuilder sb = new StringBuilder();
        for (Camera.Size size : picSizeList) {
            if (thirdCol <2) {
                sb.append(size.width + "x" + size.height + "    ");
                thirdCol+=1;
            } else {
                sb.append(size.width + "x" + size.height + "\n");
                thirdCol = 0;
            }
        }
        //show amount of Pic resulutions
        resPicAmount.setText(picSizeList.size() + "");
        return sb.toString();
    }

    public String getVidDetail(Camera.Parameters parameters) {

        List<Camera.Size> vidSizeList = parameters.getSupportedVideoSizes();
        StringBuilder sb = new StringBuilder();
        int thirdCol = 0;
        for (Camera.Size size : vidSizeList) {
            if (thirdCol <2) {
                sb.append(size.width + "x" + size.height + "    ");
                thirdCol+=1;
            } else {
                sb.append(size.width + "x" + size.height + "\n");
                thirdCol = 0;
            }


        }
        //show amount of Video resulutions
        resVidAmount.setText(vidSizeList.size() + "");
        return sb.toString();
    }


}
