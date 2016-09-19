package com.haoutil.xposed.haoblocker.presenter;

import android.view.Menu;

public interface SMSPresenter {
    void setListItems();

    void setMenuItems(Menu menu);

    void deleteSMS(int position);

    void restoreSMS();

    void importSMSes();

    void exportSMSes();
}
