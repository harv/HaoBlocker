package com.haoutil.xposed.haoblocker.ui.activity;

import android.content.Context;
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

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.presenter.RuleAddPresenter;
import com.haoutil.xposed.haoblocker.presenter.impl.RuleAddPresenterImpl;
import com.haoutil.xposed.haoblocker.ui.RuleAddView;

public class RuleActivity extends BaseActivity implements RuleAddView, CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private RuleAddPresenter mRuleAddPresenter;

    private LinearLayout ll_container;
    private EditText et_rule;
    private Spinner sp_type;
    private CheckBox cb_except;
    private Spinner sp_block;
    private EditText et_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackable();
        mRuleAddPresenter = new RuleAddPresenterImpl(this);
        mRuleAddPresenter.init();

        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        et_rule = (EditText) findViewById(R.id.et_rule);
        sp_type = (Spinner) findViewById(R.id.sp_type);
        sp_type.setOnItemSelectedListener(this);
        cb_except = (CheckBox) findViewById(R.id.cb_except);
        cb_except.setOnCheckedChangeListener(this);
        sp_block = (Spinner) findViewById(R.id.sp_block);
        sp_block.setOnItemSelectedListener(this);
        et_remark = (EditText) findViewById(R.id.et_remark);
        Button btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String operation = bundle.getString("operation");
        int position = bundle.getInt("position");
        Rule rule = (Rule) bundle.get("rule");

        mRuleAddPresenter.initView(operation, position, rule);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_type:
                mRuleAddPresenter.checkRuleType(position);
                break;
            case R.id.sp_block:
                mRuleAddPresenter.checkBlockType(position, sp_type.getSelectedItemPosition());
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
                    mRuleAddPresenter.checkFailed();
                } else {
                    mRuleAddPresenter.saveRule(
                            et_rule.getText().toString().trim(),
                            sp_type.getSelectedItemPosition(),
                            sp_block.isEnabled() && sp_block.getSelectedItemPosition() != Rule.BLOCK_CALL ? 1 : 0,
                            sp_block.isEnabled() && sp_block.getSelectedItemPosition() != Rule.BLOCK_SMS ? 1 : 0,
                            cb_except.isChecked() ? 1 : 0,
                            et_remark.getText().toString().trim()
                    );
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_except:
                mRuleAddPresenter.enableBlockSpinner(!b);
                break;
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_rule;
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initView(Rule rule) {
        et_rule.setText(rule.getContent());
        sp_type.setSelection(rule.getType());
        cb_except.setChecked(rule.getException() == 1);
        sp_block.setEnabled(rule.getException() != 1);
        // sms call block(@see com.haoutil.xposed.haoblocker.model.entity.Rule)
        //  1    1  0(both)
        //  1    0  1(sms)
        //  0    1  2(call)
        sp_block.setSelection(rule.getSms() == 1 ? rule.getCall() == 1 ? 0 : 1 : 2);
        et_remark.setText(rule.getRemark());
    }

    @Override
    public void selectSMSBlock() {
        sp_block.setSelection(Rule.BLOCK_SMS);
    }

    @Override
    public void focusRuleInput() {
        et_rule.requestFocus();
    }

    @Override
    public void setReturnValue(Rule rule, int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("rule", rule);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void enableBlockSpinner(boolean enabled) {
        sp_block.setEnabled(enabled);
    }

    @Override
    public void showTip(int resId) {
        Snackbar.make(ll_container, resId, Snackbar.LENGTH_LONG).show();
    }
}
