package com.pacmac.devinfo;

import android.content.Context;

import androidx.lifecycle.ViewModel;


/**
 * Created by pacmac on 2020-02-22.
 */

public class MainViewModel extends ViewModel {

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
