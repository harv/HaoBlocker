package com.haoutil.xposed.haoblocker.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.haoutil.xposed.haoblocker.model.Call;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.model.SMS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbManager {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_SMS = 1;
    public static final int TYPE_CALL = 2;
    public static final int TYPE_EXCEPTION = 3;

    private static final String AUTHORITY = "com.haoutil.xposed.haoblocker.provider.BlockProvider";

    private static final Uri URI_RULE_ALL = Uri.parse("content://" + AUTHORITY + "/rule");
    private static final Uri URI_SMS_ALL = Uri.parse("content://" + AUTHORITY + "/sms");
    private static final Uri URI_CALL_ALL = Uri.parse("content://" + AUTHORITY + "/call");

    private ContentResolver resolver;

    public DbManager(Context context) {
        resolver = context.getContentResolver();
    }

    public List<Rule> getRules(int type) {
        List<Rule> list = new ArrayList<>();

        Cursor cursor = null;
        if (type == TYPE_ALL) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created"}, null, null, "created DESC");
        } else if (type == TYPE_SMS) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created"}, "sms = ?", new String[]{"1"}, "created DESC");
        } else if (type == TYPE_CALL) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created"}, "call = ?", new String[]{"1"}, "created DESC");
        } else if (type == TYPE_EXCEPTION) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created"}, "exception = ?", new String[]{"1"}, "created DESC");
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Rule rule = new Rule();
                rule.setId(cursor.getLong(0));
                rule.setContent(cursor.getString(1));
                rule.setType(cursor.getInt(2));
                rule.setSms(cursor.getInt(3));
                rule.setCall(cursor.getInt(4));
                rule.setException(cursor.getInt(5));
                rule.setCreated(cursor.getLong(6));

                list.add(rule);
            } while (cursor.moveToNext());
        }

        return list;
    }

    public long saveRule(Rule rule) {
        ContentValues values = new ContentValues();
        values.put("content", rule.getContent());
        values.put("type", rule.getType());
        values.put("sms", rule.getSms());
        values.put("call", rule.getCall());
        values.put("exception", rule.getException());
        values.put("created", new Date().getTime());

        return ContentUris.parseId(resolver.insert(URI_RULE_ALL, values));
    }

    public void updateRule(Rule rule) {
        ContentValues values = new ContentValues();
        values.put("content", rule.getContent());
        values.put("type", rule.getType());
        values.put("sms", rule.getSms());
        values.put("call", rule.getCall());
        values.put("exception", rule.getException());
        values.put("created", new Date().getTime());

        resolver.update(ContentUris.withAppendedId(URI_RULE_ALL, rule.getId()), values, null, null);
    }

    public void deleteRule(List<Rule> list) {
        for (Rule rule : list) {
            resolver.delete(ContentUris.withAppendedId(URI_RULE_ALL, rule.getId()), null, null);
        }
    }

    public List<SMS> getSMSes(long id) {
        List<SMS> list = new ArrayList<>();
        Cursor cursor = resolver.query(URI_SMS_ALL, new String[]{"_id", "sender", "content", "created", "read"}, "_id > ?", new String[] {String.valueOf(id)}, "created DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SMS sms = new SMS();
                sms.setId(cursor.getLong(0));
                sms.setSender(cursor.getString(1));
                sms.setContent(cursor.getString(2));
                sms.setCreated(cursor.getLong(3));
                sms.setRead(cursor.getInt(4));

                list.add(sms);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void saveSMS(SMS sms) {
        ContentValues values = new ContentValues();
        values.put("sender", sms.getSender());
        values.put("content", sms.getContent());
        values.put("created", sms.getCreated());
        values.put("read", sms.getRead());

        resolver.insert(URI_SMS_ALL, values);
    }

    public void deleteSMS(List<SMS> list) {
        for (SMS sms : list) {
            resolver.delete(ContentUris.withAppendedId(URI_SMS_ALL, sms.getId()), null, null);
        }
    }

    public void setRead(SMS sms) {
        ContentValues values = new ContentValues();
        values.put("read", sms.getRead());

        resolver.update(ContentUris.withAppendedId(URI_SMS_ALL, sms.getId()), values, null, null);
    }

    public List<Call> getCalls(long id) {
        List<Call> list = new ArrayList<>();
        Cursor cursor = resolver.query(URI_CALL_ALL, new String[]{"_id", "caller", "created", "read"}, "_id > ?", new String[] {String.valueOf(id)}, "created DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Call call = new Call();
                call.setId(cursor.getLong(0));
                call.setCaller(cursor.getString(1));
                call.setCreated(cursor.getLong(2));
                call.setRead(cursor.getInt(3));

                list.add(call);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCall(Call call) {
        ContentValues values = new ContentValues();
        values.put("caller", call.getCaller());
        values.put("created", call.getCreated());
        values.put("read", call.getRead());

        resolver.insert(URI_CALL_ALL, values);
    }

    public void deleteCall(List<Call> list) {
        for (Call call : list) {
            resolver.delete(ContentUris.withAppendedId(URI_CALL_ALL, call.getId()), null, null);
        }
    }

    public void readAllCall() {
        ContentValues values = new ContentValues();
        values.put("read", 1);
        resolver.update(URI_CALL_ALL, values, "read=?", new String[]{"0"});
    }

    public boolean blockSMS(String sender, String content) {
        List<Rule> exceptions = getRules(TYPE_EXCEPTION);
        if (exceptions != null && exceptions.size() > 0) {
            for (Rule exception : exceptions) {
                switch (exception.getType()) {
                    case Rule.TYPE_STRING:
                        if (sender.equals(exception.getContent())) {
                            return false;
                        }
                        break;
                    case Rule.TYPE_WILDCARD:
                        if (wildcardMatch(exception.getContent(), sender)) {
                            return false;
                        }
                        break;
                    case Rule.TYPE_KEYWORD:
                        if (content.contains(exception.getContent())) {
                            return false;
                        }
                        break;
                }
            }
        }
        List<Rule> rules = getRules(TYPE_SMS);
        if (rules != null && rules.size() > 0) {
            for (Rule rule : rules) {
                switch (rule.getType()) {
                    case Rule.TYPE_STRING:
                        if (sender.equals(rule.getContent())) {
                            return true;
                        }
                        break;
                    case Rule.TYPE_WILDCARD:
                        if (wildcardMatch(rule.getContent(), sender)) {
                            return true;
                        }
                        break;
                    case Rule.TYPE_KEYWORD:
                        if (content.contains(rule.getContent())) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    public boolean blockCall(String caller) {
        List<Rule> exceptions = getRules(TYPE_EXCEPTION);
        if (exceptions != null && exceptions.size() > 0) {
            for (Rule exception : exceptions) {
                switch (exception.getType()) {
                    case Rule.TYPE_STRING:
                        if (caller.equals(exception.getContent())) {
                            return false;
                        }
                        break;
                    case Rule.TYPE_WILDCARD:
                        if (wildcardMatch(exception.getContent(), caller)) {
                            return false;
                        }
                        break;
                }
            }
        }
        List<Rule> list = getRules(TYPE_CALL);
        if (list != null && list.size() > 0) {
            for (Rule rule : list) {
                switch (rule.getType()) {
                    case Rule.TYPE_STRING:
                        if (caller.equals(rule.getContent())) {
                            return true;
                        }
                        break;
                    case Rule.TYPE_WILDCARD:
                        if (wildcardMatch(rule.getContent(), caller)) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    public int getUnReadSMSCount() {
        return resolver.query(URI_SMS_ALL, new String[]{"_id"}, "read=?", new String[]{"0"}, null).getCount();
    }

    public int getUnReadCallCount() {
        return resolver.query(URI_CALL_ALL, new String[]{"_id"}, "read=?", new String[]{"0"}, null).getCount();
    }

    private boolean wildcardMatch(String wildcard, String str) {
        if (wildcard == null || str == null)
            return false;

        boolean result = false;
        char c;
        boolean beforeStar = false;
        int back_i = 0;
        int back_j = 0;
        int i, j;
        for (i = 0, j = 0; i < str.length(); ) {
            if (wildcard.length() <= j) {
                if (back_i != 0) {
                    beforeStar = true;
                    i = back_i;
                    j = back_j;
                    back_i = 0;
                    back_j = 0;
                    continue;
                }
                break;
            }

            if ((c = wildcard.charAt(j)) == '*') {
                if (j == wildcard.length() - 1) {
                    result = true;
                    break;
                }
                beforeStar = true;
                j++;
                continue;
            }

            if (beforeStar) {
                if (str.charAt(i) == c) {
                    beforeStar = false;
                    back_i = i + 1;
                    back_j = j;
                    j++;
                }
            } else {
                if (c != '?' && c != str.charAt(i)) {
                    result = false;
                    if (back_i != 0) {
                        beforeStar = true;
                        i = back_i;
                        j = back_j;
                        back_i = 0;
                        back_j = 0;
                        continue;
                    }
                    break;
                }
                j++;
            }
            i++;
        }

        if (i == str.length() && j == wildcard.length())
            result = true;
        return result;
    }
}
