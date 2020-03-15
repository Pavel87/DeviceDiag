package com.pacmac.devinfo.cellular;

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

public class BasicItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<UIObject> mData;

    private final static int TITLE = 1;
    private final static int CONTENT = 0;


    public BasicItemAdapter(Context context, List<UIObject> mData) {
        this.context = context;
        this.mData = mData;
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


    private class TitleViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        TextView value;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            value = itemView.findViewById(R.id.value);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getType() == 0) {
            return CONTENT;
        } else {
            return TITLE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == CONTENT) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.ui_object_item, viewGroup, false);
            return new ContentViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.ui_title_layout, viewGroup, false);
            return new TitleViewHolder(itemView);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ContentViewHolder) {
            ((ContentViewHolder) holder).label.setText(mData.get(position).getLabel());
            ((ContentViewHolder) holder).value.setText(mData.get(position).getValue());

            if (mData.get(position).getSuffix() == null) {
                ((ContentViewHolder) holder).suffix.setVisibility(View.INVISIBLE);
                ((ContentViewHolder) holder).suffix.setText("");
            } else {
                ((ContentViewHolder) holder).suffix.setVisibility(View.VISIBLE);
                ((ContentViewHolder) holder).suffix.setText(mData.get(position).getSuffix());
            }
        } else {
            ((TitleViewHolder) holder).label.setText(mData.get(position).getLabel());
            ((TitleViewHolder) holder).value.setText(mData.get(position).getValue());
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<UIObject> newList) {
        this.mData = newList;
        notifyDataSetChanged();
    }
}