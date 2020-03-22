package com.pacmac.devinfo.main;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.CheckAppVersionTask;
import com.pacmac.devinfo.R;
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
        list.add(new UIObject(context.getString(R.string.os_version), MainUtils.getOsVersion()));
        list.add(new UIObject(context.getString(R.string.device_model), MainUtils.getModel()));
        list.add(new UIObject(context.getString(R.string.device_manufacturer), MainUtils.getManufacturer()));
        String sn = MainUtils.getSerialNumber(context);
        if (sn != null) {
            list.add(new UIObject(context.getString(R.string.device_sn), sn));
        }
        list.add(new UIObject(context.getString(R.string.device_build_number), MainUtils.getBuildNumber()));
        list.add(new UIObject(context.getString(R.string.device_hardware), MainUtils.getHardware()));
        UIObject simCount = MainUtils.getSimCount(context);
        if (simCount != null) {
            list.add(simCount);
        }
        String radioFirmware = MainUtils.getRadioFirmware();
        if (radioFirmware != null) {
            list.add(new UIObject(context.getString(R.string.device_radio_fw), radioFirmware));
        }
        list.add(new UIObject(context.getString(R.string.device_bootloader), MainUtils.getBootloader()));
        list.add(new UIObject(context.getString(R.string.device_lang), MainUtils.getDeviceLanguageSetting()));
        list.add(new UIObject(context.getString(R.string.device_locale), MainUtils.getDeviceLanguageLocale()));
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
