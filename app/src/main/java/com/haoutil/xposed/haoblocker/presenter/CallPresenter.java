package com.haoutil.xposed.haoblocker.presenter;

import android.view.Menu;

public interface CallPresenter {
    void init();

    void setListItems();

    void setMenuItems(Menu menu);

    void deleteCallConfirm(int position);

    void deleteCall();

    void deleteCallCancel();

    void restoreCall();

    void importCalls();

    void exportCalls();
}
