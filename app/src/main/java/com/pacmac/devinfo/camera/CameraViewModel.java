package com.pacmac.devinfo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.ThreeState;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraViewModel extends ViewModel {

    public static String EXPORT_FILE_NAME = "camera_info";

    private MutableLiveData<List<UIObject>> cameraInfoGeneral = new MutableLiveData<>();
    private MutableLiveData<List<List<UIObject>>> cameraListData = new MutableLiveData<>();
    private MutableLiveData<List<List<ResolutionObject>>> cameraListPicResolutions = new MutableLiveData<>();
    private MutableLiveData<List<List<ResolutionObject>>> cameraListVideoResolutions = new MutableLiveData<>();

    public MutableLiveData<List<List<ResolutionObject>>> getCameraListPicResolutions() {
        return cameraListPicResolutions;
    }

    public MutableLiveData<List<List<ResolutionObject>>> getCameraListVideoResolutions() {
        return cameraListVideoResolutions;
    }

    public MutableLiveData<List<List<UIObject>>> getCameraListData() {
        return cameraListData;
    }

    public MutableLiveData<List<UIObject>> getCameraInfoGeneral(Context context) {
        loadCameraInfo(context);
        return cameraInfoGeneral;
    }


    public List<UIObject> getCameraDataForExport(Context context) {
        List<UIObject> list = new ArrayList<>();

        if (cameraInfoGeneral.getValue() != null) {
            list.add(new UIObject(context.getString(R.string.camera_general_info), "", 1));
            list.addAll(cameraInfoGeneral.getValue());
        }

        if (cameraListData.getValue() != null) {
            for (int i = 0; i < cameraListData.getValue().size(); i++) {
                list.add(new UIObject("", "", 1));
                list.add(new UIObject(String.format(Locale.ENGLISH, context.getString(R.string.camera_id_title), i + 1), "", 1));
                list.addAll(cameraListData.getValue().get(i));

                if (cameraListPicResolutions.getValue() != null) {
                    list.add(new UIObject(context.getString(R.string.camera_supported_picture_size), "", 1));
                    list.add(new UIObject(context.getString(R.string.width), context.getString(R.string.height), 1));
                    for (int a = 0; a < cameraListPicResolutions.getValue().get(i).size(); a++) {
                        list.add(new UIObject(String.valueOf(cameraListPicResolutions.getValue().get(i).get(a).getWidth()),
                                String.valueOf(cameraListPicResolutions.getValue().get(i).get(a).getHeight())));
                    }
                }

                if (cameraListVideoResolutions.getValue() != null) {
                    list.add(new UIObject(context.getString(R.string.camera_supported_video_size), "", 1));
                    list.add(new UIObject(context.getString(R.string.width), context.getString(R.string.height), 1));
                    for (int a = 0; a < cameraListVideoResolutions.getValue().get(i).size(); a++) {
                        list.add(new UIObject(String.valueOf(cameraListVideoResolutions.getValue().get(i).get(a).getWidth()),
                                String.valueOf(cameraListVideoResolutions.getValue().get(i).get(a).getHeight())));
                    }
                }
            }
        }
        return list;
    }


    // TODO explore ##CameraCharacteristics##
    private void loadCameraInfo(Context context) {
        List<UIObject> list = new ArrayList<>();

        list.add(new UIObject(context.getString(R.string.camera_autofocus), CameraUtils.hasAutoFocus(context) ? ThreeState.YES : ThreeState.NO, 2));
        list.add(new UIObject(context.getString(R.string.camera_flash), CameraUtils.hasFlash(context) ? ThreeState.YES : ThreeState.NO, 2));
        list.add(new UIObject(context.getString(R.string.camera_front_facing_feature), CameraUtils.hasFrontFacingCamera(context) ? ThreeState.YES : ThreeState.NO, 2));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list.add(new UIObject(context.getString(R.string.camera_external_support), CameraUtils.supportsExternalCamera(context) ? ThreeState.YES : ThreeState.NO, 2));
            list.add(new UIObject(context.getString(R.string.camera_post_processing), CameraUtils.hasManualPostProcessing(context) ? ThreeState.YES : ThreeState.NO, 2));
            list.add(new UIObject(context.getString(R.string.camera_manual_sensor), CameraUtils.hasManualSensor(context) ? ThreeState.YES : ThreeState.NO, 2));
            list.add(new UIObject(context.getString(R.string.camera_capability_raw), CameraUtils.hasCapabilityRaw(context) ? ThreeState.YES : ThreeState.NO, 2));
            list.add(new UIObject(context.getString(R.string.camera_full_hw_capability_level), CameraUtils.hasFulHWCapabilityLevel(context) ? ThreeState.YES : ThreeState.NO, 2));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                list.add(new UIObject(context.getString(R.string.camera_ar_support), CameraUtils.supportsAR(context) ? ThreeState.YES : ThreeState.NO, 2));
            }
        }
        cameraInfoGeneral.postValue(list);
    }


    public void initializeCameras(Context context) {
        new Thread(() -> {
            List<List<UIObject>> cameraList = new ArrayList<>();
            List<List<ResolutionObject>> picResList = new ArrayList<>();
            List<List<ResolutionObject>> videoResList = new ArrayList<>();
            int count = CameraUtils.cameraCount;
            for (int i = 0; i < count; i++) {
                Camera camera = Camera.open(i);
                Camera.Parameters params = camera.getParameters();
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);
                camera.release();
                cameraList.add(CameraUtils.getCameraSpecParams(context, params, cameraInfo));

                picResList.add(CameraUtils.getPictureResolutions(params));
                videoResList.add(CameraUtils.getVideoResolutions(params));

            }
            cameraListData.postValue(cameraList);
            cameraListPicResolutions.postValue(picResList);
            cameraListVideoResolutions.postValue(videoResList);
        }).start();
    }
}