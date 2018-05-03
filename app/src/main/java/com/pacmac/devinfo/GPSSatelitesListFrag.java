package com.pacmac.devinfo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    private ListView list;
    private SateliteAdapter mAdapter;
    private int satSizePrev = 0;

    private ArrayList<Satelites> satelites = new ArrayList<Satelites>();

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
        //list + adapter inicialization
        list = (ListView) view.findViewById(R.id.sateliteList);
        mAdapter = new SateliteAdapter(getActivity().getApplicationContext(), satelites);
        View header = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.satelites_header, null);
        list.addHeaderView(header);
        list.setAdapter(mAdapter);


        return view;
    }


    public void invalidateListView(ArrayList<Satelites> sats) {
        if (sats.size() != satSizePrev && sats.size() > 0) {
            satSizePrev = sats.size();
            this.satelites = sats;
            mAdapter = new SateliteAdapter(getActivity().getApplicationContext(), satelites);
            list.setAdapter(mAdapter);
        }

    }
}
