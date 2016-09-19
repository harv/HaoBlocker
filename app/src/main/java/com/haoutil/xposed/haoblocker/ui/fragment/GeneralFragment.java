package com.haoutil.xposed.haoblocker.ui.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.presenter.GeneralPresenter;
import com.haoutil.xposed.haoblocker.presenter.impl.GeneralPresenterImpl;
import com.haoutil.xposed.haoblocker.ui.GeneralView;

public class GeneralFragment extends BaseFragment implements GeneralView, CompoundButton.OnCheckedChangeListener {
    private GeneralPresenter mGeneralPresenter;

    private TextView tv_about;
    private Switch sw_enable;
    private Switch sw_sms_enable;
    private Switch sw_call_enable;
    private Switch sw_show_notification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeneralPresenter = new GeneralPresenterImpl(this);
        mGeneralPresenter.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            tv_about = (TextView) view.findViewById(R.id.tv_about);
            sw_enable = (Switch) view.findViewById(R.id.sw_enable);
            sw_sms_enable = (Switch) view.findViewById(R.id.sw_sms_enable);
            sw_call_enable = (Switch) view.findViewById(R.id.sw_call_enable);
            sw_show_notification = (Switch) view.findViewById(R.id.sw_show_notification);

            mGeneralPresenter.initView();
        }

        return view;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_general;
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean b) {
        switch (v.getId()) {
            case R.id.sw_enable:
                mGeneralPresenter.enable(b);
                break;
            case R.id.sw_sms_enable:
                mGeneralPresenter.enableSMS(b);
                break;
            case R.id.sw_call_enable:
                mGeneralPresenter.enableCall(b);
                break;
            case R.id.sw_show_notification:
                mGeneralPresenter.enableNotification(b);
                break;
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void setOnCheckedChangeListener() {
        sw_enable.setOnCheckedChangeListener(this);
        sw_sms_enable.setOnCheckedChangeListener(this);
        sw_call_enable.setOnCheckedChangeListener(this);
        sw_show_notification.setOnCheckedChangeListener(this);
    }

    @Override
    public void setAbout() {
        String versionName;
        try {
            versionName = "v" + getActivity().getApplicationContext().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }
        tv_about.setText(String.format(getResources().getString(R.string.general_about_title), versionName));
    }

    @Override
    public void check(boolean checked) {
        sw_enable.setChecked(checked);
    }

    @Override
    public void checkSMS(boolean checked) {
        sw_sms_enable.setChecked(checked);
    }

    @Override
    public void checkCall(boolean checked) {
        sw_call_enable.setChecked(checked);
    }

    @Override
    public void checkNotification(boolean checked) {
        sw_show_notification.setChecked(checked);
    }

    @Override
    public void enable(boolean enabled) {
        sw_sms_enable.setEnabled(enabled);
        sw_call_enable.setEnabled(enabled);
        sw_show_notification.setEnabled(enabled);
    }
}
