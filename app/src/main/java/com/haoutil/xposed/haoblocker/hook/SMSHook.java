package com.haoutil.xposed.haoblocker.hook;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.SparseArray;

import com.haoutil.xposed.haoblocker.XposedMod;
import com.haoutil.xposed.haoblocker.util.DbManager;
import com.haoutil.xposed.haoblocker.util.Logger;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SMSHook implements BaseHook {
    private SettingsHelper settingsHelper;
    private DbManager dbManager;

    private Context mContext;

    private SparseArray<String[]> smsArrays = new SparseArray<String[]>();

    public SMSHook() {
        settingsHelper = new SettingsHelper();
    }

    @Override
    public void exec() {
        Logger.log("Hook com.android.internal.telephony.RIL...");
        Class<?> clazz = XposedHelpers.findClass("com.android.internal.telephony.RIL", null);

        XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                mContext = (Context) param.args[0];
                dbManager = new DbManager(mContext);
            }
        });

        XposedBridge.hookAllMethods(clazz, "processUnsolicited", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!settingsHelper.isEnable() || !settingsHelper.isEnableSMS()) {
                    return;
                }

                Parcel p = (Parcel) param.args[0];

                int position = p.dataPosition();
                int response = p.readInt();

                switch (response) {
                    case 1003:  // new SMS
                        try {
                            boolean received = true;

                            String a[] = new String[2];
                            a[1] = (String) XposedHelpers.callMethod(param.thisObject, "responseString", p);

                            SmsMessage sms = (SmsMessage) XposedHelpers.callStaticMethod(SmsMessage.class, "newFromCMT", (Object) a);
                            String sender = sms.getOriginatingAddress();
                            String content = sms.getMessageBody();

                            Object smsHeader = XposedHelpers.callMethod(XposedHelpers.getObjectField(sms, "mWrappedSmsMessage"), "getUserDataHeader");
                            if (smsHeader != null) {
                                Object concatRef = XposedHelpers.getObjectField(smsHeader, "concatRef");
                                int refNumber = XposedHelpers.getIntField(concatRef, "refNumber");
                                int seqNumber = XposedHelpers.getIntField(concatRef, "seqNumber");
                                int msgCount = XposedHelpers.getIntField(concatRef, "msgCount");

                                String[] smsArray = smsArrays.get(refNumber);
                                if (smsArray == null) {
                                    smsArray = new String[msgCount];
                                    smsArrays.put(refNumber, smsArray);
                                }
                                smsArray[seqNumber - 1] = content;

                                if (isFullFilled(smsArray)) {
                                    content = TextUtils.join("", smsArray);
                                    smsArrays.remove(refNumber);
                                } else {
                                    received = false;
                                }
                            }

                            if (received) {
                                Logger.log("New SMS: " + sender + "," + content);
                                if (dbManager.blockSMS(sender, content)) {
                                    try {
                                        XposedHelpers.callMethod(param.thisObject, "acknowledgeLastIncomingGsmSms", true, 0, null);
                                    } catch (Throwable t) {
                                        XposedHelpers.callMethod(param.thisObject, "acknowledgeLastIncomingCdmaSms", true, 0, null);
                                    }

                                    param.setResult(null);

                                    Intent intent = new Intent(XposedMod.FILTER_NOTIFY_BLOCKED);
                                    intent.putExtra("type", DbManager.TYPE_SMS);
                                    intent.putExtra("sender", sender);
                                    intent.putExtra("content", content);
                                    intent.putExtra("created", sms.getTimestampMillis());
                                    mContext.sendBroadcast(intent);
                                }
                            }
                        } catch (Throwable t) {
                            Logger.log("Block SMS error.");
                            Logger.log(t);
                        }
                        break;
//                    case 1018:  // new Incoming Call
//                        break;
                }

                p.setDataPosition(position);
            }
        });
    }

    private boolean isFullFilled(String[] smss) {
        for (String sms : smss) {
            if (sms == null) {
                return false;
            }
        }

        return true;
    }
}