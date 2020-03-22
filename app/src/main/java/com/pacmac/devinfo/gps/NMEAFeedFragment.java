package com.pacmac.devinfo.gps;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.utils.Utility;

import static android.content.Context.LOCATION_SERVICE;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NMEAFeedFragment extends Fragment implements OnNmeaMessageListener {

    private TextView nmeaUpdate;
    private Button startButton;
    private ScrollView mScrollView;

    private GPSViewModel viewModel;

    LocationManager locationManager;
    private boolean isNMEAListenerOn = false;


    public static NMEAFeedFragment newInstance() {
        NMEAFeedFragment fragment = new NMEAFeedFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(GPSViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gps_nmea, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Utility.showBannerAdView(view, getContext(), R.string.banner_id_13);

        nmeaUpdate = view.findViewById(R.id.nmeaUpdate);
        startButton = view.findViewById(R.id.nmeaRollButton);
        mScrollView = view.findViewById(R.id.mScrollView);

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        startButton.setOnClickListener(view1 -> {
            if (isNMEAListenerOn) {
                isNMEAListenerOn = false;
                locationManager.removeNmeaListener(NMEAFeedFragment.this);
                startButton.setText(R.string.nmea_start);
            } else {
                nmeaUpdate.setText(R.string.nmea_fetching_data);
                locationManager.addNmeaListener(NMEAFeedFragment.this);
                isNMEAListenerOn = true;
                startButton.setText(R.string.nmea_stop);
            }

        });

        Observer<String> basicObserver = message -> {
            if (message != null) {
                nmeaUpdate.setText(Html.fromHtml(message));
                mScrollView.post(() -> mScrollView.smoothScrollTo(0, nmeaUpdate.getBottom()));
            }
        };
        viewModel.getMessageLive().observe(getViewLifecycleOwner(), basicObserver);

    }


    @Override
    public void onPause() {
        if (locationManager != null && isNMEAListenerOn) {
            locationManager.removeNmeaListener(this);
        }
        super.onPause();
    }


    @Override
    public void onNmeaMessage(String message, long timestamp) {
        viewModel.onNmeaMessage(getContext(), message, timestamp);
    }


}
