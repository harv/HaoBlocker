package com.haoutil.xposed.haoblocker.presenter;

import android.view.Menu;

public interface CallPresenter {
    void init();

    void setListItems();

    void setMenuItems(Menu menu);

    void deleteCall(int position);

    void restoreCall();

    void importCalls();

    void exportCalls();
}
