package com.haoutil.xposed.haoblocker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XModuleResources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.hook.CallHook;
import com.haoutil.xposed.haoblocker.hook.SMSHook;
import com.haoutil.xposed.haoblocker.model.entity.Call;
import com.haoutil.xposed.haoblocker.model.entity.SMS;
import com.haoutil.xposed.haoblocker.util.BlockerManager;
import com.haoutil.xposed.haoblocker.util.Logger;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

import java.util.Date;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMod implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    public static final String MODULE_NAME = BuildConfig.APPLICATION_ID;
    public static final String FILTER_NOTIFY_BLOCKED = BuildConfig.APPLICATION_ID + "_NOTIFY_BLOCKED";

    private String MODULE_PATH;

    private NotificationManagerCompat notiManager;
    private NotificationCompat.Builder notiBuilder;

    private int smallNotificationIcon = -1;
    private String notificationContentText;

    private SettingsHelper settingsHelper;
    private BlockerManager blockerManager;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        settingsHelper = new SettingsHelper();
        new SMSHook().initZygote(startupParam);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.android.phone")) {
            new CallHook().handleLoadPackage(loadPackageParam);
        } else if (loadPackageParam.packageName.equals("android")) {
            initNotificationBroadcastReceiver(loadPackageParam);
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resParam) throws Throwable {
        if (resParam.packageName.equals("android")) {
            if (smallNotificationIcon != -1) {
                return;
            }

            getNotificationIcon(resParam);
        }
    }

    private void initNotificationBroadcastReceiver(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("com.android.server.am.ActivityManagerService", loadPackageParam.classLoader, "systemReady", Runnable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                final Runnable origCallback = (Runnable) param.args[0];
                param.args[0] = new Runnable() {
                    @Override
                    public void run() {
                        if (origCallback != null) origCallback.run();

                        final Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        blockerManager = new BlockerManager(mContext);
                        notiManager = NotificationManagerCompat.from(mContext);
                        notiBuilder = new NotificationCompat.Builder(mContext).setContentTitle("HaoBlocker").setTicker("HaoBlocker").setAutoCancel(true);

                        HandlerThread thread = new HandlerThread("HaoBlocker");
                        thread.start();
                        final Handler mHandler = new Handler(thread.getLooper());
                        mContext.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(final Context context, final Intent intent) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        XposedMod.this.saveHistoryAndNotify(mContext, intent);
                                    }
                                });
                            }
                        }, new IntentFilter(FILTER_NOTIFY_BLOCKED));
                    }
                };
            }
        });
    }

    private void getNotificationIcon(XC_InitPackageResources.InitPackageResourcesParam resParam) {
        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resParam.res);
        smallNotificationIcon = resParam.res.addResource(modRes, R.drawable.ic_notification);
        notificationContentText = resParam.res.getString(resParam.res.addResource(modRes, R.string.notification_content_text));
    }

    private void saveHistoryAndNotify(Context context, Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            int type = bundle.getInt("type");
            switch (type) {
                case BlockerManager.TYPE_SMS:
                    SMS savedSMS = new SMS();
                    savedSMS.setSender(bundle.getString("sender"));
                    savedSMS.setContent(bundle.getString("content"));
                    savedSMS.setCreated(bundle.getLong("created"));
                    savedSMS.setRead(SMS.SMS_UNREADED);

                    blockerManager.saveSMS(savedSMS);

                    Logger.log("Block SMS: " + savedSMS.getSender() + "," + savedSMS.getContent() + "," + savedSMS.getCreated());
                    break;
                case BlockerManager.TYPE_CALL:
                    Call savedCall = new Call();
                    savedCall.setCaller(bundle.getString("caller"));
                    savedCall.setCreated(new Date().getTime());
                    savedCall.setRead(Call.CALL_UNREADED);

                    blockerManager.saveCall(savedCall);

                    Logger.log("Block call: " + savedCall.getCaller() + "," + savedCall.getCreated());
                    break;
            }

            if (settingsHelper.isShowBlockNotification()) {
                showNotification(context, type);
            }
        }
    }

    private void showNotification(Context context, int type) {
        if (smallNotificationIcon == -1 || notificationContentText == null) {
            return;
        }

        if (type != BlockerManager.TYPE_SMS && type != BlockerManager.TYPE_CALL) {
            return;
        }

        int unreadSMSCount = blockerManager.getUnReadSMSCount();
        int unreadCallCount = blockerManager.getUnReadCallCount();
        if (unreadSMSCount == 0 && unreadCallCount == 0) {
            return;
        }

        ComponentName componentName = new ComponentName(MODULE_NAME, SettingsActivity.class.getName());

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(componentName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("position", type == BlockerManager.TYPE_SMS ? 2 : 3);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(componentName);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        notiBuilder.setSmallIcon(smallNotificationIcon)
                .setContentText(String.format(notificationContentText, unreadSMSCount, unreadCallCount))
                .setContentIntent(pendingIntent);

        notiManager.notify(0, notiBuilder.build());
    }
}
