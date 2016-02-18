package com.pacmac.devinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by pacmac on 2/9/2016.
 */
public class CenterProgress extends AlertDialog {


    protected CenterProgress(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);
    }
}
