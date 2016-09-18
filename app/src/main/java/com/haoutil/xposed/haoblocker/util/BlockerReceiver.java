package com.haoutil.xposed.haoblocker.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.haoutil.xposed.haoblocker.BuildConfig;
import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;

public class BlockerReceiver extends BroadcastReceiver {
    public static final String ACTION = BuildConfig.APPLICATION_ID + ".receiver.BlockerReceiver";
    public static final String PERMISSION = BuildConfig.APPLICATION_ID + ".permission.SHOW_BLOCKER_NOTIFICATION";

    private SettingsHelper settingsHelper;
    private BlockerManager blockerManager;

    private NotificationManagerCompat notiManager;
    private NotificationCompat.Builder notiBuilder;

    private Handler mHandler;

    public BlockerReceiver() {
        HandlerThread thread = new HandlerThread("HaoBlocker");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!BlockerReceiver.ACTION.equals(intent.getAction())) {
            return;
        }

        final Context mContext = context.getApplicationContext();
        final Intent mIntent = intent;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (settingsHelper == null) {
                    settingsHelper = new SettingsHelper(mContext);
                }

                if (blockerManager == null) {
                    blockerManager = new BlockerManager(mContext);
                }

                if (notiManager == null) {
                    notiManager = NotificationManagerCompat.from(mContext);
                }

                if (notiBuilder == null) {
                    notiBuilder = new NotificationCompat.Builder(mContext)
                            .setContentTitle("HaoBlocker")
                            .setTicker("HaoBlocker")
                            .setAutoCancel(true);
                }

                if (settingsHelper.isShowBlockNotification()) {
                    if (mIntent != null && mIntent.getExtras() != null) {
                        int type = mIntent.getExtras().getInt("type");
                        showNotification(mContext, type);
                    }
                }
            }
        });
    }

    private void showNotification(Context context, int type) {
        if (type != BlockerManager.TYPE_SMS && type != BlockerManager.TYPE_CALL) {
            return;
        }

        int unreadSMSCount = blockerManager.getUnReadSMSCount();
        int unreadCallCount = blockerManager.getUnReadCallCount();
        if (unreadSMSCount == 0 && unreadCallCount == 0) {
            return;
        }

        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("position", type == BlockerManager.TYPE_SMS ? 2 : 3);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SettingsActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notiBuilder.setSmallIcon(R.drawable.ic_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentText(
                        String.format(
                                context.getResources().getString(R.string.notification_content_text),
                                unreadSMSCount,
                                unreadCallCount
                        )
                )
                .setContentIntent(pendingIntent);

        notiManager.notify(0, notiBuilder.build());
    }
}
