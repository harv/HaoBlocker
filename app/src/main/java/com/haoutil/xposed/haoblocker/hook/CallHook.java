package com.haoutil.xposed.haoblocker.hook;

import android.content.Context;
import android.content.Intent;

import com.haoutil.xposed.haoblocker.XposedMod;
import com.haoutil.xposed.haoblocker.model.Call;
import com.haoutil.xposed.haoblocker.util.DbManager;
import com.haoutil.xposed.haoblocker.util.Logger;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

import java.util.Date;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CallHook implements BaseHook {
    private SettingsHelper settingsHelper;
    private DbManager dbManager;

    private Context mContext;

    private XC_LoadPackage.LoadPackageParam loadPackageParam;

    public CallHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;

        settingsHelper = new SettingsHelper();
    }

    @Override
    public void exec() {
        Logger.log("Hook com.android.phone.CallNotifier...");
        Class<?> clazz = XposedHelpers.findClass("com.android.phone.CallNotifier", loadPackageParam.classLoader);

        XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                mContext = (Context) XposedHelpers.getObjectField(param.args[2], "mContext");
                dbManager = new DbManager(mContext);
            }
        });

        XposedBridge.hookAllMethods(clazz, "onNewRingingConnection", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!settingsHelper.isEnable() || !settingsHelper.isEnableCall()) {
                    return;
                }

                try {
                    Object connection = XposedHelpers.getObjectField(param.args[0], "result");
                    Object call = XposedHelpers.callMethod(connection, "getCall");
                    String caller = (String) XposedHelpers.callMethod(connection, "getAddress");

                    Logger.log("Incoming call: " + caller);

                    if (dbManager.blockCall(caller)) {
                        Call savedCall = new Call();
                        savedCall.setCaller(caller);
                        savedCall.setCreated(new Date().getTime());
                        savedCall.setRead(Call.CALL_UNREADED);

                        dbManager.saveCall(savedCall);

                        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.android.phone.PhoneUtils", loadPackageParam.classLoader), "hangupRingingCall", call);

                        param.setResult(null);

                        Intent intent = new Intent(XposedMod.FILTER_NOTIFY_BLOCKED);
                        intent.putExtra("blockNewCall", true);
                        mContext.sendBroadcast(intent);

                        Logger.log("Block call: " + caller + "," + savedCall.getCreated());
                    }
                } catch (Throwable t) {
                    Logger.log("Block Call error.");
                    Logger.log(t);
                }
            }
        });
    }
}
