package com.pacmac.devinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

/**
 * Created by pacmac on 2016-10-04.
 */


public class Utility {


    public final static int MY_PERMISSIONS_REQUEST = 8;

    /**
     * This method will check if permission is granted at runtime
     */
    public static boolean checkPermission(Context context, String permission) {

        int status = PermissionChecker.checkSelfPermission(context, permission);
        if (status == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public static void requestPermissions(Activity activity, String permission) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity, new String[]{permission}, MY_PERMISSIONS_REQUEST);
    }

    public static void displayExplanationForPermission(Activity act, String msg, final String permission){

        final Activity mActivity = act;
        AlertDialog.Builder builder = new AlertDialog.Builder(act, 0)
                .setCancelable(true).setMessage(msg).setTitle("Missing Permission")
                .setPositiveButton((act.getResources().getString(R.string.request_perm)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(mActivity, permission);
                    }
                })
                .setNegativeButton(act.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}


