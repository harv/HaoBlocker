package com.haoutil.xposed.haoblocker.model;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

import java.util.List;

public interface RuleModel {
    void init(Context context);

    List<Rule> getRules(int ruleType);

    long saveRule(Rule rule);

    void deleteRule(Rule rule);

    long restoreRule(Rule rule);
}
