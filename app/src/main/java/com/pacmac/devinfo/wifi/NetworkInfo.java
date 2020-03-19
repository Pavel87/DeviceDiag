package com.pacmac.devinfo.wifi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.export.ExportActivity;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.BasicItemAdapter;
import com.pacmac.devinfo.export.ExportTask;
import com.pacmac.devinfo.export.ExportUtils;

import java.util.ArrayList;
import java.util.List;

public class NetworkInfo extends AppCompatActivity implements ExportTask.OnExportTaskFinished {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private BasicItemAdapter mItemAdapter;

    private boolean isExporting = false;
    private NetworkViewModel viewModel;

    private Handler handler = null;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewModel != null) {
                viewModel.getWifiInfo(getApplicationContext());
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_info);

        handler = new Handler();
        viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        mRecyclerView = findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mItemAdapter = new BasicItemAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Observer<List<UIObject>> basicObserver = uiObjects -> mItemAdapter.updateData(uiObjects);
        viewModel.getWifiInfo(getApplicationContext()).observe(this, basicObserver);


        // Open ICMP PING tool in market store
        findViewById(R.id.icmpPingTool).setOnClickListener(view -> {
            String appPackage = "com.pacmac.pinger";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000);
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
                new ExportTask(getApplicationContext(), NetworkViewModel.EXPORT_FILE_NAME, this).execute(viewModel);
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
