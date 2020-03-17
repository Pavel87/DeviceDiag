package com.pacmac.devinfo.gps;

import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pacmac.devinfo.R;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.main_tab, R.string.sat_tab, R.string.nmea_tab};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return MainGPSFragment.newInstance();
            case 1:
                return GPSSatellites.newInstance();
            case 2:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    return NMEAFeedFragment.newInstance();
                }
        }
        return MainGPSFragment.newInstance();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return 2;
        }
        return 3;
    }
}