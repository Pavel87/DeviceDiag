package com.pacmac.devinfo.gps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class GPSSatellites extends Fragment {

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
        mRecyclerView = view.findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new SateliteAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<Satellites>> basicObserver = sats -> {
            if (sats == null) {
                return;
            }
            mItemAdapter.updateSatellites(sats);
        };
        viewModel.getSatellites().observe(getViewLifecycleOwner(), basicObserver);
    }

}
