package com.pacmac.devinfo.export;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.pacmac.devinfo.R;

import java.io.File;

import static com.pacmac.devinfo.export.ExportUtils.EXPORT_SLOT_AVAILABLE;

public class ExportActivity extends AppCompatActivity {

    private final static int PROMO_REQUEST_CODE = 8;

    private String filePathString;
    private RewardedAd rewardedAd;
    private SharedPreferences sharedPreferences;

    private TextView exportSlotCounter;
    private Button watchVideoBtn;
    private Button exportBtn;
    private AppCompatImageView slot1;
    private AppCompatImageView slot2;
    private AppCompatImageView slot3;
    private AppCompatImageView slot4;
    private AppCompatImageView slot5;

    private ProgressBar progressBar;


    private int slotCount = 0;
    private int error = -1;

    private boolean isAdLoading = false;
    private boolean userClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        rewardedAd = createAndLoadRewardedAd();

        exportSlotCounter = findViewById(R.id.exportSlotCounter);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        slot4 = findViewById(R.id.slot4);
        slot5 = findViewById(R.id.slot5);
        exportBtn = findViewById(R.id.exportButton);
        watchVideoBtn = findViewById(R.id.watchVideoBtn);
        exportSlotCounter = findViewById(R.id.exportSlotCounter);

        progressBar = findViewById(R.id.progress);

        sharedPreferences = getSharedPreferences(ExportUtils.EXPORT_SHARED_PREF_FILE, MODE_PRIVATE);
        slotCount = sharedPreferences.getInt(EXPORT_SLOT_AVAILABLE, 0);

        updateSlotViews(slotCount);


        Intent intent = getIntent();
        if (intent != null) {
            filePathString = intent.getStringExtra(ExportUtils.EXPORT_FILE);
        }


        exportBtn.setOnClickListener(v -> {
            slotCount -= 1;
            if (slotCount < 0) {
                slotCount = 0;
            }
            sharedPreferences.edit().putInt(EXPORT_SLOT_AVAILABLE, slotCount).apply();
            updateSlotViews(slotCount);

            File exportFile = new File(filePathString);
            ExportUtils.sendShareIntent(ExportActivity.this, exportFile);
        });


        watchVideoBtn.setOnClickListener(v -> {
            userClick = true;
            progressBar.setVisibility(View.VISIBLE);
            if (!isAdLoading) {
                if (rewardedAd.isLoaded() && error != AdRequest.ERROR_CODE_NETWORK_ERROR) {
                    watchVideoBtn.setEnabled(false);
                    rewardedAd.show(ExportActivity.this, adShowCallback);
                } else if (error == AdRequest.ERROR_CODE_NETWORK_ERROR) {
                    rewardedAd = createAndLoadRewardedAd();
                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                    rewardedAd = createAndLoadRewardedAd();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public RewardedAd createAndLoadRewardedAd() {
        isAdLoading = true;
        RewardedAd rewardedAd = new RewardedAd(this, getResources().getString(R.string.rewarded1));
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    private RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
        @Override
        public void onRewardedAdLoaded() {
            isAdLoading = false;
            error = -1;
            watchVideoBtn.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
            if (userClick) {
                rewardedAd.show(ExportActivity.this, adShowCallback);
            }
            userClick = false;
        }

        @Override
        public void onRewardedAdFailedToLoad(int errorCode) {
            error = errorCode;
            isAdLoading = false;
            userClick = false;
            watchVideoBtn.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        }
    };


    private RewardedAdCallback adShowCallback = new RewardedAdCallback() {
        @Override
        public void onRewardedAdOpened() {
            watchVideoBtn.setEnabled(false);
            rewardedAd = createAndLoadRewardedAd();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onRewardedAdClosed() {
        }

        @Override
        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            if (slotCount < 5) {
                slotCount += 1;
                sharedPreferences.edit().putInt(EXPORT_SLOT_AVAILABLE, slotCount).apply();
                updateSlotViews(slotCount);
                Toast.makeText(getApplicationContext(), "You have just earned 1 EXPORT Slot!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "All export slots are unlocked.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onRewardedAdFailedToShow(int errorCode) {
            // Ad failed to display.
            rewardedAd = createAndLoadRewardedAd();
            startActivityForResult(new Intent(getApplicationContext(), PromoActivity.class), PROMO_REQUEST_CODE);
            // TODO add my own ad withing the APP like showing WALLET APP Ad
        }
    };


    private void updateSlotViews(int slotCount) {
        exportSlotCounter.setText(String.valueOf(slotCount));

        slot1.setColorFilter(getResources().getColor(R.color.export_slot_disabled));
        slot2.setColorFilter(getResources().getColor(R.color.export_slot_disabled));
        slot3.setColorFilter(getResources().getColor(R.color.export_slot_disabled));
        slot4.setColorFilter(getResources().getColor(R.color.export_slot_disabled));
        slot5.setColorFilter(getResources().getColor(R.color.export_slot_disabled));

        if (slotCount > 0) {
            exportBtn.setEnabled(true);
            slot1.setColorFilter(getResources().getColor(R.color.export_slot_earned));

            if (slotCount > 1) {
                slot2.setColorFilter(getResources().getColor(R.color.export_slot_earned));
            }
            if (slotCount > 2) {
                slot3.setColorFilter(getResources().getColor(R.color.export_slot_earned));
            }
            if (slotCount > 3) {
                slot4.setColorFilter(getResources().getColor(R.color.export_slot_earned));
            }
            if (slotCount > 4) {
                slot5.setColorFilter(getResources().getColor(R.color.export_slot_earned));
            }
        } else {
            exportBtn.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROMO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (slotCount < 5) {
                slotCount += 1;
                sharedPreferences.edit().putInt(EXPORT_SLOT_AVAILABLE, slotCount).apply();
                updateSlotViews(slotCount);
                Toast.makeText(getApplicationContext(), "You have just earned 1 EXPORT Slot!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "All export slots are unlocked.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
