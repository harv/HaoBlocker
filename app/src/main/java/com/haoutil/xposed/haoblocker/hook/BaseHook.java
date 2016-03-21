package com.haoutil.xposed.haoblocker.hook;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface BaseHook {
    void initZygote(final IXposedHookZygoteInit.StartupParam startupParam);

    void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam);
}
