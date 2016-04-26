package com.haoutil.xposed.haoblocker.presenter;

import com.haoutil.xposed.haoblocker.model.entity.Rule;

public interface RuleAddPresenter {
    void init();

    void initView(String operation, int position, Rule rule);

    void checkRuleType(int RuleType);

    void checkBlockType(int blockType, int ruleType);

    void checkFailed();

    void saveRule(String content, int type, int sms, int call, int except, String remark);

    void enableBlockSpinner(boolean enabled);
}
