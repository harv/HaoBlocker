package com.haoutil.xposed.haoblocker.presenter;

import android.content.Context;
import android.os.Environment;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.RuleModel;
import com.haoutil.xposed.haoblocker.model.RuleModelImpl;
import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.ui.RuleView;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.ui.adapter.RuleAdapter;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RulePresenterImpl implements RulePresenter {
    private RuleModel mRuleModel;
    private RuleView mRuleView;

    private RuleAdapter adapter;

    private int positionDeleted = -1;
    private Rule ruleDeleted = null;

    public RulePresenterImpl(RuleView mRuleView) {
        this.mRuleView = mRuleView;
        mRuleModel = new RuleModelImpl();
    }

    @Override
    public void init() {
        Context context = mRuleView.getApplicationContext();
        mRuleModel.init(context);
    }

    @Override
    public void setListItems(int ruleType) {
        Context context = mRuleView.getApplicationContext();
        List<Rule> rules = mRuleModel.getRules(ruleType);
        BaseRecycleAdapter.OnItemClick onItemClick = mRuleView.getOnItemClick();
        adapter = new RuleAdapter(context, rules, onItemClick);
        mRuleView.setRuleAdapter(adapter);
    }

    @Override
    public void setMenuItems(Menu menu) {
        mRuleView.setMenuItems(menu);
    }

    @Override
    public void addRule() {
        mRuleView.addRule();
    }

    @Override
    public void addOrUpdateRuleSuccess(int position, Rule rule) {
        if (position == -1) {
            adapter.add(0, rule);
        } else {
            adapter.replace(position, rule);
        }

        mRuleView.showTip(R.string.rule_tip_rule_added, true);
    }

    @Override
    public void modifyRule(int position) {
        Rule rule = adapter.getItem(position);
        mRuleView.modifyRule(position, rule);
    }

    @Override
    public void deleteRuleConfirm(int position) {
        positionDeleted = position;
        mRuleView.showConfirm();
    }

    @Override
    public void deleteRule() {
        ruleDeleted = adapter.getItem(positionDeleted);
        mRuleModel.deleteRule(ruleDeleted);
        adapter.remove(positionDeleted);
        mRuleView.showTip(R.string.rule_tip_rule_deleted, true);
    }

    @Override
    public void deleteRuleCancel() {
        positionDeleted = -1;
    }

    @Override
    public void restoreRule() {
        if (-1 != positionDeleted && null != ruleDeleted) {
            long newId = mRuleModel.restoreRule(ruleDeleted);
            ruleDeleted.setId(newId);
            adapter.add(positionDeleted, ruleDeleted);

            positionDeleted = -1;
            ruleDeleted = null;
        }
    }

    @Override
    public void importRules() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "blocker_rule.csv");
            if (!file.exists() || !file.isFile()) {
                mRuleView.showTip(R.string.menu_import_rule_miss_tip, false);
                return;
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String content = columns[1];
                content = content.substring(1, content.length() - 1).replaceAll("\"\"", "\"");
                int type = Integer.valueOf(columns[2]);
                int sms = Integer.valueOf(columns[3]);
                int call = Integer.valueOf(columns[4]);
                int except = Integer.valueOf(columns[5]);
                long created = Long.valueOf(columns[6]);
                String remark = columns[7];
                remark = remark.substring(1, remark.length() - 1).replaceAll("\"\"", "\"");

                Rule rule = new Rule();
                rule.setContent(content);
                rule.setType(type);
                rule.setSms(sms);
                rule.setCall(call);
                rule.setException(except);
                rule.setCreated(created);
                rule.setRemark(remark);

                long id = mRuleModel.saveRule(rule);
                rule.setId(id);
                adapter.add(0, rule);
            }
            br.close();

            mRuleView.showTip(R.string.menu_import_rule_tip, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportRules() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "blocker_rule.csv");
            OutputStream os = new FileOutputStream(file);

            List<Rule> rules = mRuleModel.getRules(BlockerManager.TYPE_ALL);
            StringBuilder sb = new StringBuilder();
            for (int i = rules.size(); i > 0; i--) {
                Rule rule = rules.get(i - 1);
                sb.append(rule.getId());
                sb.append(",").append("\"").append(rule.getContent().replaceAll("\"", "\"\"")).append("\"");
                sb.append(",").append(rule.getType());
                sb.append(",").append(rule.getSms());
                sb.append(",").append(rule.getCall());
                sb.append(",").append(rule.getException());
                sb.append(",").append(rule.getCreated());
                sb.append(",").append("\"").append(rule.getRemark().replaceAll("\"", "\"\"")).append("\"");
                sb.append("\n");
            }
            byte[] bs = sb.toString().getBytes();
            os.write(bs, 0, bs.length);
            os.flush();
            os.close();

            mRuleView.showTip(R.string.menu_export_rule_tip, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleAddButton(boolean visible) {
        mRuleView.toggleAddButton(visible);
    }
}
