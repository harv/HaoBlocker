package com.haoutil.xposed.haoblocker.ui;

import android.support.v7.widget.RecyclerView;
import android.view.Menu;

public interface CallView extends PromptView {
    void setCallAdapter(RecyclerView.Adapter adapter);

    void setMenuItems(Menu menu);
}
