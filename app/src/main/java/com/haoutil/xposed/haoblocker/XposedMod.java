package com.haoutil.xposed.haoblocker;

import com.haoutil.xposed.haoblocker.hook.CallHook;
import com.haoutil.xposed.haoblocker.hook.PermissionHook;
import com.haoutil.xposed.haoblocker.hook.SMSHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMod implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        new SMSHook().initZygote(startupParam);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.android.phone")) {
            new CallHook().handleLoadPackage(loadPackageParam);
        } else if (loadPackageParam.packageName.equals("android")) {
            new PermissionHook().handleLoadPackage(loadPackageParam);
        }
    }
}
