package com.haoutil.xposed.haoblocker.model;

import com.haoutil.xposed.haoblocker.model.entity.SMS;

import java.util.List;

public interface SMSModel {
    void readAllSMS();

    List<SMS> getSMSes(long id);

    long saveSMS(SMS sms);

    void deleteSMS(SMS sms);

    long restoreSMS(SMS sms);
}
