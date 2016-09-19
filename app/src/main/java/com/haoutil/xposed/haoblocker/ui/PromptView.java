package com.haoutil.xposed.haoblocker.ui;

import android.content.DialogInterface;
import android.view.View;

public interface PromptView extends BaseView {
    void showConfirm(int resId, DialogInterface.OnClickListener onPositiveListener, DialogInterface.OnClickListener onNegativeListener);

    void showTip(int resId, boolean showAction);

    void onActionClick(View action);
}
