package com.haoutil.xposed.haoblocker.model.impl;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.SMSModel;
import com.haoutil.xposed.haoblocker.model.entity.SMS;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.util.List;

public class SMSModelImpl implements SMSModel {
    private BlockerManager mBlockerManager;

    @Override
    public void init(Context context) {
        mBlockerManager = new BlockerManager(context);
        readAllSMS();
    }

    @Override
    public void readAllSMS() {
        mBlockerManager.readAllSMS();
    }

    @Override
    public List<SMS> getSMSes(long id) {
        return mBlockerManager.getSMSes(id);
    }

    @Override
    public long saveSMS(SMS sms) {
        return mBlockerManager.hasSMS(sms) ? -1 : mBlockerManager.saveSMS(sms);
    }

    @Override
    public void deleteSMS(SMS sms) {
        mBlockerManager.deleteSMS(sms);
    }

    @Override
    public long restoreSMS(SMS sms) {
        return mBlockerManager.restoreSMS(sms);
    }
}
