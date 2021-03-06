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

public class ActiveNetworkFragment extends Fragment {


    private CellularViewModel cellularViewModel;

    private RecyclerView networkRecyclerView;
    private RecyclerView.LayoutManager networkLinearLayoutManager;
    private BasicItemAdapter networkItemAdapter;


    public static ActiveNetworkFragment newInstance() {
        ActiveNetworkFragment fragment = new ActiveNetworkFragment();
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

        Utility.showBannerAdView(getActivity(), view, getContext(), R.string.banner_id_6);

        networkRecyclerView = view.findViewById(R.id.recylerView);
        networkRecyclerView.setHasFixedSize(false);
        networkLinearLayoutManager = new LinearLayoutManager(getContext());
        networkRecyclerView.setLayoutManager(networkLinearLayoutManager);
        networkItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        networkRecyclerView.setAdapter(networkItemAdapter);
        networkRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<UIObject>> basicObserver = uiObjects -> networkItemAdapter.updateData(uiObjects);
        cellularViewModel.getNetworkInfos(getContext()).observe(getViewLifecycleOwner(), basicObserver);
    }

}