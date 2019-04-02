package com.pacmac.devinfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CameraInfo extends AppCompatActivity {

    private final String TAG = "DIAGPAC";
    private ImageView autoFocus, manualPP, manualSensor, capRaw, capFull, flashSupport, extSupport;
    private TextView vertical, horizontal, focalLength, minMaxEV, zoomRatios, faceDetection, jpegQuality,
            focusAreas, smoothZoom, orientation, videoSnapshot, videoStab, autoExposure, autoWhiteBalance,
            picSizes, videoSizes, resPicAmount, resVidAmount;
    private LinearLayout tabGeneral;
    private LinearLayout tabCamSpec;
    private Camera camera;
    private Spinner spinnner;
    private boolean isLoaded = false;
    private ShareActionProvider mShareActionProvider;

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private boolean isPermissionEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_info);

        tabCamSpec = (LinearLayout) findViewById(R.id.tabCamSpec);
        tabGeneral = findViewById(R.id.tabGeneral);

        vertical = (TextView) findViewById(R.id.vertical);
        horizontal = (TextView) findViewById(R.id.horizontal);
        focalLength = (TextView) findViewById(R.id.focalLength);
        minMaxEV = (TextView) findViewById(R.id.minMaxEV);
        zoomRatios = (TextView) findViewById(R.id.zoomRatios);
        faceDetection = (TextView) findViewById(R.id.faceDetection);
        focusAreas = (TextView) findViewById(R.id.focusAreas);
        smoothZoom = (TextView) findViewById(R.id.smoothZoom);
        orientation = (TextView) findViewById(R.id.camOrientation);
        videoSnapshot = (TextView) findViewById(R.id.videoSnapshot);
        videoStab = (TextView) findViewById(R.id.videoStab);
        autoExposure = (TextView) findViewById(R.id.autoExposure);
        autoWhiteBalance = (TextView) findViewById(R.id.autoWhiteBalance);
        jpegQuality = (TextView) findViewById(R.id.jpegQuality);
        picSizes = (TextView) findViewById(R.id.picSizes);
        videoSizes = (TextView) findViewById(R.id.videoSizes);
        resPicAmount = (TextView) findViewById(R.id.resPicAmount);
        resVidAmount = (TextView) findViewById(R.id.resVidAmount);

        autoFocus = (ImageView) findViewById(R.id.autoFocus);
        manualPP = (ImageView) findViewById(R.id.manualPP);
        manualSensor = (ImageView) findViewById(R.id.manualSensor);
        capRaw = (ImageView) findViewById(R.id.capRaw);
        capFull = (ImageView) findViewById(R.id.capFull);
        flashSupport = (ImageView) findViewById(R.id.flashSupport);
        extSupport = (ImageView) findViewById(R.id.extSupport);
        spinnner = (Spinner) findViewById(R.id.spinner);

        // Check if user disabled CAMERA permission at some point
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), CAMERA_PERMISSION);
        }


        if (isPermissionEnabled) {
            getCameraInfo();

        } else {
            Utility.displayExplanationForPermission(this, getResources().getString(R.string.cam_permission_msg), CAMERA_PERMISSION);
            //Utility.requestPermissions(this , CAMERA_PERMISSION);
        }
    }

    private void getCameraInfo() {
        if (checkCameraFeature(this, PackageManager.FEATURE_CAMERA)) {
            int amountOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            ArrayList<CharSequence> arrayList = new ArrayList<>();
            arrayList.add("General Info");

            for (int i = 0; i < amountOfCameras; i++) {

                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    arrayList.add((i + 1) + ": Front Cam");
                } else
                    arrayList.add((i + 1) + ": Rear Cam");
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
                        getCameraSpecParams(params, (item - 1));

                    } catch (Exception ex) {
                        Log.e(TAG, "Camera cannot be aquired");
                    }

                } else {   /// GENERAL TAB SELECTED
                    tabCamSpec.setVisibility(View.GONE);
                    tabGeneral.setVisibility(View.VISIBLE);
                    showGeneralInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        showGeneralInfo();
        isLoaded = true;
    }


    private void showGeneralInfo() {

        boolean sAutoFocus, sManualPP, sManualSensor, sCapFull, sCapRaw, sFlashSupport, sExtSupport;

        //general tab data + updating share Intent
        sAutoFocus = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_AUTOFOCUS);
        sManualPP = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING);
        sManualSensor = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR);
        sCapRaw = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_CAPABILITY_RAW);
        sCapFull = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_LEVEL_FULL);
        sFlashSupport = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_FLASH);
        sExtSupport = checkCameraFeature(this, PackageManager.FEATURE_CAMERA_EXTERNAL);

        if (!isLoaded) {
            if (sAutoFocus) {
                autoFocus.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                autoFocus.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }

            if (sManualPP) {
                manualPP.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                manualPP.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }
            if (sManualSensor) {
                manualSensor.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                manualSensor.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }

            if (sCapRaw) {
                capRaw.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                capRaw.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }

            if (sCapFull) {
                capFull.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                capFull.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }

            if (sFlashSupport) {
                flashSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                flashSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }
            if (sExtSupport) {
                extSupport.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            } else {
                extSupport.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            }
        }

        updateShareIntentGeneral(sAutoFocus, sManualPP, sManualSensor, sCapFull, sCapRaw, sFlashSupport, sExtSupport);
    }


    //check if camera feature is present

    public boolean checkCameraFeature(Context context, String feature) {
        if (context.getPackageManager().hasSystemFeature(feature))
            return true;
        else
            return false;
    }

    //get camera specific params
    public void getCameraSpecParams(Camera.Parameters parameters, int camIndex) {

        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(camIndex, camInfo);
        int camOrientation = camInfo.orientation;

        float vertAngle = parameters.getVerticalViewAngle();
        float horizontalAngle = parameters.getHorizontalViewAngle();
        float focalLen = parameters.getFocalLength();
        float step = parameters.getExposureCompensationStep();
        int min = Math.round(step * parameters.getMinExposureCompensation());
        int max = Math.round(step * parameters.getMaxExposureCompensation());
        int jpegQ = parameters.getJpegQuality();
        int faces = parameters.getMaxNumDetectedFaces();
        String sMinMaxEv, sSmoothZoom, sFaceDetection, sVideoSnapshot,
                sZoomRatios, sVideoStab, sAutoExposure, sAutoWhiteBalance;


        picSizes.setText(getPicDetail(parameters));

        vertical.setText(vertAngle + "°");
        horizontal.setText(horizontalAngle + "°");
        focalLength.setText(focalLen + " mm");

        if (min != 0 || max != 0)
            sMinMaxEv = min + "/" + max;
        else
            sMinMaxEv = "Not Supported";

        minMaxEV.setText(sMinMaxEv);

        if (parameters.isZoomSupported()) {
            sZoomRatios = "1x - " + getZoomRatios(parameters);
            if (parameters.isSmoothZoomSupported())
                sSmoothZoom = getResources().getString(R.string.yes_string);
            else
                sSmoothZoom = getResources().getString(R.string.no_string);
        } else {
            sZoomRatios = getResources().getString(R.string.no_string);
            sSmoothZoom = getResources().getString(R.string.no_string);
        }

        zoomRatios.setText(sZoomRatios);
        smoothZoom.setText(sSmoothZoom);

        if (faces != 0)
            sFaceDetection = faces + " max";
        else
            sFaceDetection = "Not Supported";

        faceDetection.setText(sFaceDetection);
        focusAreas.setText(parameters.getMaxNumFocusAreas() + " max");
        orientation.setText(camOrientation + " degrees");
        if (parameters.isVideoSnapshotSupported())
            sVideoSnapshot = getResources().getString(R.string.yes_string);
        else
            sVideoSnapshot = getResources().getString(R.string.no_string);

        videoSnapshot.setText(sVideoSnapshot);

        if (parameters.isVideoStabilizationSupported())
            sVideoStab = getResources().getString(R.string.yes_string);
        else
            sVideoStab = getResources().getString(R.string.no_string);

        videoStab.setText(sVideoStab);

        if (parameters.isAutoExposureLockSupported())
            sAutoExposure = getResources().getString(R.string.yes_string);
        else
            sAutoExposure = getResources().getString(R.string.no_string);

        autoExposure.setText(sAutoExposure);

        if (parameters.isAutoWhiteBalanceLockSupported())
            sAutoWhiteBalance = getResources().getString(R.string.yes_string);
        else
            sAutoWhiteBalance = getResources().getString(R.string.no_string);

        autoWhiteBalance.setText(sAutoWhiteBalance);

        jpegQuality.setText(jpegQ + "%");

        videoSizes.setText(getVidDetail(parameters));


        // update SHARE INTENT WITH CAM PARAMETERS
        StringBuilder sb = new StringBuilder();

        sb.append("Vertical View Angle:\t\t" + vertAngle + "°");
        sb.append("\n");
        sb.append("Horizontal View Angle:\t\t" + horizontalAngle + "°");
        sb.append("\n");
        sb.append("Focal Length:\t\t" + focalLen + " mm");
        sb.append("\n");
        sb.append("EV Min/Max:\t\t" + sMinMaxEv);
        sb.append("\n");
        sb.append("Zoom Ratio:\t\t" + sZoomRatios);
        sb.append("\n");
        sb.append("Smooth Zoom:\t\t" + sSmoothZoom);
        sb.append("\n");
        sb.append("Face Detection:\t\t" + sFaceDetection);
        sb.append("\n");
        sb.append("Focus Areas:\t\t" + parameters.getMaxNumFocusAreas() + " max");
        sb.append("\n");
        sb.append("Video Snapshot:\t\t" + sVideoSnapshot + "°");
        sb.append("\n");
        sb.append("Video Stabilization:\t\t" + sVideoStab + "°");
        sb.append("\n");
        sb.append("Auto Exposure Lock:\t\t" + sAutoExposure + "°");
        sb.append("\n");
        sb.append("Auto White Balance:\t\t" + sAutoWhiteBalance + "°");
        sb.append("\n");
        sb.append("JPEG Quality:\t\t" + jpegQ + "%");
        sb.append("\n\n");

        sb.append("Supported Picture Sizes [w x h]:\n" + getPicDetail(parameters));
        sb.append("\n");
        sb.append("Supported Video Sizes [w x h]:\n" + getVidDetail(parameters));
        sb.append("\n\n");
        updateShareIntentCamSpecific(sb);
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
            if (thirdCol < 2) {
                sb.append(size.width + "x" + size.height + "    ");
                thirdCol += 1;
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
            if (thirdCol < 2) {
                sb.append(size.width + "x" + size.height + "    ");
                thirdCol += 1;
            } else {
                sb.append(size.width + "x" + size.height + "\n");
                thirdCol = 0;
            }

        }
        //show amount of Video resulutions
        resVidAmount.setText(vidSizeList.size() + "");
        return sb.toString();
    }


    // SHARE VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shareTextEmpty));
        return shareIntent;
    }

    private Intent createShareIntent(StringBuilder sb) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Build.MODEL + "\t-\t"
                + getResources().getString(R.string.title_activity_camera_info));
        return shareIntent;
    }


    private void updateShareIntentGeneral(boolean sAutoFocus, boolean sManualPP,
                                          boolean sManualSensor, boolean sCapFull,
                                          boolean sCapRaw, boolean sFlashSupport,
                                          boolean sExtSupport) {

        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + "Camera General Info");
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");
        //body
        sb.append("Autofocus:\t\t" + Boolean.toString(sAutoFocus));
        sb.append("\n");
        sb.append("Manual Post Processing:\t\t" + Boolean.toString(sManualPP));
        sb.append("\n");
        sb.append("Manual Sensor:\t\t" + Boolean.toString(sManualSensor));
        sb.append("\n");
        sb.append("Capability Raw:\t\t" + Boolean.toString(sCapRaw));
        sb.append("\n");
        sb.append("Full HW Capability:\t\t" + Boolean.toString(sCapFull));
        sb.append("\n");
        sb.append("Support Flash:\t\t" + Boolean.toString(sFlashSupport));
        sb.append("\n");
        sb.append("Support External Camera:\t\t" + Boolean.toString(sExtSupport));
        sb.append("\n\n");

        sb.append(getResources().getString(R.string.shareTextTitle1));
        setShareIntent(createShareIntent(sb));
    }

    private void updateShareIntentCamSpecific(StringBuilder body) {

        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_camera_info));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");

        //body
        sb.append(body);

        sb.append(getResources().getString(R.string.shareTextTitle1));
        setShareIntent(createShareIntent(sb));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), CAMERA_PERMISSION);
        }
        if (isPermissionEnabled) {
            getCameraInfo();
        }
    }

}
