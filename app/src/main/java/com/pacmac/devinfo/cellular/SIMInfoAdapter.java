package com.pacmac.devinfo.cellular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.List;
import java.util.Locale;

public class SIMInfoAdapter extends RecyclerView.Adapter<SIMInfoAdapter.MyViewHolder> {

    private Context context;
    private List<List<UIObject>> mData;


    public SIMInfoAdapter(Context context, List<List<UIObject>> mData) {
        this.context = context;
        this.mData = mData;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView slotID;
        RecyclerView mRecyclerView;
        LinearLayoutManager basicLinearLayoutManager;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            slotID = itemView.findViewById(R.id.simID);
            mRecyclerView = itemView.findViewById(R.id.simInfo);
            mRecyclerView.setHasFixedSize(false);
            basicLinearLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(basicLinearLayoutManager);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.sim_slot_info, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.slotID.setText(String.format(Locale.ENGLISH, context.getResources().getString(R.string.simID), position + 1));
        BasicItemAdapter basicItemAdapter = new BasicItemAdapter(context, mData.get(position));
        holder.mRecyclerView.setAdapter(basicItemAdapter);
        holder.mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<List<UIObject>> newList){
        this.mData = newList;
        notifyDataSetChanged();
    }
}
