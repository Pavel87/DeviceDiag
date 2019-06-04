package com.pacmac.devinfo;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuildPropertiesActivity extends AppCompatActivity implements BuildPropsAdapter.FilterResultCallback {

    private TextView propertyCountView;
    private SearchView searchView;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private BuildPropsAdapter buildPropsAdapter;

    private List<BuildProperty> buildPropertyList = new ArrayList<>();

    private int allPropertiesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_properties);

        mRecyclerView = findViewById(R.id.propsList);
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)

        buildPropertyList = Utility.getBuildPropsList(getApplicationContext());

        buildPropsAdapter = new BuildPropsAdapter(getApplicationContext(), buildPropertyList, this);
        mRecyclerView.setAdapter(buildPropsAdapter);
        mRecyclerView.addItemDecoration(new PropertiesDivider(this, DividerItemDecoration.VERTICAL, 16));
        propertyCountView = findViewById(R.id.propertyCount);

        allPropertiesCount = buildPropertyList.size();
        propertyCountView.setText(String.format(Locale.ENGLISH, "%d / %d", allPropertiesCount, allPropertiesCount));
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
        getMenuInflater().inflate(R.menu.menu_build_props, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search_build_prop)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                buildPropsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                buildPropsAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onFilterResult(int size) {
        propertyCountView.setText(String.format(Locale.ENGLISH, "%d / %d", size, allPropertiesCount));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search_build_prop) {
            return true;
        }

        if (id == R.id.menu_item_share) {
            Utility.exporData(BuildPropertiesActivity.this, getResources().getString(R.string.title_activity_build_properties), updateMessageForExport());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO fix share intents to be available all since activity start
    private String updateMessageForExport() {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_build_properties));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");
        //body

        for (BuildProperty buildProperty : buildPropertyList) {
            sb.append(buildProperty.getKey() + ": " + buildProperty.getValue());
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        return sb.toString();
    }
}
