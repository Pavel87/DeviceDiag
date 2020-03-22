package com.pacmac.devinfo.display;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "display_info";

    private MutableLiveData<List<UIObject>> displayInfo = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getDisplayInfo(Context context, Display display, DisplayMetrics metrics) {
        loadDisplayInfo(context, display, metrics);
        return displayInfo;
    }

    public List<UIObject> getDisplayInfoForExport() {
        return displayInfo.getValue();
    }

    private void loadDisplayInfo(Context context, Display display, DisplayMetrics metrics) {

        List<UIObject> list = new ArrayList<>();

        list.add(DisplayUtils.getDensity(context, metrics));
        list.add(DisplayUtils.getScaleFactor(context, metrics));
        list.add(DisplayUtils.getRefreshRate(context, display));
        list.addAll(DisplayUtils.getResolution(context, display, metrics));
        list.add(DisplayUtils.getXYDpi(context, metrics));
        list.add(DisplayUtils.getOrientation(context, display));
        list.add(DisplayUtils.getLayoutSize(context));
        list.add(DisplayUtils.getType(context, display));
        list.add(DisplayUtils.getDrawType(context, metrics));

        displayInfo.postValue(list);
    }

}