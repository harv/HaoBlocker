package com.haoutil.xposed.haoblocker.presenter.impl;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.AppContext;
import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.SMSModel;
import com.haoutil.xposed.haoblocker.model.impl.SMSModelImpl;
import com.haoutil.xposed.haoblocker.model.entity.SMS;
import com.haoutil.xposed.haoblocker.presenter.SMSPresenter;
import com.haoutil.xposed.haoblocker.ui.SMSView;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.ui.adapter.SMSAdapter;
import com.haoutil.xposed.haoblocker.util.ThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SMSPresenterImpl implements SMSPresenter {
    private SMSModel mSMSModel;
    private SMSView mSMSView;

    private SMSAdapter mAdapter;

    private int mPositionDeleted = -1;
    private SMS mSmsDeleted = null;

    public SMSPresenterImpl(SMSView mSMSView) {
        this.mSMSView = mSMSView;
        mSMSModel = new SMSModelImpl();
        mSMSModel.readAllSMS();
    }

    @Override
    public void setListItems() {
        Context context = AppContext.getsInstance().getApplicationContext();
        List<SMS> smses = mSMSModel.getSMSes(-1);
        BaseRecycleAdapter.OnItemClick onItemClick = mSMSView.getOnItemClick();
        mAdapter = new SMSAdapter(context, smses, onItemClick);
        mSMSView.setSMSAdapter(mAdapter);
    }

    @Override
    public void setMenuItems(Menu menu) {
        mSMSView.setMenuItems(menu);
    }

    @Override
    public void deleteSMS(int position) {
        mPositionDeleted = position;
        mSMSView.showConfirm(R.string.discard_dialog_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSmsDeleted = mAdapter.getItem(mPositionDeleted);
                mSMSModel.deleteSMS(mSmsDeleted);
                mAdapter.remove(mPositionDeleted);
                mSMSView.showTip(R.string.rule_tip_sms_deleted, true);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPositionDeleted = -1;
                mSmsDeleted = null;
            }
        });
    }

    @Override
    public void restoreSMS() {
        if (-1 != mPositionDeleted && null != mSmsDeleted) {
            long newId = mSMSModel.restoreSMS(mSmsDeleted);
            mSmsDeleted.setId(newId);
            mAdapter.add(mPositionDeleted, mSmsDeleted);

            mPositionDeleted = -1;
            mSmsDeleted = null;
        }
    }

    @Override
    public void importSMSes() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_sms.csv");
                    if (!file.exists() || !file.isFile()) {
                        mSMSView.showTip(R.string.menu_restore_sms_miss_tip, false);
                        return;
                    }
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split(",");
                        long id = Long.valueOf(columns[0]);
                        String sender = columns[1];
                        String content = columns[2];
                        content = content.substring(1, content.length() - 1).replaceAll("\"\"", "\"");
                        long created = Long.valueOf(columns[3]);
                        int read = Integer.valueOf(columns[4]);

                        final SMS sms = new SMS();
                        sms.setId(id);
                        sms.setSender(sender);
                        sms.setContent(content);
                        sms.setCreated(created);
                        sms.setRead(read);

                        id = mSMSModel.saveSMS(sms);
                        if (id != -1) {
                            sms.setId(id);
                            mAdapter.add(0, sms);
                        }
                    }
                    br.close();

                    mSMSView.showTip(R.string.menu_restore_sms_tip, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void exportSMSes() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_sms.csv");
                    OutputStream os = new FileOutputStream(file);

                    List<SMS> smses = mSMSModel.getSMSes(-1);
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

                    mSMSView.showTip(R.string.menu_backup_sms_tip, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
