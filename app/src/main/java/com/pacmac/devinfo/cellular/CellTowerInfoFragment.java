package com.pacmac.devinfo.cellular;

import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class CellTowerInfoFragment extends Fragment {


    private CellularViewModel cellularViewModel;

    private RecyclerView cellRecyclerView;
    private RecyclerView.LayoutManager cellLinearLayoutManager;
    private BasicItemAdapter cellItemAdapter;


    public static CellTowerInfoFragment newInstance() {
        CellTowerInfoFragment fragment = new CellTowerInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cellularViewModel = new ViewModelProvider(getActivity()).get(CellularViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.default_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Utility.showBannerAdView(view, getContext(), R.string.banner_id_7);

        cellRecyclerView = view.findViewById(R.id.recylerView);
        cellRecyclerView.setHasFixedSize(false);
        cellLinearLayoutManager = new LinearLayoutManager(getContext());
        cellRecyclerView.setLayoutManager(cellLinearLayoutManager);
        cellItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        cellRecyclerView.setAdapter(cellItemAdapter);
        cellRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<UIObject>> basicObserver = uiObjects -> cellItemAdapter.updateData(uiObjects);
        cellularViewModel.getCellInfos(getContext()).observe(getViewLifecycleOwner(), basicObserver);
    }

}
