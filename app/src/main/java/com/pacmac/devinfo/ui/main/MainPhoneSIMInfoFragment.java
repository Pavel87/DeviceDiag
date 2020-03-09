package com.pacmac.devinfo.ui.main;

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

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainPhoneSIMInfoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private CellularViewModel cellularViewModel;

    private RecyclerView basicRecyclerView;
    private RecyclerView simRecyclerView;
    private RecyclerView.LayoutManager basicLinearLayoutManager;
    private RecyclerView.LayoutManager simLinearLayoutManager;
    private BasicItemAdapter basicItemAdapter;
    private SIMInfoAdapter simInfoAdapter;


    public static MainPhoneSIMInfoFragment newInstance(int index) {
        MainPhoneSIMInfoFragment fragment = new MainPhoneSIMInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cellularViewModel = new ViewModelProvider(getActivity()).get(CellularViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        cellularViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        basicRecyclerView = view.findViewById(R.id.basicInfo);
        basicRecyclerView.setHasFixedSize(true);
        basicLinearLayoutManager = new LinearLayoutManager(getContext());
        basicRecyclerView.setLayoutManager(basicLinearLayoutManager);
        basicItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        basicRecyclerView.setAdapter(basicItemAdapter);
        basicRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<UIObject>> basicObserver = uiObjects -> basicItemAdapter.updateData(uiObjects);
        cellularViewModel.getBasicInfo(getContext()).observe(getViewLifecycleOwner(), basicObserver);



        simRecyclerView = view.findViewById(R.id.simList);
        simRecyclerView.setHasFixedSize(true);
        simLinearLayoutManager = new LinearLayoutManager(getContext());
        simRecyclerView.setLayoutManager(simLinearLayoutManager);
        simInfoAdapter = new SIMInfoAdapter(getContext(), new ArrayList<>());
        simRecyclerView.setAdapter(simInfoAdapter);
        simRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<List<UIObject>>> simInfoObserver = listUiObjects -> simInfoAdapter.updateData(listUiObjects);
        cellularViewModel.getSimInfos(getContext()).observe(getViewLifecycleOwner(), simInfoObserver);
    }
}