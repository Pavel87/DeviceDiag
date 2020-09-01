package com.pacmac.devinfo.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.BasicItemAdapter;
import com.pacmac.devinfo.config.BuildPropertiesActivity;
import com.pacmac.devinfo.export.ExportActivity;
import com.pacmac.devinfo.export.ExportTask;
import com.pacmac.devinfo.export.ExportUtils;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentMain extends Fragment implements ExportTask.OnExportTaskFinished {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private BasicItemAdapter mItemAdapter;

    private boolean isExporting = false;

    private MainViewModel viewModel;

    public FragmentMain() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utility.showBannerAdView(getActivity(), view, getContext(), R.string.banner_id_1);
        view.findViewById(R.id.buildPropsBtn).
                setOnClickListener(v -> startActivity(new Intent(getContext(), BuildPropertiesActivity.class)));

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mRecyclerView = view.findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(false);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new BasicItemAdapter(getContext(), new ArrayList<>());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        Observer<List<UIObject>> basicObserver = uiObjects -> mItemAdapter.updateData(uiObjects);
        viewModel.getMainInfo(getContext()).observe(getViewLifecycleOwner(), basicObserver);
        mRecyclerView.setAdapter(mItemAdapter);
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
                new ExportTask(getContext(), MainViewModel.EXPORT_FILE_NAME, this).execute(viewModel);
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
