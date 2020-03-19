package com.pacmac.devinfo.config;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.Utility;

import java.util.List;

public class BuildPropertiesViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "build_properties";
    MutableLiveData<List<UIObject>> buildProperties = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getBuildProperties(Context context) {
        new Thread(() -> loadBuildProperties(context)).start();
        return buildProperties;
    }

    public List<UIObject> getBuildPropertiesForExport() {
        return buildProperties.getValue();
    }

    private void loadBuildProperties(Context context) {
        List<UIObject> buildPropertyList;
        buildPropertyList = Utility.getBuildPropsList(context);
        buildProperties.postValue(buildPropertyList);
    }
}
