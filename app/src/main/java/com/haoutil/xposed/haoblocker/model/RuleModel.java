package com.haoutil.xposed.haoblocker.model;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

import java.util.List;

public interface RuleModel {
    List<Rule> getRules(int ruleType);

    long saveRule(Rule rule);

    void deleteRule(Rule rule);

    long restoreRule(Rule rule);
}
