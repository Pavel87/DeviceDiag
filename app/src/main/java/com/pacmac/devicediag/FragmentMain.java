package com.pacmac.devicediag;

import android.graphics.Color;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentMain extends Fragment {

    TextView modelName;
    TextView serialNumber;
    TextView manufacturer;
    TextView hardWare;
    TextView buildNumber;
    TextView cpuUsage;
    TextView memory;

    private GraphView graph;
    private LineGraphSeries<DataPoint> seriesLive;
    private double graphLastXValue = 40d;
    private double test =0d;
    private final Handler mHandler = new Handler();
    private Runnable timer;
    private float usage = 0;
    public FragmentMain() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diag_main, container, false);

        modelName = (TextView) rootView.findViewById(R.id.modelName);
        serialNumber = (TextView) rootView.findViewById(R.id.serialNumber);
        manufacturer = (TextView) rootView.findViewById(R.id.manufacturer);
        hardWare = (TextView) rootView.findViewById(R.id.hardware);
        buildNumber = (TextView) rootView.findViewById(R.id.buildNumber);
        cpuUsage = (TextView) rootView.findViewById(R.id.cpuUsage);
        memory = (TextView) rootView.findViewById(R.id.memory);

        graph = (GraphView) rootView.findViewById(R.id.graph);


        seriesLive = new LineGraphSeries<DataPoint>();
        seriesLive.setColor(Color.RED);
        seriesLive.setThickness(3);
        graph.addSeries(seriesLive);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setBackgroundColor(Color.DKGRAY);


        graph.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumVerticalLabels(5);
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().reloadStyles();

        return rootView;

    }






    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        modelName.setText(Build.MODEL);
        serialNumber.setText(Build.SERIAL);
        manufacturer.setText(Build.MANUFACTURER);
        hardWare.setText(Build.HARDWARE.toUpperCase() + " " + Build.BOARD);
        buildNumber.setText(Build.DISPLAY);
        cpuUsage.setText("" + readUsage());
        memory.setText("free:" + Runtime.getRuntime().freeMemory() + " total:" + Runtime.getRuntime().totalMemory() +
                " max:" + Runtime.getRuntime().maxMemory());


    }

    public void updateGraph() {
        usage  = readUsage()*100;
        graphLastXValue+=0.5d;
        seriesLive.appendData(new DataPoint(graphLastXValue, usage), true, 120);

    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
               updateGraph();
                mHandler.postDelayed(this, 500);

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            cpuUsage.setText("" + String.format("%.1f", usage));
                        }
                    });

            }
        };
        mHandler.postDelayed(timer, 500);

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timer);
    }



    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }



    // get RAM memory



}
