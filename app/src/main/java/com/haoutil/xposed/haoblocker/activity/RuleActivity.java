package com.haoutil.xposed.haoblocker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.util.DbManager;

public class RuleActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private DbManager dbManager;

    private int ruleType = Rule.TYPE_STRING;

    private TextView tv_id;
    private EditText et_rule;
    private CheckBox cb_sms;
    private CheckBox cb_call;
    private CheckBox cb_exception;

    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DbManager(this);

        RadioGroup rg_group = (RadioGroup) findViewById(R.id.rg_group);
        rg_group.setOnCheckedChangeListener(this);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_rule = (EditText) findViewById(R.id.et_rule);
        cb_sms = (CheckBox) findViewById(R.id.cb_sms);
        cb_call = (CheckBox) findViewById(R.id.cb_call);
        cb_exception = (CheckBox) findViewById(R.id.cb_exception);
        cb_exception.setOnCheckedChangeListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String operation = bundle.getString("operation");
        if (operation.equalsIgnoreCase("add")) {
        } else if (operation.equalsIgnoreCase("modify")) {
            position = bundle.getInt("position");

            Rule rule = (Rule) bundle.get("rule");
            tv_id.setText(String.valueOf(rule.getId()));
            et_rule.setText(rule.getContent());
            cb_sms.setChecked(rule.getSms() == 1);
            cb_call.setChecked(rule.getCall() == 1);
            cb_exception.setChecked(rule.getException() == 1);
            switch (rule.getType()) {
                case Rule.TYPE_STRING:
                    ((RadioButton) findViewById(R.id.rb_string)).setChecked(true);
                    ruleType = Rule.TYPE_STRING;
                    break;
                case Rule.TYPE_WILDCARD:
                    ((RadioButton) findViewById(R.id.rb_wildcard)).setChecked(true);
                    ruleType = Rule.TYPE_WILDCARD;
                    break;
                case Rule.TYPE_KEYWORD:
                    ((RadioButton) findViewById(R.id.rb_keyword)).setChecked(true);
                    ruleType = Rule.TYPE_KEYWORD;
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_string:
                ruleType = Rule.TYPE_STRING;
                cb_call.setEnabled(true);
                break;
            case R.id.rb_wildcard:
                ruleType = Rule.TYPE_WILDCARD;
                cb_call.setEnabled(true);
                break;
            case R.id.rb_keyword:
                ruleType = Rule.TYPE_KEYWORD;
                cb_call.setEnabled(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            cb_sms.setEnabled(false);
            cb_call.setEnabled(false);
        } else {
            cb_sms.setEnabled(true);
            cb_call.setEnabled(true);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_rule;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_actions, menu);

        menu.findItem(R.id.action_accept).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_accept:
                if (TextUtils.isEmpty(et_rule.getText())) {
                    et_rule.requestFocus();
                    Toast.makeText(this, getResources().getString(R.string.rule_tip_empty_rule), Toast.LENGTH_SHORT).show();
                } else if (!(cb_exception.isChecked() || cb_sms.isChecked() || cb_call.isChecked())) {
                    Toast.makeText(this, getResources().getString(R.string.rule_tip_empty_block), Toast.LENGTH_SHORT).show();
                } else {
                    Rule rule = new Rule();
                    boolean isModify = !TextUtils.isEmpty(tv_id.getText());
                    if (isModify) {
                        rule.setId(Long.valueOf(tv_id.getText().toString().trim()));
                    }
                    rule.setContent(et_rule.getText().toString().trim());
                    rule.setType(ruleType);
                    rule.setSms(cb_sms.isEnabled() && cb_sms.isChecked() ? 1 : 0);
                    rule.setCall((cb_call.isEnabled() && cb_call.isChecked()) ? 1 : 0);
                    rule.setException(cb_exception.isChecked() ? 1 : 0);

                    if (isModify) {
                        dbManager.updateRule(rule);
                    } else {
                        long id = dbManager.saveRule(rule);
                        rule.setId(id);
                    }

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putSerializable("rule", rule);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);

                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
