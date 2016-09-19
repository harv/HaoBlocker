package com.haoutil.xposed.haoblocker.model;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

public interface RuleAddModel {
    long saveRule(Rule rule);

    void updateRule(Rule rule);
}
