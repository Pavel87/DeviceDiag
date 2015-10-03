package com.pacmac.devicediag;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pacmac on 5/27/2015.
 */
public class DetailAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mFields;
    private static final int PADDING = 5;
    private static final int WIDTH = 110;
    private static final int HEIGHT = 110;

    public DetailAdapter(Context context, List<String> fields) {
        this.mContext = context;
        this.mFields = fields;
    }

    @Override
    public int getCount() {
        return mFields.size();
    }

    @Override
    public Object getItem(int i) {
        return mFields.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {

        TextView textView = (TextView) view;

        if(textView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams( new GridView.LayoutParams(WIDTH,HEIGHT));
            textView.setPadding(PADDING, PADDING, PADDING, PADDING);
            textView.setFadingEdgeLength(10);

        }

        textView.setText(mFields.get(pos));

        return textView;
    }
}
