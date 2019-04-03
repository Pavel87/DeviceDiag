package com.pacmac.devinfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    private final String WAS_RATED = "ratingDone";
    private final String PREF = "prefRating";

    private boolean isRating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        isRating = preferences.getBoolean(WAS_RATED, false);


        Button sendFeedback = findViewById(R.id.sendFeedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitAlert();
            }
        });


        Button rating = findViewById(R.id.rateApp);
        if (!isRating) {
            rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRateDialog();
                }
            });
        } else
            rating.setVisibility(View.GONE);  // if rating was submited don't show this button anymore


        TextView versionText = findViewById(R.id.versionText);
        String s = "Unknown";
        try {
            s = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionText.setText(getResources().getString(R.string.version_text) + s);


    }


    private void showExitAlert() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.send_feedback_dialog);
        dialog.setCancelable(false);

        Button yesButton = dialog.findViewById(R.id.yesExit);
        final EditText feedbackMsg = dialog.findViewById(R.id.feedbackMsg);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackMsg.getText().toString().length() > 0) {
                    Intent Email = new Intent(Intent.ACTION_SEND);
                    Email.setType("text/email");
                    Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"pacmac.dev@gmail.com"});
                    Email.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_subject));
                    Email.putExtra(Intent.EXTRA_TEXT, feedbackMsg.getText().toString());
                    startActivity(Intent.createChooser(Email, "Send Feedback:"));
                } else {
                    Toast.makeText(getApplicationContext(), "Feedback is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button noButton = dialog.findViewById(R.id.noExit);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showRateDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rateit_dialog);
        dialog.setCancelable(false);

        Button yesButton = dialog.findViewById(R.id.yesExit);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appPackage = getApplicationContext().getPackageName();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                    startActivity(intent);
                }
                setPreferences();  // will hide the RATE IT button
                dialog.dismiss();
            }
        });

        Button noButton = (Button) dialog.findViewById(R.id.noExit);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void setPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        isRating = preferences.getBoolean(WAS_RATED, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(WAS_RATED, true);
        editor.commit();
    }

}
