package com.haoutil.xposed.haoblocker.model.impl;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.RuleAddModel;
import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

public class RuleAddModelImpl implements RuleAddModel {
    private BlockerManager mBlockerManager;

    @Override
    public void init(Context context) {
        mBlockerManager = new BlockerManager(context);
    }

    @Override
    public long saveRule(Rule rule) {
        return mBlockerManager.saveRule(rule);
    }

    @Override
    public void updateRule(Rule rule) {
        mBlockerManager.updateRule(rule);
    }
}
