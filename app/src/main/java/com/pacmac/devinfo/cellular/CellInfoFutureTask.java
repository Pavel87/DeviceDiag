package com.pacmac.devinfo.cellular;

import android.annotation.SuppressLint;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CellInfoFutureTask extends FutureTask {

    private final static int TIMEOUT = 500;  // seconds

    private List<CellInfo> cellInfo = null;

    CellInfoFutureTask() {
        super(() -> null);
    }

    void run(List<CellInfo> cellInfo) {
        this.cellInfo = cellInfo;
        super.run();
    }

    List<CellInfo> getAllCellInfoBlocking() throws ExecutionException, InterruptedException, TimeoutException {
        super.get(TIMEOUT, TimeUnit.MILLISECONDS);
        return cellInfo;
    }

    @SuppressLint("MissingPermission")
    public static List<CellInfo> getAllCellInfoBlocking(TelephonyManager telephonyManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                final CellInfoFutureTask future = new CellInfoFutureTask();

                telephonyManager.requestCellInfoUpdate(new Executor() {
                    @Override
                    public void execute(Runnable cellInfoCallbackCallable) {
                        cellInfoCallbackCallable.run();
                    }
                }, new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(List<CellInfo> cellInfo) {
                        future.run(cellInfo);
                    }
                });
                return future.getAllCellInfoBlocking();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}