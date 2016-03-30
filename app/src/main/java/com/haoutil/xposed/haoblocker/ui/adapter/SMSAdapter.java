package com.haoutil.xposed.haoblocker.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.entity.SMS;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SMSAdapter extends BaseRecycleAdapter<SMS> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public SMSAdapter(Context context, List<SMS> data, OnItemClick onItemClick) {
        super(context, data, onItemClick);
    }

    @Override
    public int getItemResource() {
        return R.layout.item_sms;
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        SMS sms = getItem(position);
        if (sms != null) {
            TextView tv_sender = holder.getView(R.id.tv_sender);
            tv_sender.setText(sms.getSender());
            TextView tv_date = holder.getView(R.id.tv_date);
            tv_date.setText(simpleDateFormat.format(new Date(sms.getCreated())));
            TextView tv_content = holder.getView(R.id.tv_content);
            tv_content.setText(sms.getContent());
        }
    }
}
