package com.pacmac.devinfo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by pacmac on 6/27/2015.
 */
public class SateliteAdapter extends ArrayAdapter<Satelites> {

    private Context context;

    public SateliteAdapter(Context context,ArrayList<Satelites> satelites) {
        super(context, 0, satelites);

        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Satelites satelite = getItem(position);

        Viewholder viewHolder;
        if (convertView == null) {
            viewHolder = new Viewholder();
            convertView = LayoutInflater.from(context).inflate(R.layout.satelite_list_item, null);
            viewHolder.idT = (TextView) convertView.findViewById(R.id.satId);
            viewHolder.snrT = (TextView) convertView.findViewById(R.id.satSNR);
            viewHolder.pnrT = (TextView) convertView.findViewById(R.id.satPNR);
            viewHolder.azimuthT =(TextView) convertView.findViewById(R.id.satAzimuth);
            viewHolder.elevationT = (TextView) convertView.findViewById(R.id.satElevation);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (Viewholder) convertView.getTag();
        }
        viewHolder.idT.setText(""+satelite.getID());
        viewHolder.pnrT.setText(""+satelite.getPnr());
        viewHolder.snrT.setText(String.format("%.1f",satelite.getSnr()));
        viewHolder.azimuthT.setText(""+satelite.getAzimuth());
        viewHolder.elevationT.setText(""+satelite.getElevation());

        return convertView;
    }

    @Override
    public Satelites getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public void addAll(Satelites... items) {
        super.addAll(items);
    }

    @Override
    public void add(Satelites object) {
        super.add(object);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void remove(Satelites object) {
        super.remove(object);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public static class Viewholder {
        TextView idT;
        TextView snrT;
        TextView pnrT;
        TextView azimuthT;
        TextView elevationT;
    }
}


