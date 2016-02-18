package com.pacmac.devinfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by pacmac on 5/27/2015.
 */
public class DetailAdapter extends BaseAdapter {

    private Context mContext;
    private Integer[] mFields;

    public DetailAdapter(Context context, Integer[] fields) {
        this.mContext = context;
        this.mFields = fields;
    }

    @Override
    public int getCount() {
        return mFields.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {

        ImageView imageView;
        if (convertView == null){

            imageView = new ImageView(mContext);
           // imageView.setLayoutParams(new GridView.LayoutParams(160,160));
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(8,8,8,8);
        }
        else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mFields[pos]);

        return imageView;
    }
}
