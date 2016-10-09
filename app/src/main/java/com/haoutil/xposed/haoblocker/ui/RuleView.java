package com.haoutil.xposed.haoblocker.ui;

import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

public interface RuleView extends PromptView {
    void setRuleAdapter(RecyclerView.Adapter adapter);

    void setMenuItems(Menu menu);

    void addRule();

    void modifyRule(int position, Rule rule);

    void toggleAddButton(boolean visible);
}
