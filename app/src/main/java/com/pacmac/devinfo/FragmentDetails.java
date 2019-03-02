package com.pacmac.devinfo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tutelatechnologies.sdk.framework.TutelaSDKFactory;

/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentDetails extends Fragment {

    private GridView gridView;

    private Integer[] mThumbIds = {
            R.drawable.cpuimg, R.drawable.ramimg,
            R.drawable.batimg, R.drawable.camimg,
            R.drawable.gpsimg, R.drawable.simimg,
            R.drawable.sensorimg, R.drawable.displayimg,
            R.drawable.wifiimg, R.drawable.aboutimg
    };

    public FragmentDetails() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_details, container, false);

        try {
            TutelaSDKFactory.getTheSDK().initializeWithApiKey(DeviceDiagApp.REG_KEY,
                    getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        gridView = rootView.findViewById(R.id.gridViewMain);
        gridView.setAdapter(new DetailAdapter(getActivity().getApplicationContext(), mThumbIds));

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
                        i = new Intent(getActivity(), CameraInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
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
                        i = new Intent(getActivity(), AboutActivity.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                        break;
                }
            }
        });


        return rootView;
    }
}
