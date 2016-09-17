package com.haoutil.xposed.haoblocker.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.ui.adapter.PageFragmentAdapter;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    private CoordinatorLayout cl_container;
    private FloatingActionButton fab_add;

    private OnAddListener onAddListener;
    private OnMenuItemClickListener onMenuItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PageFragmentAdapter(getFragmentManager(), SettingsActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        cl_container = (CoordinatorLayout) findViewById(R.id.cl_container);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);

        int position = getIntent().getIntExtra("position", -1);
        if (position > -1 && position < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_all:
            case R.id.filter_call:
            case R.id.filter_sms:
            case R.id.filter_except:
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onFilter(item);
                }
                break;
            case R.id.backup:
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onBackup(item);
                }
                break;
            case R.id.restore:
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onRestore(item);
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                if (onAddListener != null) {
                    onAddListener.onAdd();
                }
                break;
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public void showConfirm(DialogInterface.OnClickListener onPositiveListener, DialogInterface.OnClickListener onNegativeListener) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(getResources().getString(R.string.discard_dialog_title))
                .setMessage(getResources().getString(R.string.discard_dialog_message))
                .setPositiveButton(R.string.discard_dialog_button_ok, onPositiveListener)
                .setNegativeButton(R.string.discard_dialog_button_cancel, onNegativeListener)
                .create()
                .show();
    }

    public void showTip(final int resId, final View.OnClickListener onClickListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar snackbar = Snackbar.make(cl_container, resId, Snackbar.LENGTH_SHORT);
                if (onClickListener != null) {
                    snackbar.setAction(R.string.snackbar_undo, onClickListener);
                }
                snackbar.show();
            }
        });
    }

    public void setOnAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
        if (this.onAddListener == null) {
            fab_add.hide();
        } else {
            fab_add.show();
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public interface OnAddListener {
        void onAdd();
    }

    public interface OnMenuItemClickListener {
        void onFilter(MenuItem item);

        void onBackup(MenuItem item);

        void onRestore(MenuItem item);
    }
}
