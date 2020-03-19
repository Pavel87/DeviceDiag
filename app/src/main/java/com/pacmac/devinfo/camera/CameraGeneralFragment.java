package com.pacmac.devinfo.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.BasicItemAdapter;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class CameraGeneralFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private BasicItemAdapter mItemAdapter;
    private CameraViewModel viewModel;

    public static CameraGeneralFragment newInstance() {
        CameraGeneralFragment fragment = new CameraGeneralFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(CameraViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.default_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Utility.showBannerAdView(view, getContext(), R.string.banner_id_5);

        mRecyclerView = view.findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<UIObject>> basicObserver = uiObjects -> mItemAdapter.updateData(uiObjects);
        viewModel.getCameraInfoGeneral(getContext()).observe(getViewLifecycleOwner(), basicObserver);
    }

}