package com.pacmac.devinfo.sensor;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.export.ExportActivity;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.export.ExportTask;
import com.pacmac.devinfo.export.ExportUtils;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class SensorListFragment extends Fragment implements ExportTask.OnExportTaskFinished {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isExporting = false;

    private SensorAdapter sensorAdapter;
    private OnFragmentInteractionListener mListener;

    private SensorViewModel viewModel;

    public SensorListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.default_info, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Utility.showBannerAdView(view, getContext(), R.string.banner_id_8);


        viewModel = new ViewModelProvider(this).get(SensorViewModel.class);

        recyclerView = view.findViewById(R.id.recylerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        sensorAdapter = new SensorAdapter(assetListClickListener, new ArrayList<>());
        recyclerView.setAdapter(sensorAdapter);

        Observer<List<Sensor>> sensorObserver = sensors -> sensorAdapter.updateSensors(sensors);
        viewModel.getSensorList(getContext()).observe(getViewLifecycleOwner(), sensorObserver);
    }

    private View.OnClickListener assetListClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Sensor currentSensor = sensorAdapter.getItem(position);
            mListener.onFragmentInteraction(currentSensor.getType());
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int sensorType);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            if (!isExporting) {
                isExporting = true;
                new ExportTask(getContext(), SensorViewModel.EXPORT_FILE_NAME, this).execute(viewModel);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onExportTaskFinished(String filePath) {
        isExporting = false;
        if (filePath != null) {
            Intent intent = new Intent(getContext(), ExportActivity.class);
            intent.putExtra(ExportUtils.EXPORT_FILE, filePath);
            startActivity(intent);
        }
    }
}
