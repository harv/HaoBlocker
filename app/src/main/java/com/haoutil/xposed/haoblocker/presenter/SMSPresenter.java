package com.haoutil.xposed.haoblocker.presenter;

import android.view.Menu;

public interface SMSPresenter {
    void init();

    void setListItems();

    void setMenuItems(Menu menu);

    void deleteSMSConfirm(int position);

    void deleteSMS();

    void deleteSMSCancel();

    void restoreSMS();

    void importSMSes();

    void exportSMSes();
}
