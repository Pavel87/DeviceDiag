package com.pacmac.devinfo.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.List;

public class BasicItemAdapter extends RecyclerView.Adapter<BasicItemAdapter.MyViewHolder> {

    private Context context;
    private List<UIObject> mData;


    public BasicItemAdapter(Context context, List<UIObject> mData) {
        this.context = context;
        this.mData = mData;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        TextView value;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            value = itemView.findViewById(R.id.value);
        }
    }

    @NonNull
    @Override
    public BasicItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.ui_object_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BasicItemAdapter.MyViewHolder holder, int position) {
        holder.label.setText(mData.get(position).getLabel());
        holder.value.setText(mData.get(position).getValue());
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<UIObject> newList){
        this.mData = newList;
        notifyDataSetChanged();
    }
}