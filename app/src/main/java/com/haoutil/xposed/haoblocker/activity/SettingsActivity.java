package com.haoutil.xposed.haoblocker.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.adapter.PageFragmentAdapter;

public class SettingsActivity extends BaseActivity {
    private CoordinatorLayout cl_container;
    private FloatingActionButton fab_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PageFragmentAdapter(getFragmentManager(), SettingsActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        cl_container = (CoordinatorLayout) findViewById(R.id.cl_container);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        int position = getIntent().getIntExtra("position", -1);
        if (position > -1) {
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public void showTip(int resId, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(cl_container, resId, Snackbar.LENGTH_SHORT);
        if (onClickListener != null) {
            snackbar.setAction(R.string.snackbar_undo, onClickListener);
        }
        snackbar.show();
    }

    public void setOnAddListener(final View.OnClickListener onClickListener) {
        fab_add.setOnClickListener(onClickListener);
        fab_add.show();
    }

    public void clearOnAddListener() {
        fab_add.setOnClickListener(null);
        fab_add.hide();
    }
}
