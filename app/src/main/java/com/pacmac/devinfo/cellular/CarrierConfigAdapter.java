package com.pacmac.devinfo.cellular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;

public class CarrierConfigAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context context;
    private List<UIObject> mData;
    private List<UIObject> mDataFiltered;
    private FilterResultCallback listener;


    public CarrierConfigAdapter(Context context, List<UIObject> mData, FilterResultCallback listener) {
        this.context = context;
        this.mData = mData;
        this.mDataFiltered = mData;
        this.listener = listener;
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        TextView value;
        TextView suffix;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            value = itemView.findViewById(R.id.value);
            suffix = itemView.findViewById(R.id.suffix);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.ui_object_item, viewGroup, false);
        return new ContentViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ContentViewHolder) holder).label.setText(mDataFiltered.get(position).getLabel());
        ((ContentViewHolder) holder).value.setText(mDataFiltered.get(position).getValue());

        if (mDataFiltered.get(position).getSuffix() == null) {
            ((ContentViewHolder) holder).suffix.setVisibility(View.INVISIBLE);
            ((ContentViewHolder) holder).suffix.setText("");
        } else {
            ((ContentViewHolder) holder).suffix.setVisibility(View.VISIBLE);
            ((ContentViewHolder) holder).suffix.setText(mDataFiltered.get(position).getSuffix());
        }
    }


    public int getRawDataCount() {
        return mData.size();
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    public void updateData(List<UIObject> newList) {
        this.mData = newList;
        this.mDataFiltered = newList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
//                synchronized (syncListObject) {
                String charString = charSequence.toString();
                List<UIObject> filteredList = new ArrayList<>();

                if (charString.isEmpty()) {
                    filteredList = mData;
                } else {
                    for (UIObject uiObject : mData) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (uiObject.getLabel().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(uiObject);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
//                }
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (List<UIObject>) filterResults.values;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onFilterResult(mDataFiltered.size());
                }
            }
        };
    }

    interface FilterResultCallback {
        void onFilterResult(int size);
    }
}