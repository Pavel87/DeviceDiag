package com.pacmac.devinfo.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class DiagMain extends AppCompatActivity {

//    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private final static String MAIN_PREF_FILE = "de_vi_ce";
    private final static String VERSION_KEY = "version_key";

    private boolean isLocationPermissionEnabled = true;

//    private MainViewModel viewModel = null;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//    }
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_diag_main);
//
//        MobileAds.initialize(this, initializationStatus -> {
//        });
//
//
//        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
//        viewModel.checkIfAppIsUpToDate(getApplicationContext());
//
//        // Initialize Camera count
////        CameraUtils.loadCameraCount();
//
//        // Check if user disabled LOCATION permission at some point
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            isLocationPermissionEnabled = Utility.checkPermission(getApplicationContext(), Utility.LOCATION_PERMISSION);
//        }
//        if (!isLocationPermissionEnabled) {
//            Utility.requestPermissions(this, new String[] {Utility.LOCATION_PERMISSION});
//        }
//
//        // Set up the action bar.
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = findViewById(R.id.pager);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//
//        // When swiping between different sections, select the corresponding
//        // tab. We can also use ActionBar.Tab#select() to do this if we have
//        // a reference to the Tab.
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });
//
//        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by
//            // the adapter. Also specify this Activity object, which implements
//            // the TabListener interface, as the callback (listener) for when
//            // this tab is selected.
//            actionBar.addTab(actionBar.newTab()
//                    .setText(mSectionsPagerAdapter.getPageTitle(i))
//                    .setTabListener(this));
//        }
//
//
//        // Check if user disabled LOCATION permission at some point
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            isLocationPermissionEnabled = Utility.checkPermission(getApplicationContext(), Utility.LOCATION_PERMISSION);
//        }
//        if (!isLocationPermissionEnabled) {
//            Utility.requestPermissions(this, new String[] {Utility.LOCATION_PERMISSION});
//        }
//
//        if (isLocationPermissionEnabled && checkIfAppUpdated()) {
//            startActivity(new Intent(getApplicationContext(), NewFeaturesActivity.class));
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        isLocationPermissionEnabled = Utility.checkPermission(getApplicationContext(), Utility.LOCATION_PERMISSION);
//        if (isLocationPermissionEnabled && checkIfAppUpdated()) {
//            startActivity(new Intent(getApplicationContext(), NewFeaturesActivity.class));
//        }
//    }
//
//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // When the given tab is selected, switch to the corresponding page in
//        // the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());
//        if (viewModel != null) {
//            if (viewModel.doesAppNeedsUpgrade() && !viewModel.isDoNotShowNewVersioDialog()) {
//                viewModel.setDoNotShowNewVersioDialog(true);
//                Utility.showUpdateAppDialog(DiagMain.this);
//            }
//        }
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//
//    /**
//     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
//     * one of the sections/tabs/pages.
//     */
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
////            if (position == 0) {
////                FragmentMain frag = new FragmentMain();
////                return frag;
////            } else {
////                FragmentDashboard frag = new FragmentDashboard();
////                return frag;
////            }
//            return Fragment()
//        }
//
//        @Override
//        public int getCount() {
//            //two tabs
//            return 2;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);
//            }
//            return null;
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            Vibrator myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
//            myVib.vibrate(60);
//            showExitAlert();
//            return true;
//        }
//        return false;
//    }
//
//
//    private void showExitAlert() {
//
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.exit_dialog);
//        dialog.setCancelable(true);
//
//        Button yesButton = dialog.findViewById(R.id.yesExit);
//        yesButton.setOnClickListener(view -> {
//            moveTaskToBack(true);
//            finishAffinity();
//        });
//
//        Button noButton = dialog.findViewById(R.id.noExit);
//        noButton.setOnClickListener(view -> dialog.dismiss());
//
//        dialog.show();
//    }
//
//
//    private boolean checkIfAppUpdated() {
//        SharedPreferences preferences = getSharedPreferences(MAIN_PREF_FILE, MODE_PRIVATE);
//        int versionCode = preferences.getInt(VERSION_KEY, 0);
//        int appVersionCode = -1;
//        try {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
//                appVersionCode = (int) (getPackageManager().getPackageInfo(getPackageName(), 0).getLongVersionCode() & 0x0000FFFF);
//            } else {
//                appVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        if (appVersionCode != versionCode) {
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putInt(VERSION_KEY, appVersionCode);
//            editor.apply();
//            if (versionCode == 0) {
//                return false;
//            }
//            return true;
//        }
//        return false;
//    }
}
