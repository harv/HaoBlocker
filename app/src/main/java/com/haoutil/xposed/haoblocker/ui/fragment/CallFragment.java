package com.haoutil.xposed.haoblocker.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.presenter.CallPresenter;
import com.haoutil.xposed.haoblocker.presenter.CallPresenterImpl;
import com.haoutil.xposed.haoblocker.ui.CallView;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;

public class CallFragment extends BaseFragment implements CallView, BaseRecycleAdapter.OnItemClick, View.OnClickListener, DialogInterface.OnClickListener, SettingsActivity.OnMenuItemClickListener {
    private CallPresenter mCallPresenter;
    private RecyclerView rv_call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCallPresenter = new CallPresenterImpl(this);
        mCallPresenter.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            rv_call = (RecyclerView) view.findViewById(R.id.rv_call);
            rv_call.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mCallPresenter.setListItems();
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mCallPresenter.setMenuItems(menu);
    }

    @Override
    public void onClick(int position) {
    }

    @Override
    public void onLongClick(int position) {
        mCallPresenter.deleteCallConfirm(position);
    }

    @Override
    public void onClick(View v) {
        mCallPresenter.restoreCall();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                mCallPresenter.deleteCall();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mCallPresenter.deleteCallCancel();
                break;
        }
    }

    @Override
    public void onFilter(MenuItem item) {
    }

    @Override
    public void onExport(MenuItem item) {
        mCallPresenter.exportCalls();
    }

    @Override
    public void onImport(MenuItem item) {
        mCallPresenter.importCalls();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_call;
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void setCallAdapter(RecyclerView.Adapter adapter) {
        rv_call.setAdapter(adapter);
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

    @Override
    public void showTip(int resId) {
        ((SettingsActivity) getActivity()).showTip(resId, CallFragment.this);
    }

    @Override
    public void showTipInThread(int resId) {
        ((SettingsActivity) getActivity()).showTipInThread(resId);
    }

    @Override
    public void confirm() {
        confirm(this, this);
    }
}
