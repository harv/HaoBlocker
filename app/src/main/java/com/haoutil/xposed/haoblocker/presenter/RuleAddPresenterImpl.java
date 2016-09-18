package com.haoutil.xposed.haoblocker.presenter;

import android.content.Context;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.RuleAddModel;
import com.haoutil.xposed.haoblocker.model.RuleAddModelImpl;
import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.ui.RuleAddView;

import java.util.Date;

public class RuleAddPresenterImpl implements RuleAddPresenter {
    private RuleAddView mRuleAddView;
    private RuleAddModel mRuleAddModel;

    private long mId = -1;
    private long mCreated;

    private int mPosition = -1;

    public RuleAddPresenterImpl(RuleAddView mRuleAddView) {
        this.mRuleAddView = mRuleAddView;
        mRuleAddModel = new RuleAddModelImpl();
    }

    @Override
    public void init() {
        Context context = mRuleAddView.getAppContext();
        mRuleAddModel.init(context);
    }

    @Override
    public void initView(String operation, int position, Rule rule) {
        if ("add".equalsIgnoreCase(operation)) {
        } else if ("modify".equalsIgnoreCase(operation)) {
            if (rule != null) {
                this.mPosition = position;
                mId = rule.getId();
                mCreated = rule.getCreated();
                mRuleAddView.initView(rule);
            }
        }
    }

    @Override
    public void checkRuleType(int RuleType) {
        if (RuleType == Rule.TYPE_KEYWORD) {
            mRuleAddView.selectSMSBlock();
        }
    }

    @Override
    public void checkBlockType(int blockType, int ruleType) {
        if (blockType != Rule.BLOCK_SMS && ruleType == Rule.TYPE_KEYWORD) {
            mRuleAddView.selectSMSBlock();
            mRuleAddView.showTip(R.string.rule_not_match_type);
        }
    }

    @Override
    public void checkFailed() {
        mRuleAddView.focusRuleInput();
        mRuleAddView.showTip(R.string.rule_tip_empty_rule);
    }

    @Override
    public void saveRule(String content, int type, int sms, int call, int except, String remark) {
        Rule rule = new Rule();
        boolean isModify = mId != -1;
        if (isModify) {
            rule.setId(mId);
            rule.setCreated(mCreated);
        } else {
            rule.setCreated(new Date().getTime());
        }
        rule.setContent(content);
        rule.setType(type);
        rule.setSms(sms);
        rule.setCall(call);
        rule.setException(except);
        rule.setRemark(remark);

        if (isModify) {
            mRuleAddModel.updateRule(rule);
        } else {
            long newId = mRuleAddModel.saveRule(rule);
            rule.setId(newId);
        }

        mRuleAddView.setReturnValue(rule, mPosition);
    }

    @Override
    public void enableBlockSpinner(boolean enabled) {
        mRuleAddView.enableBlockSpinner(enabled);
    }
}
