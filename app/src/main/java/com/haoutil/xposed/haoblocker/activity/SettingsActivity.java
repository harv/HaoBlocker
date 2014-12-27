package com.haoutil.xposed.haoblocker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.fragment.BaseFragment;
import com.haoutil.xposed.haoblocker.fragment.CallFragment;
import com.haoutil.xposed.haoblocker.fragment.GeneralFragment;
import com.haoutil.xposed.haoblocker.fragment.RuleFragment;
import com.haoutil.xposed.haoblocker.fragment.SMSFragment;

import butterknife.InjectView;
import butterknife.OnItemClick;

public class SettingsActivity extends BaseActivity {
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer)
    ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

    private BaseFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = mDrawerTitle = getTitle();

        mMenuTitles = getResources().getStringArray(R.array.array_menus);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mMenuTitles));

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                if (mFragment != null) {
                    mFragment.onResetActionBarButtons(true);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                if (mFragment != null) {
                    mFragment.onResetActionBarButtons(false);
                }
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getInt("position", -1) != -1) {
            selectItem(bundle.getInt("position"));
        } else if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @OnItemClick(R.id.left_drawer)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                mFragment = new GeneralFragment();
                break;
            case 1:
                mFragment = new RuleFragment();
                break;
            case 2:
                mFragment = new SMSFragment();
                break;
            case 3:
                mFragment = new CallFragment();
                break;
        }

        if (mFragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, mFragment).commit();
        }

        setTitle(mMenuTitles[position]);

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getInt("position", -1) != -1) {
            selectItem(bundle.getInt("position"));
        }
    }
}
