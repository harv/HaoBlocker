package com.haoutil.xposed.haoblocker.hook;

import android.os.Build;

import com.haoutil.xposed.haoblocker.util.BlockerProvider;
import com.haoutil.xposed.haoblocker.util.BlockerReceiver;
import com.haoutil.xposed.haoblocker.util.Logger;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PermissionHook implements BaseHook {
    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
//        Logger.log("Hook com.android.server.pm.PackageManagerService...");
        XC_MethodHook hook =
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");
                        if (!"android".equals(pkgName) && !"com.android.phone".equals(pkgName))
                            return;

                        ArrayList<String> origRequestedPermissions =
                                (ArrayList<String>) XposedHelpers.getObjectField(param.args[0], "requestedPermissions");
                        ArrayList<Boolean> origRequestedPermissionsRequired =
                                (ArrayList<Boolean>) XposedHelpers.getObjectField(param.args[0], "requestedPermissionsRequired");
//                        param.setObjectExtra("orig_requested_permissions", origRequestedPermissions);
//                        param.setObjectExtra("orig_requested_permissions_required", origRequestedPermissionsRequired);

                        ArrayList<String> newRequestedPermissions = new ArrayList<>(origRequestedPermissions.size() + 2);
                        ArrayList<Boolean> newRequestedPermissionsRequired = new ArrayList<>(origRequestedPermissionsRequired.size() + 2);
                        for (int i = 0; i < origRequestedPermissions.size(); i++) {
                            newRequestedPermissions.add(origRequestedPermissions.get(i));
                            newRequestedPermissionsRequired.add(origRequestedPermissionsRequired.get(i));
                        }
                        newRequestedPermissions.add(BlockerReceiver.PERMISSION);
                        newRequestedPermissionsRequired.add(Boolean.TRUE);
                        newRequestedPermissions.add(BlockerProvider.PERMISSION);
                        newRequestedPermissionsRequired.add(Boolean.TRUE);

                        XposedHelpers.setObjectField(param.args[0], "requestedPermissions", newRequestedPermissions);
                        XposedHelpers.setObjectField(param.args[0], "requestedPermissionsRequired", newRequestedPermissionsRequired);
                    }

//                    @SuppressWarnings("unchecked")
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");
//                        if (!"android".equals(pkgName) && !"com.android.phone".equals(pkgName))
//                            return;
//
//                        // restore requested permissions if they were modified
//                        ArrayList<String> origRequestedPermissions = (ArrayList<String>) param.getObjectExtra("orig_requested_permissions");
//                        ArrayList<Boolean> origRequestedPermissionsRequired = (ArrayList<Boolean>) param.getObjectExtra("orig_requested_permissions_required");
//                        if (origRequestedPermissions != null && origRequestedPermissionsRequired != null) {
//                            XposedHelpers.setObjectField(param.args[0], "requestedPermissions", origRequestedPermissions);
//                            XposedHelpers.setObjectField(param.args[0], "requestedPermissionsRequired", origRequestedPermissionsRequired);
//                        }
//                    }
                };
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            XposedHelpers.findAndHookMethod(
                    "com.android.server.pm.PackageManagerService",
                    loadPackageParam.classLoader,
                    "grantPermissionsLPw",
                    "android.content.pm.PackageParser$Package",
                    boolean.class,
                    hook
            );
        } else {
            XposedHelpers.findAndHookMethod(
                    "com.android.server.pm.PackageManagerService",
                    loadPackageParam.classLoader,
                    "grantPermissionsLPw",
                    "android.content.pm.PackageParser$Package",
                    boolean.class,
                    String.class,
                    hook
            );
        }
    }
}
