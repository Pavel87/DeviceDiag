package com.pacmac.devinfo.config;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pacmac.devinfo.BasicItemAdapterWithFilter;
import com.pacmac.devinfo.ExportActivity;
import com.pacmac.devinfo.PropertiesDivider;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.ExportTask;
import com.pacmac.devinfo.utils.ExportUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuildPropertiesActivity extends AppCompatActivity implements ExportTask.OnExportTaskFinished, BasicItemAdapterWithFilter.FilterResultCallback {

    private TextView paramCount;
    private SearchView searchView;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private BasicItemAdapterWithFilter basicItemAdapterWithFilter;

    private BuildPropertiesViewModel viewModel;
    private int allPropertiesCount = 0;
    private boolean isExporting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_list_layout);
        viewModel = new ViewModelProvider(this).get(BuildPropertiesViewModel.class);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)

        basicItemAdapterWithFilter = new BasicItemAdapterWithFilter(getApplicationContext(), new ArrayList<>(), this);
        mRecyclerView.setAdapter(basicItemAdapterWithFilter);
        mRecyclerView.addItemDecoration(new PropertiesDivider(this, DividerItemDecoration.VERTICAL, 16));
        paramCount = findViewById(R.id.paramCount);
        Observer<List<UIObject>> basicObserver = uiObjects -> {
            if (uiObjects != null && uiObjects.size() != 0) {
                basicItemAdapterWithFilter.updateData(uiObjects);
                allPropertiesCount = uiObjects.size();
                paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", uiObjects.size(), uiObjects.size()));
            }
        };
        viewModel.getBuildProperties(getApplicationContext()).observe(this, basicObserver);
    }

    @Override
    public void onFilterResult(int size) {
        paramCount.setText(String.format(Locale.ENGLISH, "%d / %d", size, allPropertiesCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_and_share, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                basicItemAdapterWithFilter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                basicItemAdapterWithFilter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        if (id == R.id.menu_item_share) {
            if (!isExporting) {
                isExporting = true;
                new ExportTask(getApplicationContext(), BuildPropertiesViewModel.EXPORT_FILE_NAME, this).execute(viewModel);
            }
        }
        return true;
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
