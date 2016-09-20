package com.haoutil.xposed.haoblocker.ui.fragment;

import android.content.DialogInterface;
import android.view.View;

import com.haoutil.xposed.haoblocker.ui.PromptView;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;

public abstract class PromptFragment extends BaseFragment implements PromptView {
    @Override
    public void showConfirm(int resId, DialogInterface.OnClickListener onPositiveListener, DialogInterface.OnClickListener onNegativeListener) {
        ((SettingsActivity) getActivity()).showConfirm(resId, onPositiveListener, onNegativeListener);
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
