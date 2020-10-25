package com.pacmac.devinfo.gps;


import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by pacmac on 6/27/2015.
 */
public class SateliteAdapter extends RecyclerView.Adapter<SateliteAdapter.MyViewHolder> {

    private List<Satellite> mDataset;

    private Object sync = new Object();


    public SateliteAdapter(List<Satellite> mDataset) {
        this.mDataset = mDataset;
    }

    public void updateSatellites(List<Satellite> mDataset) {
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
        TextView constellationTypeT;


        public MyViewHolder(View v) {
            super(v);
            idT = v.findViewById(R.id.satId);
            snrT = v.findViewById(R.id.satSNR);
            pnrT = v.findViewById(R.id.satPNR);
            azimuthT = v.findViewById(R.id.satAzimuth);
            elevationT = v.findViewById(R.id.satElevation);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                constellationTypeT = v.findViewById(R.id.constellationType);
            }
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

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewHolder.constellationTypeT.setText(String.format(Locale.ENGLISH, "%s", mDataset.get(position).getConstellationType()));
                }
                viewHolder.idT.setText(String.format(Locale.ENGLISH, "%d", position + 1));
                viewHolder.pnrT.setText(String.format(Locale.ENGLISH, "%d", mDataset.get(position).getPnr()));
                viewHolder.snrT.setText(String.format(Locale.ENGLISH, "%.1f", mDataset.get(position).getSnr()));
                viewHolder.azimuthT.setText(String.format(Locale.ENGLISH, "%.0f", mDataset.get(position).getAzimuth()));
                viewHolder.elevationT.setText(String.format(Locale.ENGLISH, "%.0f", mDataset.get(position).getElevation()));
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


