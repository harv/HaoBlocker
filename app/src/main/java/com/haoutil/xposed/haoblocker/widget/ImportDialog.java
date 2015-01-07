package com.haoutil.xposed.haoblocker.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.adapter.ImportAdapter;
import com.haoutil.xposed.haoblocker.event.RuleUpdateEvent;
import com.haoutil.xposed.haoblocker.model.Import;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.util.DbManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

public class ImportDialog extends Dialog {
    private Activity activity;
    private int type;

    @InjectView(R.id.tv_loading)
    TextView tv_loading;
    @InjectView(R.id.lv_items)
    ListView lv_items;
    @InjectView(R.id.cb_block_sms)
    CheckBox cb_block_sms;
    @InjectView(R.id.cb_block_call)
    CheckBox cb_block_call;

    private ImportAdapter adapter;

    public ImportDialog(Activity activity, int type) {
        super(activity, R.style.DialogTheme);
        this.activity = activity;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rule_import);

        ButterKnife.inject(this);

        new LoadImportAdapterTask().execute();
    }

    @OnClick({R.id.tv_ok, R.id.tv_cancel})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_ok && adapter != null) {
            DbManager dbManager = new DbManager(activity);

            List<Import> selectedItems = adapter.getSelectedItems();
            if (selectedItems != null && selectedItems.size() > 0) {
                for (Import im : selectedItems) {
                    Rule rule = new Rule();
                    rule.setContent(im.getNumber());
                    rule.setType(Rule.TYPE_STRING);
                    rule.setSms(cb_block_sms.isChecked() ? 1 : 0);
                    rule.setCall(cb_block_call.isChecked() ? 1 : 0);
                    rule.setException(0);
                    rule.setCreated(im.getTime());

                    dbManager.saveRule(rule);
                }
                EventBus.getDefault().post(new RuleUpdateEvent(RuleUpdateEvent.EVENT_REFRESH_LIST));
            }
        }

        dismiss();
    }

    @OnItemClick(R.id.lv_items)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.selectItem(position);
        adapter.notifyDataSetChanged();
    }

    private class LoadImportAdapterTask extends AsyncTask<Void, Void, List<Import>> {
        @Override
        protected List<Import> doInBackground(Void... params) {
            List<Import> list = new ArrayList<>();
            ContentResolver cr = activity.getContentResolver();
            Cursor cursor;

            switch (type) {
                case DbManager.TYPE_CALL:
                    cursor = cr.query(
                            CallLog.Calls.CONTENT_URI,
                            new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE, CallLog.Calls.TYPE},
                            null,
                            null,
                            CallLog.Calls.DEFAULT_SORT_ORDER
                    );
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Import im = new Import();
                            im.setType(DbManager.TYPE_CALL);
                            im.setNumber(cursor.getString(0));
                            im.setName(cursor.getString(1));
                            im.setTime(cursor.getLong(2));
                            im.setInout(cursor.getInt(3));

                            list.add(im);
                        } while (cursor.moveToNext());

                        return list;
                    }
                    break;
                case DbManager.TYPE_SMS:
                    cursor = cr.query(
                            Uri.parse("content://sms/inbox"),
                            new String[]{"address", "person", "body", "date", "type"},
                            null,
                            null,
                            "date desc"
                    );
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Import im = new Import();
                            im.setType(DbManager.TYPE_SMS);
                            im.setNumber(cursor.getString(0));
//                            im.setName(cursor.getString(1));
                            im.setContent(cursor.getString(2));
                            im.setTime(cursor.getLong(3));
                            im.setInout(cursor.getInt(4));

                            list.add(im);
                        } while (cursor.moveToNext());

                        return list;
                    }
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Import> list) {
            if (list != null) {
                adapter = new ImportAdapter(activity, list);
                lv_items.setAdapter(adapter);
            }

            tv_loading.setVisibility(View.GONE);
            lv_items.setVisibility(View.VISIBLE);
        }
    }
}
