package com.haoutil.xposed.haoblocker.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;

public interface RuleView {
    Context getApplicationContext();

    void setRuleAdapter(RecyclerView.Adapter adapter);

    void setMenuItems(Menu menu);

    BaseRecycleAdapter.OnItemClick getOnItemClick();

    void addRule();

    void modifyRule(int position, Rule rule);

    void toggleAddButton(boolean visible);

    void showTip(int resId);

    void showTipInThread(int resId);

    void confirm();
}
