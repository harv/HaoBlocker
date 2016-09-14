package com.haoutil.xposed.haoblocker.ui;

import android.view.View;

public interface PromptView {
    void showConfirm();

    void onConfirmOK();

    void onConfirmCancel();

    void showTip(int resId, boolean showAction);

    void onActionClick(View action);
}
