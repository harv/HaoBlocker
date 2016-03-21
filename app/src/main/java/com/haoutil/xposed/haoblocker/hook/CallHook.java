package com.haoutil.xposed.haoblocker.hook;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.haoutil.xposed.haoblocker.XposedMod;
import com.haoutil.xposed.haoblocker.util.BlockerManager;
import com.haoutil.xposed.haoblocker.util.Logger;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CallHook implements BaseHook {
    private SettingsHelper settingsHelper;
    private BlockerManager blockerManager;

    private Context mContext;

    private final boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private final String className = isLollipop ? "com.android.services.telephony.PstnIncomingCallNotifier" : "com.android.phone.CallNotifier";
    private final String methodName = isLollipop ? "handleNewRingingConnection" : "onNewRingingConnection";

    public CallHook() {
        settingsHelper = new SettingsHelper();
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Logger.log("Hook " + className + "...");
        Class<?> clazz = XposedHelpers.findClass(className, loadPackageParam.classLoader);

        XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mContext = (Context) XposedHelpers.callMethod(
                        isLollipop ? XposedHelpers.getObjectField(param.thisObject, "mPhoneBase") : param.args[1],
                        "getContext"
                );
                blockerManager = new BlockerManager(mContext);
            }
        });

        XposedBridge.hookAllMethods(clazz, methodName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                if (!settingsHelper.isEnable() || !settingsHelper.isEnableCall()) {
                    return;
                }

                try {
                    Object connection = XposedHelpers.getObjectField(param.args[0], "result");
                    final Object call = XposedHelpers.callMethod(connection, "getCall");
                    final String caller = (String) XposedHelpers.callMethod(connection, "getAddress");

                    Logger.log("Incoming call: " + caller);

                    if (blockerManager.blockCall(caller)) {
                        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.android.phone.PhoneUtils", loadPackageParam.classLoader), "hangupRingingCall", call);

                        param.setResult(null);

                        Intent intent = new Intent(XposedMod.FILTER_NOTIFY_BLOCKED);
                        intent.putExtra("type", BlockerManager.TYPE_CALL);
                        intent.putExtra("caller", caller);
                        mContext.sendBroadcast(intent);
                    }
                } catch (Throwable t) {
                    Logger.log("Block Call error.");
                    Logger.log(t);
                }
            }
        });
    }
}
