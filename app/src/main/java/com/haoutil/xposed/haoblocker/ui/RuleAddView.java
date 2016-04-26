package com.haoutil.xposed.haoblocker.ui;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

public interface RuleAddView {
    Context getAppContext();

    void initView(Rule rule);

    void selectSMSBlock();

    void focusRuleInput();

    void setReturnValue(Rule rule, int position);

    void enableBlockSpinner(boolean enabled);

    void showTip(int resId);
}
