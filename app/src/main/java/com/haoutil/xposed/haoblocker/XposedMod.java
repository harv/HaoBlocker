package com.haoutil.xposed.haoblocker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XModuleResources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.haoutil.xposed.haoblocker.hook.CallHook;
import com.haoutil.xposed.haoblocker.hook.SMSHook;
import com.haoutil.xposed.haoblocker.util.DbManager;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMod implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    public static final String MODULE_NAME = "com.haoutil.xposed.haoblocker";
    public static final String FILTER_NOTIFY_BLOCKED = "com.haoutil.xposed.haoblocker_NOTIFY_BLOCKED";

    private String MODULE_PATH;

    private NotificationManagerCompat notiManager;
    private NotificationCompat.Builder notiBuilder;

    private int smallNotificationIcon = -1;
    private Bitmap largeNotificationIcon = null;
    private String notificationContentText;

    private DbManager dbManager;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        initNotificationBroadcastReceiver();
        new SMSHook().exec();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.android.phone")) {
            new CallHook(loadPackageParam).exec();
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resParam) throws Throwable {
        if (resParam.packageName.equals("android")) {
            if (smallNotificationIcon != -1 && largeNotificationIcon != null) {
                return;
            }

            getNotificationIcon(resParam);
        }
    }

    private void initNotificationBroadcastReceiver() {
        XposedHelpers.findAndHookMethod("com.android.server.am.ActivityManagerService", null, "systemReady", Runnable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final XC_MethodHook.MethodHookParam param) throws Throwable {
                final Runnable origCallback = (Runnable) param.args[0];
                param.args[0] = new Runnable() {
                    @Override
                    public void run() {
                        if (origCallback != null) origCallback.run();

                        Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        dbManager = new DbManager(mContext);
                        notiManager = NotificationManagerCompat.from(mContext);
                        notiBuilder = new NotificationCompat.Builder(mContext).setContentTitle("HaoBlocker").setTicker("HaoBlocker");
                        mContext.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                XposedMod.this.showNotification(context, intent);
                            }
                        }, new IntentFilter(FILTER_NOTIFY_BLOCKED));
                    }
                };
            }
        });
    }

    private void getNotificationIcon(XC_InitPackageResources.InitPackageResourcesParam resParam) {
        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resParam.res);
        smallNotificationIcon = resParam.res.addResource(modRes, R.drawable.ic_launcher);
        largeNotificationIcon = ((BitmapDrawable) resParam.res.getDrawable(smallNotificationIcon)).getBitmap();
        notificationContentText = resParam.res.getString(resParam.res.addResource(modRes, R.string.notification_content_text));
    }

    private void showNotification(Context context, Intent intent) {
        int unreadSMSCount = dbManager.getUnReadSMSCount();
        int unreadCallCount = dbManager.getUnReadCallCount();
        if (unreadSMSCount == 0 && unreadCallCount == 0) {
            return;
        }

        Intent viewIntent = new Intent(Intent.ACTION_MAIN);
        viewIntent.setComponent(new ComponentName(MODULE_NAME, MODULE_NAME + ".activity.SettingsActivity"));
        viewIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        viewIntent.putExtra("position", intent == null || intent.getExtras().getBoolean("blockNewSMS", false) ? 2 : 3);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        notiBuilder.setSmallIcon(smallNotificationIcon)
                .setLargeIcon(largeNotificationIcon)
                .setContentText(String.format(notificationContentText, unreadSMSCount, unreadCallCount))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notiManager.notify(0, notiBuilder.build());
    }
}
