package com.haoutil.xposed.haoblocker.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnCheckedChanged;

public class GeneralFragment extends BaseFragment {
    private SettingsHelper settingsHelper;

    @InjectView(R.id.tv_about)
    TextView tv_about;
    @InjectView(R.id.sw_enable)
    Switch sw_enable;
    @InjectViews({R.id.sw_sms_enable, R.id.sw_call_enable})
    List<Switch> switches;

    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsHelper = new SettingsHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = getView();

        try {
            String versionName = getActivity().getApplicationContext().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            tv_about.setText(tv_about.getText() + " v" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }

        sw_enable.setChecked(settingsHelper.isEnable());

        Switch sw_sms_enable = switches.get(0);
        sw_sms_enable.setEnabled(sw_enable.isChecked());
        sw_sms_enable.setChecked(settingsHelper.isEnableSMS());

        Switch sw_call_enable = switches.get(1);
        sw_call_enable.setEnabled(sw_enable.isChecked());
        sw_call_enable.setChecked(settingsHelper.isEnableCall());

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_general;
    }

    @OnCheckedChanged({R.id.sw_enable, R.id.sw_sms_enable, R.id.sw_call_enable})
    public void onCheckedChanged(CompoundButton v, boolean b) {
        switch (v.getId()) {
            case R.id.sw_enable:
                ButterKnife.apply(switches, ENABLED, b);

                settingsHelper.setEnable(b);
                break;
            case R.id.sw_sms_enable:
                settingsHelper.setEnableSMS(b);
                break;
            case R.id.sw_call_enable:
                settingsHelper.setEnableCall(b);
                break;
        }
    }

    @Override
    public void onResetActionBarButtons(boolean isMenuOpen) {

    }
}
