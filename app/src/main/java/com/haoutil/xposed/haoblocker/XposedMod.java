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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.haoutil.xposed.haoblocker.hook.CallHook;
import com.haoutil.xposed.haoblocker.hook.SMSHook;
import com.haoutil.xposed.haoblocker.model.Call;
import com.haoutil.xposed.haoblocker.model.SMS;
import com.haoutil.xposed.haoblocker.util.DbManager;
import com.haoutil.xposed.haoblocker.util.Logger;

import java.util.Date;

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
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                final Runnable origCallback = (Runnable) param.args[0];
                param.args[0] = new Runnable() {
                    @Override
                    public void run() {
                        if (origCallback != null) origCallback.run();

                        Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        dbManager = new DbManager(mContext);
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
                                        XposedMod.this.saveHistoryAndNotify(context, intent);
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
        smallNotificationIcon = resParam.res.addResource(modRes, R.drawable.ic_launcher);
        largeNotificationIcon = ((BitmapDrawable) resParam.res.getDrawable(smallNotificationIcon)).getBitmap();
        notificationContentText = resParam.res.getString(resParam.res.addResource(modRes, R.string.notification_content_text));
    }

    private void saveHistoryAndNotify(Context context, Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            int type = bundle.getInt("type");
            switch (type) {
                case DbManager.TYPE_SMS:
                    SMS savedSMS = new SMS();
                    savedSMS.setSender(bundle.getString("sender"));
                    savedSMS.setContent(bundle.getString("content"));
                    savedSMS.setCreated(bundle.getLong("created"));
                    savedSMS.setRead(SMS.SMS_UNREADED);

                    dbManager.saveSMS(savedSMS);

                    Logger.log("Block SMS: " + savedSMS.getSender() + "," + savedSMS.getContent() + "," + savedSMS.getCreated());
                    break;
                case DbManager.TYPE_CALL:
                    Call savedCall = new Call();
                    savedCall.setCaller(bundle.getString("caller"));
                    savedCall.setCreated(new Date().getTime());
                    savedCall.setRead(Call.CALL_UNREADED);

                    dbManager.saveCall(savedCall);

                    Logger.log("Block call: " + savedCall.getCaller() + "," + savedCall.getCreated());
                    break;
            }

            showNotification(context, type);
        }
    }

    private void showNotification(Context context, int type) {
        if (type != DbManager.TYPE_SMS && type != DbManager.TYPE_CALL) {
            return;
        }

        int unreadSMSCount = dbManager.getUnReadSMSCount();
        int unreadCallCount = dbManager.getUnReadCallCount();
        if (unreadSMSCount == 0 && unreadCallCount == 0) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(MODULE_NAME, MODULE_NAME + ".activity.SettingsActivity"));
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("position", type == DbManager.TYPE_SMS ? 2 : 3);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        notiBuilder.setSmallIcon(smallNotificationIcon)
                .setLargeIcon(largeNotificationIcon)
                .setContentText(String.format(notificationContentText, unreadSMSCount, unreadCallCount))
                .setContentIntent(pendingIntent);

        notiManager.notify(0, notiBuilder.build());
    }
}
