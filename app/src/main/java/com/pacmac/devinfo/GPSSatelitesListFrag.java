package com.pacmac.devinfo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pacmac on 2/5/2016.
 */
public class GPSSatelitesListFrag extends Fragment {

    //fields
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    GPSModel gpsViewModel;
    private SateliteAdapter mAdapter;
    private int satSizePrev = 0;

    public GPSSatelitesListFrag() {
        //default constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gps_satelites_frag, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.sateliteList);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        gpsViewModel = ViewModelProviders.of(getActivity()).get(GPSModel.class);


        List<Satelites> satelitesList = gpsViewModel.getSatellites().getValue();


        gpsViewModel.getSatellites().observe(this, new Observer<List<Satelites>>() {
            @Override
            public void onChanged(@Nullable List<Satelites> satellites) {
                Log.d("PACMAC", "OnCHANGED: " + satellites.size());
//                if (satellites.size() != satSizePrev && satellites.size() > 0) {
                    mAdapter.updateSatellites(satellites);
                    satSizePrev = satellites.size();
//                }
            }
        });

        if (satelitesList != null) {
            satSizePrev = satelitesList.size();
        }
        if (satSizePrev == 0) {
            satelitesList = new ArrayList<>();
            satelitesList.add(new Satelites(0, 0, 0, 0, 0));
        }

        mAdapter = new SateliteAdapter(satelitesList);
        mRecyclerView.setAdapter(mAdapter);

    }


}
