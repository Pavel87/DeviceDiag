package com.pacmac.devinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NewFeaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_features);

        ImageView exitBtn = findViewById(R.id.exitButton);
        exitBtn.setOnClickListener(v -> onBackPressed());

        // Open WALLET app in market store
        findViewById(R.id.walletGoogle).setOnClickListener(view -> {
            String appPackage = "com.pacmac.mybudget";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                startActivity(intent);
            }
        });
    }
}
