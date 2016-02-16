package com.pacmac.devicediag;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button sendFeedback = (Button) findViewById(R.id.sendFeedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitAlert();
            }
        });

    }


    private void showExitAlert() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.send_feedback_dialog);
        dialog.setCancelable(false);

        Button yesButton = (Button) dialog.findViewById(R.id.yesExit);
        final EditText feedbackMsg = (EditText) dialog.findViewById(R.id.feedbackMsg);
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
                }
                else{
                    Toast.makeText(getApplicationContext(), "Feedback is empty.", Toast.LENGTH_SHORT).show();
                }
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

}
