package com.pacmac.devinfo.gps;


import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pacmac.devinfo.R;

import java.util.List;

/**
 * Created by pacmac on 6/27/2015.
 */
public class SateliteAdapter extends RecyclerView.Adapter<SateliteAdapter.MyViewHolder> {

    private List<Satellites> mDataset;

    private Object sync = new Object();


    public SateliteAdapter(List<Satellites> mDataset) {
        this.mDataset = mDataset;
    }

    public void updateSatellites(List<Satellites> mDataset) {
        synchronized (sync) {
            this.mDataset = mDataset;
            notifyDataSetChanged();
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView idT;
        TextView snrT;
        TextView pnrT;
        TextView azimuthT;
        TextView elevationT;


        public MyViewHolder(View v) {
            super(v);
            idT = v.findViewById(R.id.satId);
            snrT = v.findViewById(R.id.satSNR);
            pnrT = v.findViewById(R.id.satPNR);
            azimuthT = v.findViewById(R.id.satAzimuth);
            elevationT = v.findViewById(R.id.satElevation);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SateliteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.satellite_list_item, parent, false);
        SateliteAdapter.MyViewHolder vh = new SateliteAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SateliteAdapter.MyViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        synchronized (sync) {
            if (position < mDataset.size()) {
                viewHolder.idT.setText("" + mDataset.get(position).getID());
                viewHolder.pnrT.setText("" + mDataset.get(position).getPnr());
                viewHolder.snrT.setText(String.format("%.1f", mDataset.get(position).getSnr()));
                viewHolder.azimuthT.setText("" + mDataset.get(position).getAzimuth());
                viewHolder.elevationT.setText("" + mDataset.get(position).getElevation());
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }
}


