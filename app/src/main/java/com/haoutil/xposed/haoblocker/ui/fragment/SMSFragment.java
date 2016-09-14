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

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.presenter.SMSPresenter;
import com.haoutil.xposed.haoblocker.presenter.SMSPresenterImpl;
import com.haoutil.xposed.haoblocker.ui.SMSView;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;

public class SMSFragment extends PromptFragment implements SMSView, BaseRecycleAdapter.OnItemClick, SettingsActivity.OnMenuItemClickListener {
    private SMSPresenter mSMSPresenter;
    private RecyclerView rv_sms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSMSPresenter = new SMSPresenterImpl(this);
        mSMSPresenter.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            rv_sms = (RecyclerView) view.findViewById(R.id.rv_sms);
            rv_sms.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
    public void onConfirmOK() {
        mSMSPresenter.deleteSMS();
    }

    @Override
    public void onConfirmCancel() {
        mSMSPresenter.deleteSMSCancel();
    }

    @Override
    public void onActionClick(View action) {
        mSMSPresenter.restoreSMS();
    }

    @Override
    public void onItemClick(int position) {
    }

    @Override
    public void onItemLongClick(int position) {
        mSMSPresenter.deleteSMSConfirm(position);
    }

    @Override
    public void onFilter(MenuItem item) {
    }

    @Override
    public void onExport(MenuItem item) {
        mSMSPresenter.exportSMSes();
    }

    @Override
    public void onImport(MenuItem item) {
        mSMSPresenter.importSMSes();
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void setSMSAdapter(RecyclerView.Adapter adapter) {
        rv_sms.setAdapter(adapter);
    }

    @Override
    public void setMenuItems(Menu menu) {
        menu.findItem(R.id.export).setVisible(true);
        menu.findItem(R.id.import0).setVisible(true);
        ((SettingsActivity) getActivity()).setOnMenuItemClickListener(this);
    }

    @Override
    public BaseRecycleAdapter.OnItemClick getOnItemClick() {
        return this;
    }
}
