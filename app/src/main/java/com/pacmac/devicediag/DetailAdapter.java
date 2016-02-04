package com.pacmac.devicediag;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pacmac on 5/27/2015.
 */
public class DetailAdapter extends BaseAdapter {

    private Context mContext;
    private Integer[] mFields;
    private static final int PADDING = 5;
    private static final int WIDTH = 110;
    private static final int HEIGHT = 110;

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
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8,8,8,8);
        }
        else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mFields[pos]);


       /* TextView textView = (TextView) view;

        if(textView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams( new GridView.LayoutParams(WIDTH,HEIGHT));
            textView.setPadding(PADDING, PADDING, PADDING, PADDING);
            textView.setFadingEdgeLength(10);

        }

        textView.setText(mFields.get(pos));*/

        return imageView;
    }
}
