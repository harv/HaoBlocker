package com.haoutil.xposed.haoblocker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

public class RuleActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private BlockerManager blockerManager;

    private LinearLayout ll_container;
    private TextView tv_id;
    private EditText et_rule;
    private Spinner sp_type;
    private CheckBox cb_except;
    private Spinner sp_block;

    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableBackable();

        blockerManager = new BlockerManager(this);

        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_rule = (EditText) findViewById(R.id.et_rule);
        sp_type = (Spinner) findViewById(R.id.sp_type);
        sp_type.setOnItemSelectedListener(this);
        cb_except = (CheckBox) findViewById(R.id.cb_except);
        cb_except.setOnCheckedChangeListener(this);
        sp_block = (Spinner) findViewById(R.id.sp_block);
        sp_block.setOnItemSelectedListener(this);
        Button btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String operation = bundle.getString("operation");
        if ("add".equalsIgnoreCase(operation)) {
        } else if ("modify".equalsIgnoreCase(operation)) {
            position = bundle.getInt("position");

            Rule rule = (Rule) bundle.get("rule");
            if (rule != null) {
                tv_id.setText(String.valueOf(rule.getId()));
                et_rule.setText(rule.getContent());
                sp_type.setSelection(rule.getType());
                cb_except.setChecked(rule.getException() == 1);
                sp_block.setEnabled(rule.getException() != 1);
                // sms call block(@see com.haoutil.xposed.haoblocker.model.Rule)
                //  1    1  0(both)
                //  1    0  1(sms)
                //  0    1  2(call)
                sp_block.setSelection(rule.getSms() == 1 ? rule.getCall() == 1 ? 0 : 1 : 2);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_type:
                if (position == Rule.TYPE_KEYWORD) {
                    sp_block.setSelection(Rule.BLOCK_SMS);   // only sms can block
                }
                break;
            case R.id.sp_block:
                if (position != Rule.BLOCK_SMS && sp_type.getSelectedItemPosition() == Rule.TYPE_KEYWORD) {
                    sp_block.setSelection(Rule.BLOCK_SMS);
                    Snackbar.make(ll_container, R.string.rule_not_match_type, Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                if (TextUtils.isEmpty(et_rule.getText())) {
                    et_rule.requestFocus();
                    Snackbar.make(ll_container, R.string.rule_tip_empty_rule, Snackbar.LENGTH_LONG).show();
                } else {
                    Rule rule = new Rule();
                    boolean isModify = !TextUtils.isEmpty(tv_id.getText());
                    if (isModify) {
                        rule.setId(Long.valueOf(tv_id.getText().toString().trim()));
                    }
                    rule.setContent(et_rule.getText().toString().trim());
                    rule.setType(sp_type.getSelectedItemPosition());
                    rule.setSms(sp_block.isEnabled() && sp_block.getSelectedItemPosition() != Rule.BLOCK_CALL ? 1 : 0);
                    rule.setCall(sp_block.isEnabled() && sp_block.getSelectedItemPosition() != Rule.BLOCK_SMS ? 1 : 0);
                    rule.setException(cb_except.isChecked() ? 1 : 0);

                    if (isModify) {
                        blockerManager.updateRule(rule);
                    } else {
                        long id = blockerManager.saveRule(rule);
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
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_except:
                sp_block.setEnabled(!b);
                break;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_rule;
    }
}
