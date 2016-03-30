package com.haoutil.xposed.haoblocker.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.ui.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.ui.adapter.SMSAdapter;
import com.haoutil.xposed.haoblocker.model.entity.SMS;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SMSFragment extends BaseFragment implements BaseRecycleAdapter.OnItemClick, View.OnClickListener, DialogInterface.OnClickListener, SettingsActivity.OnMenuItemClickListener {
    private SettingsActivity activity;
    private BlockerManager blockerManager;

    private RecyclerView rv_sms;
    private SMSAdapter adapter;

    private int positionDeleted = -1;
    private SMS smsDeleted = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
        blockerManager = new BlockerManager(activity);
        blockerManager.readAllSMS();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            rv_sms = (RecyclerView) view.findViewById(R.id.rv_sms);
            rv_sms.setLayoutManager(new LinearLayoutManager(activity));

            List<SMS> smses = blockerManager.getSMSes(-1);
            adapter = new SMSAdapter(activity, smses, SMSFragment.this);
            rv_sms.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onClick(int position) {
    }

    @Override
    public void onLongClick(int position) {
        positionDeleted = position;
        confirm(this, this);
    }

    @Override
    public void onClick(View v) {
        if (-1 != positionDeleted && null != smsDeleted) {
            long newId = blockerManager.restoreSMS(smsDeleted);
            smsDeleted.setId(newId);
            adapter.add(positionDeleted, smsDeleted);

            positionDeleted = -1;
            smsDeleted = null;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                smsDeleted = adapter.getItem(positionDeleted);
                blockerManager.deleteSMS(smsDeleted);
                adapter.remove(positionDeleted);
                activity.showTip(R.string.rule_tip_sms_deleted, SMSFragment.this);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                positionDeleted = -1;
                break;
        }
    }

    @Override
    public void onFilter(MenuItem item) {
    }

    @Override
    public void onExport(MenuItem item) {
        new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_sms.csv");
                    OutputStream os = new FileOutputStream(file);

                    List<SMS> smses = blockerManager.getSMSes(-1);
                    StringBuilder sb = new StringBuilder();
                    for (int i = smses.size(); i > 0; i--) {
                        SMS sms = smses.get(i - 1);
                        sb.append(sms.getId());
                        sb.append(",").append(sms.getSender());
                        sb.append(",").append("\"").append(sms.getContent().replaceAll("\"", "\"\"")).append("\"");
                        sb.append(",").append(sms.getCreated());
                        sb.append(",").append(sms.getRead());
                        sb.append("\n");
                    }
                    byte[] bs = sb.toString().getBytes();
                    os.write(bs, 0, bs.length);
                    os.flush();
                    os.close();

                    activity.showTipInThread(R.string.menu_export_sms_tip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    @Override
    public void onImport(MenuItem item) {
        new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_sms.csv");
                    if (!file.exists() || !file.isFile()) {
                        activity.showTipInThread(R.string.menu_import_sms_miss_tip);
                        return;
                    }
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split(",");
                        String sender = columns[1];
                        String content = columns[2];
                        content = content.substring(1, content.length() - 1).replaceAll("\"\"", "\"");
                        long created = Long.valueOf(columns[3]);
                        int read = Integer.valueOf(columns[4]);

                        SMS sms = new SMS();
                        sms.setSender(sender);
                        sms.setContent(content);
                        sms.setCreated(created);
                        sms.setRead(read);

                        long id = blockerManager.saveSMS(sms);
                        sms.setId(id);
                        adapter.add(0, sms);
                    }
                    br.close();

                    activity.showTipInThread(R.string.menu_import_sms_tip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (activity != null) {
            activity.setOnMenuItemClickListener(
                    isVisibleToUser ? this : null,
                    isVisibleToUser ? SettingsActivity.SHOW_EXPORT | SettingsActivity.SHOW_IMPORT : SettingsActivity.SHOW_NONE
            );
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sms;
    }
}
