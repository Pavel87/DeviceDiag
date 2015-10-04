package com.pacmac.devicediag;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentDetails extends Fragment {

    private GridView gridView;
    private ArrayList<String> fields = new ArrayList<>(
            Arrays.asList("CPU", "MEMORY", "BATTERY", "CAMERA", "GPS", "SIM","SENSORS","DISPLAY","NETWORK","ABOUT" ));
                        //  0      1          2         3        4      5       6           7       8         9

public FragmentDetails() {
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_details, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridViewMain);
        gridView.setAdapter(new DetailAdapter(getActivity().getApplicationContext(),fields));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("TAG", "position: " + position);

                switch (position) {
                    case 0:
                        Intent i = new Intent(getActivity(), CPUInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 1:
                        i = new Intent(getActivity(), MemoryInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 2:
                        i = new Intent(getActivity(), BatteryInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 3:
                        Toast.makeText(getActivity().getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        i = new Intent(getActivity(), GPSInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 5:
                        i = new Intent(getActivity(), SIMInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 6:
                        i = new Intent(getActivity(), SensorsInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;

                    case 7:
                        i = new Intent(getActivity(), DisplayInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 8:
                        i = new Intent(getActivity(), NetworkInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                    case 9:
                        Toast.makeText(getActivity().getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_SHORT).show();
                           break;
                }
            }
        });


        return rootView;
    }
}
