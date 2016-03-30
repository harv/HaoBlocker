package com.haoutil.xposed.haoblocker.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.entity.Call;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallAdapter extends BaseRecycleAdapter<Call> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public CallAdapter(Context context, List<Call> data, OnItemClick onItemClick) {
        super(context, data, onItemClick);
    }

    @Override
    public int getItemResource() {
        return R.layout.item_call;
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        Call call = getItem(position);
        if (call != null) {
            TextView tv_caller = holder.getView(R.id.tv_caller);
            tv_caller.setText(call.getCaller());
            TextView tv_date = holder.getView(R.id.tv_date);
            tv_date.setText(simpleDateFormat.format(new Date(call.getCreated())));
        }
    }
}
