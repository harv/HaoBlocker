package com.haoutil.xposed.haoblocker.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.AppContext;
import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.presenter.SMSPresenter;
import com.haoutil.xposed.haoblocker.presenter.impl.SMSPresenterImpl;
import com.haoutil.xposed.haoblocker.ui.SMSView;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;

public class SMSFragment extends PromptFragment implements SMSView, SettingsActivity.OnMenuItemClickListener {
    private SMSPresenter mSMSPresenter;
    private RecyclerView rv_sms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSMSPresenter = new SMSPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            rv_sms = (RecyclerView) view.findViewById(R.id.rv_sms);
            Context context = AppContext.getsInstance().getApplicationContext();
            rv_sms.setLayoutManager(new LinearLayoutManager(context));
            mSMSPresenter.setListItems();
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSMSPresenter.setMenuItems(menu);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_sms;
    }

    @Override
    public void onActionClick(View action) {
        mSMSPresenter.restoreSMS();
    }

    @Override
    public void onFilter(MenuItem item) {
    }

    @Override
    public void onBackup(MenuItem item) {
        mSMSPresenter.exportSMSes();
    }

    @Override
    public void onRestore(MenuItem item) {
        mSMSPresenter.importSMSes();
    }

    @Override
    public void setSMSAdapter(RecyclerView.Adapter adapter) {
        rv_sms.setAdapter(adapter);
    }

    @Override
    public void setMenuItems(Menu menu) {
        menu.findItem(R.id.backup).setVisible(true);
        menu.findItem(R.id.restore).setVisible(true);
        ((SettingsActivity) getActivity()).setOnMenuItemClickListener(this);
    }
}
