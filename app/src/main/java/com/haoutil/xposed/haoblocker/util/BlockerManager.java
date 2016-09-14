package com.haoutil.xposed.haoblocker.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.haoutil.xposed.haoblocker.BuildConfig;
import com.haoutil.xposed.haoblocker.model.entity.Call;
import com.haoutil.xposed.haoblocker.model.entity.Rule;
import com.haoutil.xposed.haoblocker.model.entity.SMS;

import java.util.ArrayList;
import java.util.List;

public class BlockerManager {
    private static final String COUNTRY_CODES = ",376,971,93,355,374,599,244,672,54,43,61,297,994,387,880,32,226,359,973,257" +
            ",229,590,673,591,55,975,267,375,501,1,61,243,236,242,41,225,682,56,237,86" +
            ",57,506,53,238,61,357,420,49,253,45,213,593,372,20,291,34,251,358,679,500" +
            ",691,298,33,241,44,995,233,350,299,220,224,240,30,502,245,592,852,504,385,509" +
            ",36,62,353,972,44,91,964,98,39,962,81,254,996,855,686,269,850,82,965,7" +
            ",856,961,423,94,231,266,370,352,371,218,212,377,373,382,261,692,389,223,95,976" +
            ",853,222,356,230,960,265,52,60,258,264,687,227,234,505,31,47,977,674,683,64" +
            ",968,507,51,689,675,63,92,48,508,870,1,351,680,595,974,40,381,7,250,966" +
            ",677,248,249,46,65,290,386,421,232,378,221,252,597,239,503,963,268,235,228,66" +
            ",992,690,670,993,216,676,90,688,886,255,380,256,1,598,998,39,58,84,678,681" +
            ",685,967,262,27,260,263,";

    public static final int TYPE_ALL = 0;
    public static final int TYPE_SMS = 1;
    public static final int TYPE_CALL = 2;
    public static final int TYPE_EXCEPT = 3;

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.BlockProvider";

    private static final Uri URI_RULE_ALL = Uri.parse("content://" + AUTHORITY + "/rule");
    private static final Uri URI_SMS_ALL = Uri.parse("content://" + AUTHORITY + "/sms");
    private static final Uri URI_CALL_ALL = Uri.parse("content://" + AUTHORITY + "/call");

    private ContentResolver resolver;

    public BlockerManager(Context context) {
        resolver = context.getContentResolver();
    }

