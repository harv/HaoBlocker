package com.haoutil.xposed.haoblocker.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.ui.fragment.CallFragment;
import com.haoutil.xposed.haoblocker.ui.fragment.GeneralFragment;
import com.haoutil.xposed.haoblocker.ui.fragment.RuleFragment;
import com.haoutil.xposed.haoblocker.ui.fragment.SMSFragment;

public class PageFragmentAdapter extends FragmentPagerAdapter {
    private String[] tabTitles;
    private Fragment[] fragments;

    public PageFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);

        tabTitles = context.getResources().getStringArray(R.array.tabTitles);
        fragments = new Fragment[]{new GeneralFragment(), new RuleFragment(), new SMSFragment(), new CallFragment()};
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}