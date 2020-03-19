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

import com.pacmac.devinfo.BasicItemAdapterWithFilter;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * From API 26
 */
public class CarrierConfigFragment extends Fragment implements BasicItemAdapterWithFilter.FilterResultCallback {

    private CellularViewModel cellularViewModel;

    private RecyclerView basicRecyclerView;
    private RecyclerView.LayoutManager basicLinearLayoutManager;
    private BasicItemAdapterWithFilter basicItemAdapterWithFilter;
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
        return inflater.inflate(R.layout.config_list_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        paramCount = view.findViewById(R.id.paramCount);
        basicRecyclerView = view.findViewById(R.id.recyclerView);
        basicRecyclerView.setHasFixedSize(true);
        basicLinearLayoutManager = new LinearLayoutManager(getContext());
        basicRecyclerView.setLayoutManager(basicLinearLayoutManager);
        basicItemAdapterWithFilter = new BasicItemAdapterWithFilter(getContext(), new ArrayList<>(), this);
        basicRecyclerView.setAdapter(basicItemAdapterWithFilter);
        basicRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Observer<List<UIObject>> basicObserver = uiObjects -> {
                basicItemAdapterWithFilter.updateData(uiObjects);
                paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", uiObjects.size(), uiObjects.size()));
            };
            cellularViewModel.getCarrierConfig(getContext()).observe(getViewLifecycleOwner(), basicObserver);

            Observer<String> filterObserver = filterQuery -> {
                basicItemAdapterWithFilter.getFilter().filter(filterQuery);
                paramCount.setText(String.format(Locale.ENGLISH, "%d / %d",
                        basicItemAdapterWithFilter.getItemCount(), basicItemAdapterWithFilter.getRawDataCount()));
            };
            cellularViewModel.getConfigFilter().observe(getViewLifecycleOwner(), filterObserver);

        }
    }

    @Override
    public void onFilterResult(int size) {
        paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", size, basicItemAdapterWithFilter.getRawDataCount()));
    }
}
