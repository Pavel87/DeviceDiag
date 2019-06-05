package com.pacmac.devinfo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by pacmac on 2/5/2016.
 */
public class GPSSatelitesListFrag extends Fragment {

    //fields
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private SateliteAdapter mAdapter;
    private int satSizePrev = 0;

    private ArrayList<Satelites> satelites = new ArrayList<>();

    public GPSSatelitesListFrag() {
        //default constructor
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("sats_par_store", satelites);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gps_satelites_frag, container, false);

        if (savedInstanceState != null) {
            satelites = savedInstanceState.getParcelableArrayList("sats_par_store");
        } else {
            satelites.add(new Satelites(0, 0, 0, 0, 0));
        }
        mRecyclerView = view.findViewById(R.id.sateliteList);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SateliteAdapter(satelites);
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }

    public void updateSatellites(ArrayList<Satelites> sats) {
        if (sats.size() != satSizePrev && sats.size() > 0) {
            satSizePrev = sats.size();
            this.satelites = sats;
            mAdapter.updateSatellites(satelites);
        }
    }
}
