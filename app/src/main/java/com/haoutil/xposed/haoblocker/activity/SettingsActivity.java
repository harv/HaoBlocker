package com.haoutil.xposed.haoblocker.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.fragment.CallFragment;
import com.haoutil.xposed.haoblocker.fragment.GeneralFragment;
import com.haoutil.xposed.haoblocker.fragment.RuleFragment;
import com.haoutil.xposed.haoblocker.fragment.SMSFragment;

public class SettingsActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mMenuTitles = getResources().getStringArray(R.array.array_menus);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
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

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new GeneralFragment();
                break;
            case 1:
                fragment = new RuleFragment();
                break;
            case 2:
                fragment = new SMSFragment();
                break;
            case 3:
                fragment = new CallFragment();
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        setTitle(mMenuTitles[position]);

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
