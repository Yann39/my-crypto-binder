package com.mycryptobinder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycryptobinder.activities.IcoFragment;
import com.mycryptobinder.activities.PortfolioFragment;
import com.mycryptobinder.activities.SettingsFragment;
import com.mycryptobinder.activities.StatisticsFragment;
import com.mycryptobinder.activities.TransactionsFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages
 * <p>
 * Created by Yann on 25/05/2017
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page
        switch (position) {
            case 0:
                return new PortfolioFragment();
            case 1:
                return new TransactionsFragment();
            case 2:
                return new IcoFragment();
            case 3:
                return new StatisticsFragment();
            case 4:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

}