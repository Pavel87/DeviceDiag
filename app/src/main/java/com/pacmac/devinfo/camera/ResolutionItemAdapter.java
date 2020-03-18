package com.pacmac.devinfo.camera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;

import java.util.List;

public class ResolutionItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ResolutionObject> mData;

    public ResolutionItemAdapter(Context context, List<ResolutionObject> mData) {
        this.context = context;
        this.mData = mData;
    }

    private class ResolutionViewHolder extends RecyclerView.ViewHolder {

        TextView width;
        TextView height;

        ResolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            width = itemView.findViewById(R.id.width);
            height = itemView.findViewById(R.id.height);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ResolutionViewHolder(LayoutInflater.from(context).inflate(R.layout.resolution_grid_item, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ResolutionViewHolder) holder).width.setText(String.valueOf(mData.get(position).getWidth()));
        ((ResolutionViewHolder) holder).height.setText(String.valueOf(mData.get(position).getHeight()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<ResolutionObject> newList) {
        this.mData = newList;
        notifyDataSetChanged();
    }
}