package com.haoutil.xposed.haoblocker.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.activity.RuleActivity;
import com.haoutil.xposed.haoblocker.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.adapter.RuleAdapter;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.util.List;

public class RuleFragment extends BaseFragment implements BaseRecycleAdapter.OnItemClick, View.OnClickListener, DialogInterface.OnClickListener, SettingsActivity.OnMenuItemClickListener {
    private SettingsActivity activity;
    private BlockerManager blockerManager;

    private RecyclerView rv_rule;
    private RuleAdapter adapter;

    private int positionDeleted = -1;
    private Rule ruleDeleted = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
        blockerManager = new BlockerManager(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            rv_rule = (RecyclerView) view.findViewById(R.id.rv_rule);
            rv_rule.setLayoutManager(new LinearLayoutManager(activity));

            List<Rule> rules = blockerManager.getRules(BlockerManager.TYPE_ALL);
            adapter = new RuleAdapter(activity, rules, RuleFragment.this);
            rv_rule.setAdapter(adapter);
        }
        return view;
    }

    // click on list item
    @Override
    public void onClick(int position) {
        Intent intent = new Intent(getActivity(), RuleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("operation", "modify");
        bundle.putInt("position", position);
        Rule rule = adapter.getItem(position);
        bundle.putSerializable("rule", rule);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }

    // long click on list item
    @Override
    public void onLongClick(int position) {
        positionDeleted = position;
        confirm(this, this);
    }

    // click on Snackbar button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                Intent intent = new Intent(getActivity(), RuleActivity.class);
                intent.putExtra("operation", "add");
                startActivityForResult(intent, 0);
                break;
            default:
                if (-1 != positionDeleted && null != ruleDeleted) {
                    long newId = blockerManager.restoreRule(ruleDeleted);
                    ruleDeleted.setId(newId);
                    adapter.add(positionDeleted, ruleDeleted);

                    positionDeleted = -1;
                    ruleDeleted = null;
                }
                break;
        }
    }

    // click on dialog button
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                ruleDeleted = adapter.getItem(positionDeleted);
                blockerManager.deleteRule(ruleDeleted);
                adapter.remove(positionDeleted);
                activity.showTip(R.string.rule_tip_rule_deleted, RuleFragment.this);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                positionDeleted = -1;
                break;
        }
    }

    // click on menu item
    @Override
    public void onFilter(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_all:
                adapter.replaceAll(blockerManager.getRules(BlockerManager.TYPE_ALL));
                break;
            case R.id.filter_call:
                adapter.replaceAll(blockerManager.getRules(BlockerManager.TYPE_CALL));
                break;
            case R.id.filter_sms:
                adapter.replaceAll(blockerManager.getRules(BlockerManager.TYPE_SMS));
                break;
            case R.id.filter_except:
                adapter.replaceAll(blockerManager.getRules(BlockerManager.TYPE_EXCEPT));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Rule rule = (Rule) bundle.get("rule");
            int position = bundle.getInt("position");
            if (position == -1) {
                adapter.add(0, rule);
            } else {
                adapter.replace(position, rule);
            }

            activity.showTip(R.string.rule_tip_rule_added, null);
        }
    }

    // toggle FloatingActionButton
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (activity != null) {
            activity.setOnAddListener(isVisibleToUser ? this : null);
            activity.setOnFilterListener(isVisibleToUser ? this : null);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_rule;
    }
}
