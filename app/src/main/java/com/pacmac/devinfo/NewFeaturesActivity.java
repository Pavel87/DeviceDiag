package com.pacmac.devinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.pacmac.devinfo.utils.Utility;

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


        findViewById(R.id.rateApp).setOnClickListener((view) -> {
//            Utility.showRateDialog(NewFeaturesActivity.this);

//            ReviewManager manager = ReviewManagerFactory.create(NewFeaturesActivity.this);
//            Task<ReviewInfo> request = manager.requestReviewFlow();
//            request.addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // We can get the ReviewInfo object
//                    ReviewInfo reviewInfo = task.getResult();
//                    Task<Void> flow = manager.launchReviewFlow(NewFeaturesActivity.this, reviewInfo);
//                    flow.addOnCompleteListener(flowTask -> {
//                        Log.e("PACMAC", "Review flow has finished: " + flowTask.isSuccessful());
//                        // The flow has finished. The API does not indicate whether the user
//                        // reviewed or not, or even whether the review dialog was shown. Thus, no
//                        // matter the result, we continue our app flow.
//                    });
//                } else {
//                    // There was some problem, continue regardless of the result.
//                    Utility.showRateDialog(NewFeaturesActivity.this);
//                }
//            });
        });
    }
}
