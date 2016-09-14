package com.haoutil.xposed.haoblocker.model;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.util.List;

public class RuleModelImpl implements RuleModel {
    private BlockerManager mBlockerManager;

    @Override
    public void init(Context context) {
        mBlockerManager = new BlockerManager(context);
    }

    @Override
    public List<Rule> getRules(int ruleType) {
        return mBlockerManager.getRules(ruleType);
    }

    @Override
    public long saveRule(Rule rule) {
        return mBlockerManager.hasRule(rule) ? -1 : mBlockerManager.saveRule(rule);
    }

    @Override
    public void deleteRule(Rule rule) {
        mBlockerManager.deleteRule(rule);
    }

    @Override
    public long restoreRule(Rule rule) {
        return mBlockerManager.restoreRule(rule);
    }
}
