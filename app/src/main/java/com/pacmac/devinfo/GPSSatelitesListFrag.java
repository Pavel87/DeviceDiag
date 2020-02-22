package com.pacmac.devinfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        gpsViewModel = new ViewModelProvider(getActivity()).get(GPSModel.class);


        List<Satelites> satelitesList = gpsViewModel.getSatellites().getValue();

        if (satelitesList != null) {
            satSizePrev = satelitesList.size();
        }
        if (satSizePrev == 0) {
            satelitesList = new ArrayList<>();
            satelitesList.add(new Satelites(0, 0, 0, 0, 0));
        }

        mAdapter = new SateliteAdapter(new ArrayList<Satelites>());
        mRecyclerView.setAdapter(mAdapter);

        gpsViewModel.getSatellites().observe(this, new Observer<List<Satelites>>() {
            @Override
            public void onChanged(@Nullable List<Satelites> satellites) {
                if (satellites == null) {
                    satellites = new ArrayList<>();
                }
                mAdapter.updateSatellites(satellites);
                satSizePrev = satellites.size();
            }
        });





    }


}
