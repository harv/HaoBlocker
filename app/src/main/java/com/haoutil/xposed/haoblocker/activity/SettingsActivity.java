package com.haoutil.xposed.haoblocker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.adapter.PageFragmentAdapter;

public class SettingsActivity extends BaseActivity {
    public static final int SHOW_NONE = 0;
    public static final int SHOW_FILTER = 1;
    public static final int SHOW_EXPORT = 2;
    public static final int SHOW_IMPORT = 4;

    private Handler handler;

    private CoordinatorLayout cl_container;
    private FloatingActionButton fab_add;
    private MenuItem menu_filter;
    private MenuItem menu_export;
    private MenuItem menu_import;

    private OnMenuItemClickListener onFilterListener;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        menu_filter = menu.findItem(R.id.filter);
        menu_export = menu.findItem(R.id.export);
        menu_import = menu.findItem(R.id.import0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_all:
            case R.id.filter_call:
            case R.id.filter_sms:
            case R.id.filter_except:
                if (onFilterListener != null) {
                    onFilterListener.onFilter(item);
                }
                break;
            case R.id.export:
                if (onFilterListener != null) {
                    onFilterListener.onExport(item);
                }
                break;
            case R.id.import0:
                if (onFilterListener != null) {
                    onFilterListener.onImport(item);
                }
                break;
        }
        return true;
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

    public void showTipInThread(final int resId) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(cl_container, resId, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void setOnAddListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            fab_add.setOnClickListener(onClickListener);
            fab_add.show();
        } else {
            fab_add.setOnClickListener(null);
            fab_add.hide();
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener, int show) {
        onFilterListener = onMenuItemClickListener;
        menu_filter.setVisible((show & SHOW_FILTER) == SHOW_FILTER);
        menu_export.setVisible((show & SHOW_EXPORT) == SHOW_EXPORT);
        menu_import.setVisible((show & SHOW_IMPORT) == SHOW_IMPORT);
    }

    public interface OnMenuItemClickListener {
        void onFilter(MenuItem item);

        void onExport(MenuItem item);

        void onImport(MenuItem item);
    }
}
