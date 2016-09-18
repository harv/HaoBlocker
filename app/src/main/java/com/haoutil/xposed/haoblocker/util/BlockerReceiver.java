package com.haoutil.xposed.haoblocker.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.haoutil.xposed.haoblocker.BuildConfig;
import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;

public class BlockerReceiver extends BroadcastReceiver {
    public static final String ACTION = BuildConfig.APPLICATION_ID + ".receiver.BlockerReceiver";
    public static final String PERMISSION = BuildConfig.APPLICATION_ID + ".permission.SHOW_BLOCKER_NOTIFICATION";

    private SettingsHelper mSettingsHelper;
    private BlockerManager mBlockerManager;

    private NotificationManagerCompat mNotiManager;
    private NotificationCompat.Builder mNotiBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!BlockerReceiver.ACTION.equals(intent.getAction()))
            return;

        if (intent.getExtras() == null)
            return;

        if (mSettingsHelper == null) {
            mSettingsHelper = new SettingsHelper(context);
        }

        if (mBlockerManager == null) {
            mBlockerManager = new BlockerManager(context);
        }

        if (mNotiManager == null) {
            mNotiManager = NotificationManagerCompat.from(context);
        }

        if (mNotiBuilder == null) {
            mNotiBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("HaoBlocker")
                    .setTicker("HaoBlocker")
                    .setAutoCancel(true);
        }

        if (mSettingsHelper.isShowBlockNotification()) {
            int type = intent.getExtras().getInt("type");
            showNotification(context, type);
        }
    }

    private void showNotification(Context context, int type) {
        if (type != BlockerManager.TYPE_SMS && type != BlockerManager.TYPE_CALL) {
            return;
        }

        int unreadSMSCount = mBlockerManager.getUnReadSMSCount();
        int unreadCallCount = mBlockerManager.getUnReadCallCount();
        if (unreadSMSCount == 0 && unreadCallCount == 0) {
            return;
        }

        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("position", type == BlockerManager.TYPE_SMS ? 2 : 3);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SettingsActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotiBuilder.setSmallIcon(R.drawable.ic_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentText(
                        String.format(
                                context.getResources().getString(R.string.notification_content_text),
                                unreadSMSCount,
                                unreadCallCount
                        )
                )
                .setContentIntent(pendingIntent);

        mNotiManager.notify(0, mNotiBuilder.build());
    }
}
