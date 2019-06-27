package com.pacmac.devinfo;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.MyViewHolder> {

        private List<MemoryInfo.StorageSpace> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView storageType;
            public TextView storageTotal;
            public TextView storageAvailable;
            public TextView storageUsed;


            public MyViewHolder(View v) {
                super(v);
                storageType = v.findViewById(R.id.storageType);
                storageTotal = v.findViewById(R.id.storageTotal);
                storageAvailable = v.findViewById(R.id.storageAvailable);
                storageUsed = v.findViewById(R.id.storageUsed);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public StorageAdapter(List<MemoryInfo.StorageSpace> myDataset) {
            this.mDataset = myDataset;
        }

        public void updateData(List<MemoryInfo.StorageSpace> myDataset) {
            mDataset = myDataset;
            notifyDataSetChanged();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public StorageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.storage_row, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            if (mDataset.get(position).getTotal() > 0) {
                holder.storageType.setText(MemoryInfo.getTypeString(mDataset.get(position).getType()));
                holder.storageTotal.setText(Utility.bytesToHuman(mDataset.get(position).getTotal()));
                holder.storageAvailable.setText(Utility.bytesToHuman(mDataset.get(position).getFree()));
                holder.storageUsed.setText(Utility.bytesToHuman(mDataset.get(position).getTotal() - mDataset.get(position).getFree()));
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

        public MemoryInfo.StorageSpace getStorageSpaceId(int index) {
            return mDataset.get(index);
        }

    }
