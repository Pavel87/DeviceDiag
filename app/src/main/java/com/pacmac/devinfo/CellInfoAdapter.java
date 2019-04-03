package com.pacmac.devinfo;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pacmac on 2019-04-02.
 */

public class CellInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CellInfo> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class LTEViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mcc;
        public TextView mnc;
        public TextView ci;
        public TextView tac;
        public TextView pci;
        public TextView rfcn;
        public TextView bandwidth;

        public LTEViewHolder(View v) {
            super(v);
            mcc = v.findViewById(R.id.lte_mcc);
            mnc = v.findViewById(R.id.lte_mnc);
            ci = v.findViewById(R.id.lte_ci);
            tac = v.findViewById(R.id.lte_tac);
            pci = v.findViewById(R.id.lte_pci);
            rfcn = v.findViewById(R.id.lte_earfcn);
            bandwidth = v.findViewById(R.id.lte_bandwidth);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CellInfoAdapter(List<CellInfo> myDataset) {
        this.mDataset = myDataset;
    }

    public void updateData(List<CellInfo> myDataset) {
        mDataset = myDataset;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LTEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View lteRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lte_cell_row, parent, false);
        LTEViewHolder vh = new LTEViewHolder(lteRowView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ((LTEViewHolder)holder).mcc.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getMcc()));
        ((LTEViewHolder)holder).mnc.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getMnc()));
        ((LTEViewHolder)holder).ci.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getCi()));
        ((LTEViewHolder)holder).tac.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getTac()));
        ((LTEViewHolder)holder).pci.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getPci()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((LTEViewHolder)holder).tac.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getEarfcn()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ((LTEViewHolder)holder).tac.setText(String.valueOf(((CellInfoLte) mDataset.get(position)).getCellIdentity().getBandwidth()));
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