package com.pacmac.devinfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pacmac.devinfo.utils.Utility;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button rating = findViewById(R.id.rateApp);

        rating.setOnClickListener(view -> {
                    Utility.showRateDialog(AboutActivity.this);
//            ReviewManager manager = ReviewManagerFactory.create(AboutActivity.this);
//            Task<ReviewInfo> request = manager.requestReviewFlow();
//            request.addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // We can get the ReviewInfo object
//                    ReviewInfo reviewInfo = task.getResult();
//                    Task<Void> flow = manager.launchReviewFlow(AboutActivity.this, reviewInfo);
//                    flow.addOnCompleteListener(flowTask -> {
//                        Log.e("PACMAC", "Review flow has finished: " + flowTask.isSuccessful());
//                        // The flow has finished. The API does not indicate whether the user
//                        // reviewed or not, or even whether the review dialog was shown. Thus, no
//                        // matter the result, we continue our app flow.
//                    });
//                } else {
//                    // There was some problem, continue regardless of the result.
//                }
//            });
        });

        TextView versionText = findViewById(R.id.versionText);
        String s = "Unknown";
        try {
            s = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionText.setText(String.format(Locale.ENGLISH, "%s %s", getResources().getString(R.string.version_text), s));


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
