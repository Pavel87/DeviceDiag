package com.pacmac.devinfo.cellular;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.pacmac.devinfo.export.ExportActivity;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.export.ExportTask;
import com.pacmac.devinfo.export.ExportUtils;

public class CellularInfo extends AppCompatActivity implements ExportTask.OnExportTaskFinished {


    private PSL psl;
    private TelephonyManager telephonyManager;
    private int pslListen = PhoneStateListener.LISTEN_NONE;
    private Menu menu = null;
    private SearchView searchView;
    private CellularViewModel cellularViewModel;

    private boolean isExporting = false;

    private int tabSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_phone_and_sim);
        getSupportActionBar().setHomeButtonEnabled(true);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelected = tab.getPosition();
                switch (tabSelected) {
                    case 0:
                        getSupportActionBar().setTitle(getString(R.string.title_phone_and_sim));
                        if (menu != null) {
                            menu.getItem(0).setVisible(false);
                        }
                        break;
                    case 1:
                        getSupportActionBar().setTitle(R.string.active_network);
                        if (menu != null) {
                            menu.getItem(0).setVisible(false);
                        }
                        break;
                    case 2:
                        getSupportActionBar().setTitle(R.string.connected_cells);
                        if (menu != null) {
                            menu.getItem(0).setVisible(false);
                        }
                        break;
                    case 3:
                        getSupportActionBar().setTitle(R.string.carrier_config);
                        if (menu != null) {
                            menu.getItem(0).setVisible(true);
                        }
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
                | PhoneStateListener.LISTEN_SERVICE_STATE;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            pslListen = pslListen | PhoneStateListener.LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pslListen = pslListen | PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            try {
                telephonyManager.listen(psl, pslListen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(() -> {
            try {
                telephonyManager.listen(psl, PhoneStateListener.LISTEN_NONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // SHARE CPU INFO VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_search_and_share, menu);
        if (tabSelected < 3) {
            menu.getItem(0).setVisible(false);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
            if (!isExporting) {
                isExporting = true;
                new ExportTask(getApplicationContext(), CellularViewModel.EXPORT_FILE_NAME, this).execute(cellularViewModel);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
                return;
            }
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
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