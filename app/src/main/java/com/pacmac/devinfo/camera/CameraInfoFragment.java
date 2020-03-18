package com.pacmac.devinfo.camera;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.BasicItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class CameraInfoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private BasicItemAdapter mItemAdapter;

    private TextView counter1;
    private RecyclerView gridPictureRecyclerView;
    private RecyclerView gridVideoRecyclerView;

    private TextView counter2;
    private ResolutionItemAdapter pictureAdapter;
    private ResolutionItemAdapter videoAdapter;

    private CameraViewModel viewModel;


    private int camID = 0;

    public static CameraInfoFragment newInstance(int cameraID) {
        CameraInfoFragment fragment = new CameraInfoFragment();
        Bundle args = new Bundle();
        args.putInt("ID", cameraID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(getActivity()).get(CameraViewModel.class);
        camID = getArguments().getInt("ID");

        mRecyclerView = view.findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Observer<List<List<UIObject>>> basicObserver = camerasData -> {
            if (camerasData != null && camID < camerasData.size()) {
                mItemAdapter.updateData(camerasData.get(camID));
            }
        };
        viewModel.getCameraListData().observe(getViewLifecycleOwner(), basicObserver);

        counter1 = view.findViewById(R.id.counter1);
        gridPictureRecyclerView = view.findViewById(R.id.gridView1);
        gridPictureRecyclerView.setHasFixedSize(true);
        gridPictureRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        gridPictureRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        pictureAdapter = new ResolutionItemAdapter(getContext(), new ArrayList<>());
        gridPictureRecyclerView.setAdapter(pictureAdapter);

        Observer<List<List<ResolutionObject>>> pictureResObserver = resolutions -> {
            if (resolutions != null && camID < resolutions.size()) {
                pictureAdapter.updateData(resolutions.get(camID));
                counter1.setText(String.valueOf(resolutions.get(camID).size()));
            }
        };
        viewModel.getCameraListPicResolutions().observe(getViewLifecycleOwner(), pictureResObserver);


        counter2 = view.findViewById(R.id.counter2);
        gridVideoRecyclerView = view.findViewById(R.id.gridView2);
        gridVideoRecyclerView.setHasFixedSize(true);
        gridVideoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        videoAdapter = new ResolutionItemAdapter(getContext(), new ArrayList<>());
        gridVideoRecyclerView.setAdapter(videoAdapter);

        Observer<List<List<ResolutionObject>>> videoResObserver = resolutions -> {
            if (resolutions != null && camID < resolutions.size()) {
                videoAdapter.updateData(resolutions.get(camID));
                counter2.setText(String.valueOf(resolutions.get(camID).size()));
            }
        };
        viewModel.getCameraListVideoResolutions().observe(getViewLifecycleOwner(), videoResObserver);


    }


}
