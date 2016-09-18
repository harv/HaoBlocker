package com.haoutil.xposed.haoblocker.hook;

import android.content.Context;
import android.os.Build;

import com.haoutil.xposed.haoblocker.util.BlockerManager;
import com.haoutil.xposed.haoblocker.util.Logger;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CallHook implements BaseHook {
    private SettingsHelper mSettingsHelper;
    private BlockerManager mBlockerManager;

    private final boolean mIsLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private final String mClassName = mIsLollipop ? "com.android.services.telephony.PstnIncomingCallNotifier" : "com.android.phone.CallNotifier";
    private final String mMethodName = mIsLollipop ? "handleNewRingingConnection" : "onNewRingingConnection";

    public CallHook() {
        mSettingsHelper = new SettingsHelper();
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
//        Logger.log("Hook " + mClassName + "...");
        Class<?> clazz = XposedHelpers.findClass(mClassName, loadPackageParam.classLoader);

        XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) XposedHelpers.callMethod(
                        mIsLollipop ? XposedHelpers.getObjectField(param.thisObject, "mPhoneBase") : param.args[1],
                        "getContext"
                );
                mBlockerManager = new BlockerManager(context);
            }
        });

        XposedBridge.hookAllMethods(clazz, mMethodName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                if (!mSettingsHelper.isEnable() || !mSettingsHelper.isEnableCall()) {
                    return;
                }

                try {
                    Object connection = XposedHelpers.getObjectField(param.args[0], "result");
                    final Object call = XposedHelpers.callMethod(connection, "getCall");
                    final String caller = (String) XposedHelpers.callMethod(connection, "getAddress");

                    Logger.log("Incoming call: " + caller);

                    if (mBlockerManager.blockCall(caller)) {
                        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.android.phone.PhoneUtils", loadPackageParam.classLoader), "hangupRingingCall", call);

                        param.setResult(null);

                        Logger.log("Block call: " + caller);
                    }
                } catch (Throwable t) {
                    Logger.log("Block Call error.");
                    Logger.log(t);
                }
            }
        });
    }
}
