package com.haoutil.xposed.haoblocker.ui.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.ui.BaseView;
import com.haoutil.xposed.haoblocker.ui.PromptView;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;

public abstract class PromptFragment extends Fragment implements BaseView, PromptView {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void showConfirm() {
        ((SettingsActivity) getActivity()).showConfirm(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onConfirmOK();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onConfirmCancel();
            }
        });
    }

    @Override
    public void showTip(int resId, boolean showAction) {
        ((SettingsActivity) getActivity()).showTip(resId, showAction ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionClick(v);
            }
        } : null);
    }
}
