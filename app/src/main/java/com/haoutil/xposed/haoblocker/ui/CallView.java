package com.haoutil.xposed.haoblocker.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;

public interface CallView {
    Context getApplicationContext();

    void setCallAdapter(RecyclerView.Adapter adapter);

    void setMenuItems(Menu menu);

    BaseRecycleAdapter.OnItemClick getOnItemClick();

    void showTip(int resId);

    void showTipInThread(int resId);

    void confirm();
}
