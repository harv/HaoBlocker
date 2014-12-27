package com.haoutil.xposed.haoblocker.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.event.RuleUpdateEvent;
import com.haoutil.xposed.haoblocker.model.Rule;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class RuleAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Rule> list;

    private RuleUpdateEvent[] events;

    private List<Rule> checkedRules = new ArrayList<>();

    public RuleAdapter(LayoutInflater inflater, List<Rule> list) {
        this.inflater = inflater;
        this.list = list;

        this.events = new RuleUpdateEvent[4];
        for (int i = 0; i < this.events.length; i++) {
            this.events[i] = new RuleUpdateEvent(i);
        }
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
            holder = new ItemViewHolder(view);
        }

        Rule item = list.get(position);
        if (item != null) {
            holder.cb_item_check.setChecked(item.isChecked());
            holder.cb_item_check.setTag(item);
            holder.tv_item_rule.setText(item.getContent());
            String blockItems = "";
            if (item.getException() == 1) {
                holder.tv_item_block.setTextColor(view.getResources().getColor(R.color.textColorGreen));
                blockItems = "Exception";
            } else {
                holder.tv_item_block.setTextColor(view.getResources().getColor(R.color.textColorRed));
                if (item.getSms() == 1) {
                    blockItems = "SMS";
                }
                if (item.getCall() == 1) {
                    if (!TextUtils.isEmpty(blockItems)) {
                        blockItems += ", ";
                    }
                    blockItems += "Call";
                }
            }
            holder.tv_item_block.setText(blockItems);
        }

        return view;
    }

    public void addItem(Rule rule, int position) {
        if (position == -1) {   // add
            list.add(0, rule);
        } else {    // modify
            list.set(position, rule);
        }
    }

    public void clearChecked() {
        list.removeAll(checkedRules);
        checkedRules.clear();

        EventBus.getDefault().post(events[0]);
        EventBus.getDefault().post(events[2]);
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
            EventBus.getDefault().post(events[1]);
        } else {
            EventBus.getDefault().post(events[0]);
        }
    }

    public List<Rule> getCheckedRules() {
        return checkedRules;
    }


    class ItemViewHolder {
        @InjectView(R.id.cb_item_check)
        CheckBox cb_item_check;
        @InjectView(R.id.tv_item_rule)
        TextView tv_item_rule;
        @InjectView(R.id.tv_item_block)
        TextView tv_item_block;

        public ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @OnClick(R.id.cb_item_check)
        public void onClick(View v) {
            int totalSize = getCount();
            int beforeSize = checkedRules.size();

            Rule item = (Rule) v.getTag();
            boolean b = ((CheckBox) v).isChecked();
            item.setChecked(b);
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
                EventBus.getDefault().post(events[0]);
            }
            if (beforeSize == 0 && afterSize == 1) {    // 0 --> 1
                EventBus.getDefault().post(events[1]);
            }
            if (beforeSize == totalSize && afterSize == totalSize - 1) {    // MAX --> MAX - 1
                EventBus.getDefault().post(events[2]);
            }
            if (beforeSize == totalSize - 1 && afterSize == totalSize) {    // MAX - 1 --> MAX
                EventBus.getDefault().post(events[3]);
            }
        }
    }
}
