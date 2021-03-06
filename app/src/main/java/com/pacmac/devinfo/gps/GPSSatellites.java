package com.pacmac.devinfo.gps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPSSatellites extends Fragment {

    private TextView activeSatellitesTV;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private SateliteAdapter mItemAdapter;

    private GPSViewModel viewModel;
    public static GPSSatellites newInstance() {
        GPSSatellites fragment = new GPSSatellites();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(GPSViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gps_satellites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        Utility.showBannerAdView(view, getContext(), R.string.banner_id_12);

        activeSatellitesTV  = view.findViewById(R.id.activeSatellites);
        mRecyclerView = view.findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(false);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new SateliteAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<Satellite>> basicObserver = sats -> {
            if (sats == null) {
                return;
            }
            activeSatellitesTV.setText(String.format(Locale.ENGLISH, "%d", sats.size()));
            mItemAdapter.updateSatellites(sats);
        };
        viewModel.getSatellites().observe(getViewLifecycleOwner(), basicObserver);
    }

}
