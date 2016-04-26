package com.haoutil.xposed.haoblocker.model;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.entity.Call;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.util.List;

public class CallModelImpl implements CallModel {
    private BlockerManager mBlockerManager;

    @Override
    public void init(Context context) {
        mBlockerManager = new BlockerManager(context);
        readAllCall();
    }

    @Override
    public void readAllCall() {
        mBlockerManager.readAllCall();
    }

    @Override
    public List<Call> getCalls(long id) {
        return mBlockerManager.getCalls(id);
    }

    @Override
    public long saveCall(Call call) {
        return mBlockerManager.saveCall(call);
    }

    @Override
    public void deleteCall(Call call) {
        mBlockerManager.deleteCall(call);
    }

    @Override
    public long restoreCall(Call call) {
        return mBlockerManager.restoreCall(call);
    }
}
