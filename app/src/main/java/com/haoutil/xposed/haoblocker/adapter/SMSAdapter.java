package com.haoutil.xposed.haoblocker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.event.SMSUpdateEvent;
import com.haoutil.xposed.haoblocker.model.SMS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class SMSAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<SMS> list;

    private SMSUpdateEvent[] events;

    private List<SMS> checkedSMSes = new ArrayList<SMS>();

    private SimpleDateFormat simpleDateFormat;

    public SMSAdapter(LayoutInflater inflater, List<SMS> list) {
        this.inflater = inflater;
        this.list = list;

        this.events = new SMSUpdateEvent[4];
        for (int i = 0; i < this.events.length; i++) {
            this.events[i] = new SMSUpdateEvent(i);
        }

        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
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
    public long getItemId(int i) {
        return -1;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View view;
        if (contentView != null) {
            view = contentView;
        } else {
            view = inflater.inflate(R.layout.sms_list_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder(view);
        }

        SMS item = list.get(position);
        if (item != null) {
            holder.cb_item_check.setChecked(item.isChecked());
            holder.cb_item_check.setTag(item);
            holder.tv_item_sender.setText(item.getSender());
            holder.tv_item_date.setText(simpleDateFormat.format(new Date(item.getCreated())));
            if (item.getRead() != 1) {
                int colorPrimary = view.getResources().getColor(R.color.colorPrimary);
                holder.tv_item_sender.setTextColor(colorPrimary);
                holder.tv_item_date.setTextColor(colorPrimary);
            } else {
                int textColorSecondaryDark = view.getResources().getColor(R.color.textColorSecondaryDark);
                holder.tv_item_sender.setTextColor(textColorSecondaryDark);
                holder.tv_item_date.setTextColor(textColorSecondaryDark);
            }
        }

        return view;
    }

    public void addItem(SMS sms) {
        list.add(0, sms);
    }

    public List<SMS> getCheckedSMSes() {
        return checkedSMSes;
    }

    public void clearChecked() {
        list.removeAll(checkedSMSes);
        checkedSMSes.clear();

        EventBus.getDefault().post(events[0]);
        EventBus.getDefault().post(events[2]);
    }

    public void checkAll(boolean checked) {
        for (SMS item : list) {
            item.setChecked(checked);
            if (checked) {
                if (!checkedSMSes.contains(item)) {
                    checkedSMSes.add(item);
                }
            } else {
                if (checkedSMSes.contains(item)) {
                    checkedSMSes.remove(item);
                }
            }
        }
        if (checked) {
            EventBus.getDefault().post(events[1]);
        } else {
            EventBus.getDefault().post(events[0]);
        }
    }

    class ItemViewHolder {
        @InjectView(R.id.cb_item_check)
        CheckBox cb_item_check;
        @InjectView(R.id.tv_item_sender)
        TextView tv_item_sender;
        @InjectView(R.id.tv_item_date)
        TextView tv_item_date;

        public ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @OnClick(R.id.cb_item_check)
        public void onClick(View v) {
            int totalSize = getCount();
            int beforeSize = checkedSMSes.size();

            SMS item = (SMS) v.getTag();
            boolean b = ((CheckBox) v).isChecked();
            item.setChecked(b);
            if (b) {
                if (!checkedSMSes.contains(item)) {
                    checkedSMSes.add(item);
                }
            } else {
                if (checkedSMSes.contains(item)) {
                    checkedSMSes.remove(item);
                }
            }
            int afterSize = checkedSMSes.size();

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
