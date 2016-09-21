package com.haoutil.xposed.haoblocker.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.entity.Rule;

import java.util.List;

public class RuleAdapter extends BaseRecycleAdapter<Rule> {
    public RuleAdapter(Context context, List<Rule> data, OnItemClick onItemClick) {
        super(context, data, onItemClick);
    }

    @Override
    public int getItemResource() {
        return R.layout.item_rule;
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        Rule rule = getItem(position);
        if (rule != null) {
            ImageView iv_block_type = holder.getView(R.id.iv_block_type);
            if (rule.getException() == 1) {
                iv_block_type.setImageResource(R.drawable.ic_except);
            } else if (rule.getSms() == 1 && rule.getCall() == 1) {
                iv_block_type.setImageResource(R.drawable.ic_block_both);
            } else if (rule.getSms() == 1) {
                iv_block_type.setImageResource(R.drawable.ic_block_sms);
            } else if (rule.getCall() == 1) {
                iv_block_type.setImageResource(R.drawable.ic_block_call);
            }
            TextView tv_rule = holder.getView(R.id.tv_rule);
            tv_rule.setText(rule.getContent());
            TextView tv_remark = holder.getView(R.id.tv_remark);
            tv_remark.setText(rule.getRemark());
        }
    }
}
