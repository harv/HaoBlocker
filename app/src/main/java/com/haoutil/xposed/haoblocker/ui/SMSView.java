package com.haoutil.xposed.haoblocker.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;

public interface SMSView extends BaseView, PromptView {
    Context getApplicationContext();

    void setSMSAdapter(RecyclerView.Adapter adapter);

    void setMenuItems(Menu menu);

    BaseRecycleAdapter.OnItemClick getOnItemClick();
}
