package com.haoutil.xposed.haoblocker.model;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

public interface RuleAddModel {
    void init(Context context);

    long saveRule(Rule rule);

    void updateRule(Rule rule);
}
