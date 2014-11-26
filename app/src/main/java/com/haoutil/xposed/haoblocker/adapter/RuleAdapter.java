package com.haoutil.xposed.haoblocker.adapter;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater;
    private Handler handler;
    private List<Rule> list;

    private List<Rule> checkedRules = new ArrayList<Rule>();

    public RuleAdapter(LayoutInflater inflater, Handler handler, List<Rule> list) {
        this.inflater = inflater;
        this.handler = handler;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View view;
        if (contentView != null) {
            view = contentView;
        } else {
            view = inflater.inflate(R.layout.rule_list_item, parent, false);
        }

        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.cb_item_check = (CheckBox) view.findViewById(R.id.cb_item_check);
            holder.tv_item_rule = (TextView) view.findViewById(R.id.tv_item_rule);
            holder.tv_item_block = (TextView) view.findViewById(R.id.tv_item_block);
        }

        Rule item = list.get(position);
        if (item != null) {
            holder.cb_item_check.setChecked(item.isChecked());
            holder.cb_item_check.setOnClickListener(this);
            holder.cb_item_check.setTag(item);
            holder.tv_item_rule.setText(item.getContent());
            String blockItems = "";
            if (item.getSms() == 1) {
                blockItems = "SMS";
            }
            if (item.getCall() == 1) {
                if (!TextUtils.isEmpty(blockItems)) {
                    blockItems += ", ";
                }
                blockItems += "Call";
            }
            holder.tv_item_block.setText(blockItems);
        }

        return view;
    }

    public void addItem(Rule rule, int position) {
        if (position == -1) {   // add
            list.add(rule);
        } else {    // modify
            list.set(position, rule);
        }
    }

    public void clearChecked() {
        list.removeAll(checkedRules);
        checkedRules.clear();

        handler.sendEmptyMessage(0);
        handler.sendEmptyMessage(2);
    }

    public void checkAll(boolean checked) {
        for (Rule item : list) {
            item.setChecked(checked);
            if (checked) {
                if (!checkedRules.contains(item)) {
                    checkedRules.add(item);
                }
            } else {
                if (checkedRules.contains(item)) {
                    checkedRules.remove(item);
                }
            }
        }
        if (checked) {
            handler.sendEmptyMessage(1);
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    public List<Rule> getCheckedRules() {
        return checkedRules;
    }

    @Override
    public void onClick(View v) {
        int totalSize = getCount();
        int beforeSize = checkedRules.size();

        Rule item = (Rule) v.getTag();
        boolean b = ((CheckBox) v).isChecked();
        if (b) {
            if (!checkedRules.contains(item)) {
                checkedRules.add(item);
            }
        } else {
            if (checkedRules.contains(item)) {
                checkedRules.remove(item);
            }
        }
        int afterSize = checkedRules.size();

        if (beforeSize == 1 && afterSize == 0) {    // 1 --> 0
            handler.sendEmptyMessage(0);
        }
        if (beforeSize == 0 && afterSize == 1) {    // 0 --> 1
            handler.sendEmptyMessage(1);
        }
        if (beforeSize == totalSize && afterSize == totalSize - 1) {    // MAX --> MAX - 1
            handler.sendEmptyMessage(2);
        }
        if (beforeSize == totalSize - 1 && afterSize == totalSize) { // MAX - 1 --> MAX
            handler.sendEmptyMessage(3);
        }
    }

    private class ItemViewHolder {
        private CheckBox cb_item_check;
        private TextView tv_item_rule;
        private TextView tv_item_block;
    }
}