    public List<Rule> getRules(int type) {
        List<Rule> list = new ArrayList<>();

        Cursor cursor = null;
        if (type == TYPE_ALL) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created", "remark"}, null, null, "created DESC");
        } else if (type == TYPE_SMS) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created", "remark"}, "sms = ?", new String[]{"1"}, "created DESC");
        } else if (type == TYPE_CALL) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created", "remark"}, "call = ?", new String[]{"1"}, "created DESC");
        } else if (type == TYPE_EXCEPT) {
            cursor = resolver.query(URI_RULE_ALL, new String[]{"_id", "content", "type", "sms", "call", "exception", "created", "remark"}, "exception = ?", new String[]{"1"}, "created DESC");
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
                rule.setRemark(cursor.getString(7));

                list.add(rule);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    public boolean hasRule(Rule rule) {
        boolean rtn = false;

        Cursor cursor = resolver.query(URI_RULE_ALL, new String[]{"_id"}, "content = ? AND type = ? AND sms = ? AND call = ? AND exception = ?", new String[]{rule.getContent(), String.valueOf(rule.getType()), String.valueOf(rule.getSms()), String.valueOf(rule.getCall()), String.valueOf(rule.getException())}, "created DESC");
        if (cursor != null && cursor.getCount() > 0) {
            rtn = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return rtn;
    }

    public long saveRule(Rule rule) {
        ContentValues values = new ContentValues();
        values.put("content", rule.getContent());
        values.put("type", rule.getType());
        values.put("sms", rule.getSms());
        values.put("call", rule.getCall());
        values.put("exception", rule.getException());
        values.put("created", rule.getCreated());
        values.put("remark", rule.getRemark());

        return ContentUris.parseId(resolver.insert(URI_RULE_ALL, values));
    }

    public long restoreRule(Rule rule) {
        return saveRule(rule);
    }

    public void updateRule(Rule rule) {
        ContentValues values = new ContentValues();
        values.put("content", rule.getContent());
        values.put("type", rule.getType());
        values.put("sms", rule.getSms());
        values.put("call", rule.getCall());
        values.put("exception", rule.getException());
        values.put("created", rule.getCreated());
        values.put("remark", rule.getRemark());

        resolver.update(ContentUris.withAppendedId(URI_RULE_ALL, rule.getId()), values, null, null);
    }

    public void deleteRule(Rule rule) {
        resolver.delete(ContentUris.withAppendedId(URI_RULE_ALL, rule.getId()), null, null);
    }

    public List<SMS> getSMSes(long id) {
        List<SMS> list = new ArrayList<>();
        Cursor cursor = resolver.query(URI_SMS_ALL, new String[]{"_id", "sender", "content", "created", "read"}, "_id > ?", new String[]{String.valueOf(id)}, "created DESC");
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

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    public boolean hasSMS(SMS sms) {
        boolean rtn = false;

        Cursor cursor = resolver.query(URI_SMS_ALL, new String[]{"_id"}, "sender = ? AND content = ? AND created = ?", new String[]{sms.getSender(), sms.getContent(), String.valueOf(sms.getCreated())}, "created DESC");
        if (cursor != null && cursor.getCount() > 0) {
            rtn = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return rtn;
    }

    public long saveSMS(SMS sms) {
        ContentValues values = new ContentValues();
        values.put("sender", sms.getSender());
        values.put("content", sms.getContent());
        values.put("created", sms.getCreated());
        values.put("read", sms.getRead());

        return ContentUris.parseId(resolver.insert(URI_SMS_ALL, values));
    }

    public long restoreSMS(SMS sms) {
        return saveSMS(sms);
    }

    public void deleteSMS(SMS sms) {
        resolver.delete(ContentUris.withAppendedId(URI_SMS_ALL, sms.getId()), null, null);
    }

    public void readAllSMS() {
        ContentValues values = new ContentValues();
        values.put("read", 1);
        resolver.update(URI_SMS_ALL, values, "read=?", new String[]{"0"});
    }

    public List<Call> getCalls(long id) {
        List<Call> list = new ArrayList<>();
        Cursor cursor = resolver.query(URI_CALL_ALL, new String[]{"_id", "caller", "created", "read"}, "_id > ?", new String[]{String.valueOf(id)}, "created DESC");
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

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    public boolean hasCall(Call call) {
        boolean rtn = false;

        Cursor cursor = resolver.query(URI_CALL_ALL, new String[]{"_id"}, "caller = ? AND created = ?", new String[]{call.getCaller(), String.valueOf(call.getCreated())}, "created DESC");
        if (cursor != null && cursor.getCount() > 0) {
            rtn = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return rtn;
    }

    public long saveCall(Call call) {
        ContentValues values = new ContentValues();
        values.put("caller", call.getCaller());
        values.put("created", call.getCreated());
        values.put("read", call.getRead());

        return ContentUris.parseId(resolver.insert(URI_CALL_ALL, values));
    }

    public long restoreCall(Call call) {
        return saveCall(call);
    }

    public void deleteCall(Call call) {
        resolver.delete(ContentUris.withAppendedId(URI_CALL_ALL, call.getId()), null, null);
    }

    public void readAllCall() {
        ContentValues values = new ContentValues();
        values.put("read", 1);
        resolver.update(URI_CALL_ALL, values, "read=?", new String[]{"0"});
    }

    public boolean blockSMS(String sender, String content) {
        sender = trimCountryCode(sender);
        List<Rule> exceptions = getRules(TYPE_EXCEPT);
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
        caller = trimCountryCode(caller);
        List<Rule> exceptions = getRules(TYPE_EXCEPT);
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
        int count = 0;

        Cursor cursor = resolver.query(URI_SMS_ALL, new String[]{"_id"}, "read=?", new String[]{"0"}, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        return count;
    }

    public int getUnReadCallCount() {
        int count = 0;

        Cursor cursor = resolver.query(URI_CALL_ALL, new String[]{"_id"}, "read=?", new String[]{"0"}, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        return count;
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

    private String trimCountryCode(String phoneNumber) {
        if (phoneNumber.charAt(0) == '+') {
            if (COUNTRY_CODES.contains("," + phoneNumber.substring(1, 2) + ",")) {
                return phoneNumber.substring(2);
            } else if (COUNTRY_CODES.contains("," + phoneNumber.substring(1, 3) + ",")) {
                return phoneNumber.substring(3);
            } else if (COUNTRY_CODES.contains("," + phoneNumber.substring(1, 4) + ",")) {
                return phoneNumber.substring(4);
            }
        }
        return phoneNumber;
    }
}
