package com.pacmac.devinfo.camera;

import android.content.Context;
import android.hardware.Camera;

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
    private static final int[] TAB_TITLES = new int[]{R.string.tab_general, R.string.tab_camera};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {
            return CameraGeneralFragment.newInstance();
        } else {
            return CameraInfoFragment.newInstance(position - 1);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position > 0) {
            return mContext.getResources().getString(TAB_TITLES[1]) + " " + position;
        } else {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return CameraUtils.cameraCount + 1;
    }
}