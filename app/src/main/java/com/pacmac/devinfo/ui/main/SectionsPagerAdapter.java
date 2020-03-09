package com.pacmac.devinfo.ui.main;

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
    private static final int[] TAB_TITLES = new int[]{R.string.cell_phone_sim_tab, R.string.cell_network_tab, R.string.carrier_config_tab};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainPhoneSIMInfoFragment.newInstance();
            case 1:
                return ActiveNetworkFragment.newInstance();
            case 2:
                return CarrierConfigFragment.newInstance();
        }
        return MainPhoneSIMInfoFragment.newInstance();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return 3;
        } else {
            return 2;
        }
    }
}