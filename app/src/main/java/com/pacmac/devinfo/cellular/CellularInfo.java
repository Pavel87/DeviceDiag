package com.pacmac.devinfo.cellular;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pacmac.devinfo.R;

public class CellularInfo extends AppCompatActivity {


    private PSL psl;
    private TelephonyManager telephonyManager;
    private int pslListen = PhoneStateListener.LISTEN_NONE;
    private Menu menu = null;
    private SearchView searchView;
    private CellularViewModel cellularViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellular_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Phone & SIM");
        getSupportActionBar().setHomeButtonEnabled(true);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportActionBar().setTitle("Phone & SIM");
                        menu.getItem(0).setVisible(false);
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Active Network");
                        menu.getItem(0).setVisible(false);
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Connected Cells");
                        menu.getItem(0).setVisible(false);
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Carrier Config");
                        menu.getItem(0).setVisible(true);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        cellularViewModel = new ViewModelProvider(this).get(CellularViewModel.class);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        psl = new PSL(cellularViewModel, getApplicationContext());

        pslListen = pslListen
                | PhoneStateListener.LISTEN_DATA_ACTIVITY
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                | PhoneStateListener.LISTEN_CELL_INFO
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE
                | PhoneStateListener.LISTEN_SERVICE_STATE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        telephonyManager.listen(psl, pslListen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        telephonyManager.listen(psl, PhoneStateListener.LISTEN_NONE);
    }

    // SHARE CPU INFO VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_and_share, menu);
        menu.getItem(0).setVisible(false);
        this.menu = menu;

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
                if (cellularViewModel != null) {
                    cellularViewModel.setConfigFilter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (cellularViewModel != null) {
                    cellularViewModel.setConfigFilter(query);
                }
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}