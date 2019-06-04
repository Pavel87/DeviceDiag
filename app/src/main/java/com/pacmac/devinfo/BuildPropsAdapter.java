package com.pacmac.devinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BuildPropsAdapter extends RecyclerView.Adapter<BuildPropsAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<BuildProperty> mData;
    private List<BuildProperty> mDataFiltered;
    private FilterResultCallback listener;

    public BuildPropsAdapter(Context context, List<BuildProperty> mData, FilterResultCallback listener) {
        this.context = context;
        this.mData = mData;
        this.mDataFiltered = mData;
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView keyView;
        TextView valueView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            keyView = itemView.findViewById(R.id.keyView);
            valueView = itemView.findViewById(R.id.valueView);
        }
    }

    @NonNull
    @Override
    public BuildPropsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.build_property_row, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildPropsAdapter.MyViewHolder holder, int position) {
        holder.keyView.setText(mDataFiltered.get(position).getKey());
        holder.valueView.setText(mDataFiltered.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDataFiltered = mData;
                } else {
                    List<BuildProperty> filteredList = new ArrayList<>();
                    for (BuildProperty property : mDataFiltered) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (property.getKey().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(property);
                        }
                    }
                    mDataFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (List<BuildProperty>) filterResults.values;
                if(listener != null)  {
                    listener.onFilterResult(mDataFiltered.size());
                }
                notifyDataSetChanged();
            }
        };
    }


    interface FilterResultCallback {
        void onFilterResult(int size);
    }
}
