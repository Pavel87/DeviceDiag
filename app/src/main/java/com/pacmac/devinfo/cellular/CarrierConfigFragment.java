package com.pacmac.devinfo.cellular;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * From API 26
 */
public class CarrierConfigFragment extends Fragment implements CarrierConfigAdapter.FilterResultCallback {

    private CellularViewModel cellularViewModel;

    private RecyclerView basicRecyclerView;
    private RecyclerView.LayoutManager basicLinearLayoutManager;
    private CarrierConfigAdapter carrierConfigAdapter;
    private TextView paramCount;

    static CarrierConfigFragment newInstance() {
        CarrierConfigFragment fragment = new CarrierConfigFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cellularViewModel = new ViewModelProvider(getActivity()).get(CellularViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_carrier_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        paramCount = view.findViewById(R.id.paramCount);
        basicRecyclerView = view.findViewById(R.id.carrierConfigList);
        basicRecyclerView.setHasFixedSize(true);
        basicLinearLayoutManager = new LinearLayoutManager(getContext());
        basicRecyclerView.setLayoutManager(basicLinearLayoutManager);
        carrierConfigAdapter = new CarrierConfigAdapter(getContext(), new ArrayList<>(), this);
        basicRecyclerView.setAdapter(carrierConfigAdapter);
        basicRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Observer<List<UIObject>> basicObserver = uiObjects -> {
                carrierConfigAdapter.updateData(uiObjects);
                paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", uiObjects.size(), uiObjects.size()));
            };
            cellularViewModel.getCarrierConfig(getContext()).observe(getViewLifecycleOwner(), basicObserver);

            Observer<String> filterObserver = filterQuery -> {
                carrierConfigAdapter.getFilter().filter(filterQuery);
                paramCount.setText(String.format(Locale.ENGLISH, "%d / %d",
                        carrierConfigAdapter.getItemCount(), carrierConfigAdapter.getRawDataCount()));
            };
            cellularViewModel.getConfigFilter().observe(getViewLifecycleOwner(), filterObserver);


        }
    }

    @Override
    public void onFilterResult(int size) {
        paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", size, carrierConfigAdapter.getRawDataCount()));
    }
}
