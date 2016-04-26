package com.haoutil.xposed.haoblocker.presenter;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.GeneralModel;
import com.haoutil.xposed.haoblocker.model.GeneralModelImpl;
import com.haoutil.xposed.haoblocker.ui.GeneralView;

public class GeneralPresenterImpl implements GeneralPresenter {
    private GeneralModel mGeneralModel;
    private GeneralView mGeneralView;

    public GeneralPresenterImpl(GeneralView mGeneralView) {
        this.mGeneralView = mGeneralView;
        mGeneralModel = new GeneralModelImpl();
    }

    @Override
    public void init() {
        Context context = mGeneralView.getApplicationContext();
        mGeneralModel.init(context);
    }

    @Override
    public void initView() {
        mGeneralView.setOnCheckedChangeListener();
        mGeneralView.setAbout();
        mGeneralView.checkSMS( mGeneralModel.isEnableSMS());
        mGeneralView.checkCall(mGeneralModel.isEnableCall());
        mGeneralView.checkNotification(mGeneralModel.isShowBlockNotification());
        mGeneralView.check(mGeneralModel.isEnable());
    }

    @Override
    public void enable(boolean enabled) {
        mGeneralModel.enable(enabled);
        mGeneralView.enable(enabled);
    }

    @Override
    public void enableSMS(boolean enabled) {
        mGeneralModel.enableSMS(enabled);
    }

    @Override
    public void enableCall(boolean enabled) {
        mGeneralModel.enableCall(enabled);
    }

    @Override
    public void enableNotification(boolean enabled) {
        mGeneralModel.enableNotification(enabled);
    }
}
