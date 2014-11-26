package com.haoutil.xposed.haoblocker.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallAdaptor extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater;
    private Handler handler;
    private List<Call> list;

    private List<Call> checkedCalls = new ArrayList<Call>();

    private SimpleDateFormat simpleDateFormat;

    public CallAdaptor(LayoutInflater inflater, Handler handler, List<Call> list) {
        this.inflater = inflater;
        this.handler = handler;
        this.list = list;

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
            view = inflater.inflate(R.layout.call_list_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.cb_item_check = (CheckBox) view.findViewById(R.id.cb_item_check);
            holder.tv_item_caller = (TextView) view.findViewById(R.id.tv_item_caller);
            holder.tv_item_date = (TextView) view.findViewById(R.id.tv_item_date);
        }

        Call item = list.get(position);
        if (item != null) {
            holder.cb_item_check.setChecked(item.isChecked());
            holder.cb_item_check.setOnClickListener(this);
            holder.cb_item_check.setTag(item);
            holder.tv_item_caller.setText(item.getCaller());
            holder.tv_item_date.setText(simpleDateFormat.format(new Date(item.getCreated())));
        }

        return view;
    }

    public List<Call> getCheckedCalls() {
        return checkedCalls;
    }

    public void clearChecked() {
        list.removeAll(checkedCalls);
        checkedCalls.clear();

        handler.sendEmptyMessage(0);
        handler.sendEmptyMessage(2);
    }

    public void checkAll(boolean checked) {
        for (Call item : list) {
            item.setChecked(checked);
            if (checked) {
                if (!checkedCalls.contains(item)) {
                    checkedCalls.add(item);
                }
            } else {
                if (checkedCalls.contains(item)) {
                    checkedCalls.remove(item);
                }
            }
        }
        if (checked) {
            handler.sendEmptyMessage(1);
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onClick(View v) {
        int totalSize = getCount();
        int beforeSize = checkedCalls.size();

        Call item = (Call) v.getTag();
        boolean b = ((CheckBox) v).isChecked();
        if (b) {
            if (!checkedCalls.contains(item)) {
                checkedCalls.add(item);
            }
        } else {
            if (checkedCalls.contains(item)) {
                checkedCalls.remove(item);
            }
        }
        int afterSize = checkedCalls.size();

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
        private TextView tv_item_caller;
        private TextView tv_item_date;
    }
}
