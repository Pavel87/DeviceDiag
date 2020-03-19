package com.pacmac.devinfo.cpu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pacmac.devinfo.export.ExportActivity;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.BasicItemAdapter;
import com.pacmac.devinfo.export.ExportTask;
import com.pacmac.devinfo.export.ExportUtils;
import com.pacmac.devinfo.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class CPUInfo extends AppCompatActivity implements ExportTask.OnExportTaskFinished {


    CPUViewModel viewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private BasicItemAdapter mItemAdapter;
    private Handler handler;

    private boolean isExporting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_info);

        Utility.showBannerAdView(this.findViewById(android.R.id.content), getApplicationContext(), R.string.banner_id_4);

        handler = new Handler();
        viewModel = new ViewModelProvider(this).get(CPUViewModel.class);

        mRecyclerView = findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new BasicItemAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Observer<List<UIObject>> basicObserver = uiObjects -> mItemAdapter.updateData(uiObjects);
        viewModel.getCpuInfo().observe(this, basicObserver);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewModel != null) {
                viewModel.getCpuInfo();
                handler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    // SHARE CPU INFO VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
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
                new ExportTask(getApplicationContext(), CPUViewModel.EXPORT_FILE_NAME, this).execute(viewModel);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExportTaskFinished(String filePath) {
        isExporting = false;
        if (filePath != null) {
            Intent intent = new Intent(getApplicationContext(), ExportActivity.class);
            intent.putExtra(ExportUtils.EXPORT_FILE, filePath);
            startActivity(intent);
        }
    }
}
