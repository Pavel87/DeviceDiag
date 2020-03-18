package com.pacmac.devinfo.main;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.CheckAppVersionTask;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.UpToDateEnum;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "main_info";

    private MutableLiveData<List<UIObject>> mainInfo = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getMainInfo(Context context) {
        loadDisplayInfo(context);
        return mainInfo;
    }

    public List<UIObject> getMainInfoForExport() {
        return mainInfo.getValue();
    }

    private void loadDisplayInfo(Context context) {

        List<UIObject> list = new ArrayList<>();
        list.add(new UIObject("OS Version", MainUtils.getOsVersion()));
        list.add(new UIObject("Model", MainUtils.getModel()));
        list.add(new UIObject("Manufacturer", MainUtils.getManufacturer()));
        String sn = MainUtils.getSerialNumber(context);
        if (sn != null) {
            list.add(new UIObject("Serial Number", sn));
        }
        list.add(new UIObject("Build Number", MainUtils.getBuildNumber()));
        list.add(new UIObject("Hardware", MainUtils.getHardware()));
        list.add(new UIObject("Bootloader", MainUtils.getBootloader()));
        UIObject simCount = MainUtils.getSimCount(context);
        if (simCount != null) {
            list.add(simCount);
        }
        String radioFirmware = MainUtils.getRadioFirmware();
        if (radioFirmware != null) {
            list.add(new UIObject("Radio Firmware", radioFirmware));
        }
        list.add(new UIObject("Bootloader", MainUtils.getBootloader()));
        list.add(new UIObject("Device Language", MainUtils.getDeviceLanguageSetting()));
        list.add(new UIObject("Device Locale", MainUtils.getDeviceLanguageLocale()));
        mainInfo.postValue(list);
    }


    private CheckAppVersionTask checkAppVersionTask = null;

    private boolean doNotShowNewVersioDialog = false;

    public boolean doesAppNeedsUpgrade() {
        if (checkAppVersionTask != null) {
            return checkAppVersionTask.getStatus() == UpToDateEnum.NO;
        }
        return false;
    }

    public void checkIfAppIsUpToDate(Context context) {
        if (checkAppVersionTask == null) {
            checkAppVersionTask = new CheckAppVersionTask(context);
            checkAppVersionTask.checkIfAppUpToDate();
        }
    }

    public boolean isDoNotShowNewVersioDialog() {
        return doNotShowNewVersioDialog;
    }

    public void setDoNotShowNewVersioDialog(boolean doNotShowNewVersioDialog) {
        this.doNotShowNewVersioDialog = doNotShowNewVersioDialog;
    }
}
