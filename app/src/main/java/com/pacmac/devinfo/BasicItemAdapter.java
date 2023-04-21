package com.pacmac.devinfo;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.ListType;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.ThreeState;
import com.pacmac.devinfo.UIObject;

import java.util.List;

public class BasicItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<UIObject> mData;

    private final static int CONTENT = 0;
    private final static int TITLE = 1;
    private final static int IMAGE = 2;


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

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        AppCompatImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            imageView = itemView.findViewById(R.id.stateImage);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getType() == ListType.MAIN) {
            return CONTENT;
        } else if (mData.get(position).getType() == ListType.TITLE) {
            return TITLE;
        } else {
            return IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == CONTENT) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.ui_object_item, viewGroup, false);
            return new ContentViewHolder(itemView);
        } else if (i == TITLE) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.ui_title_layout, viewGroup, false);
            return new TitleViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.ui_object_image, viewGroup, false);
            return new ImageViewHolder(itemView);
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
        } else if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).label.setText(mData.get(position).getLabel());
            ((TitleViewHolder) holder).value.setText(mData.get(position).getValue());
        } else {
            ((ImageViewHolder) holder).label.setText(mData.get(position).getLabel());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (mData.get(position).getState() == ThreeState.YES) {
                    ((ImageViewHolder) holder).imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tick, null));
                } else if (mData.get(position).getState() == ThreeState.NO) {
                    ((ImageViewHolder) holder).imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cancel, null));
                } else {
                    ((ImageViewHolder) holder).imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.maybe, null));
                }
            }
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