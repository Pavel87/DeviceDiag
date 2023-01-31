package com.pacmac.devinfo.export;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.pacmac.devinfo.R;

public class PromoActivity extends AppCompatActivity {

    private boolean playstoreVisited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);


        ImageView exitBtn = findViewById(R.id.exitButton);
        exitBtn.setOnClickListener(v -> onBackPressed());

        // Open WALLET app in market store
        findViewById(R.id.walletGoogle).setOnClickListener(view -> {
            playstoreVisited = true;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (playstoreVisited) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (playstoreVisited) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
