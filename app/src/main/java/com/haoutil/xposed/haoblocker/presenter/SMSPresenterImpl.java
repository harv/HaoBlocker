package com.haoutil.xposed.haoblocker.presenter;

import android.content.Context;
import android.os.Environment;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.SMSModel;
import com.haoutil.xposed.haoblocker.model.SMSModelImpl;
import com.haoutil.xposed.haoblocker.model.entity.SMS;
import com.haoutil.xposed.haoblocker.ui.SMSView;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.ui.adapter.SMSAdapter;

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

    private SMSAdapter adapter;

    private int positionDeleted = -1;
    private SMS smsDeleted = null;

    public SMSPresenterImpl(SMSView mSMSView) {
        this.mSMSView = mSMSView;
        mSMSModel = new SMSModelImpl();
    }

    @Override
    public void init() {
        Context context = mSMSView.getApplicationContext();
        mSMSModel.init(context);
    }

    @Override
    public void setListItems() {
        Context context = mSMSView.getApplicationContext();
        List<SMS> smses = mSMSModel.getSMSes(-1);
        BaseRecycleAdapter.OnItemClick onItemClick = mSMSView.getOnItemClick();
        adapter = new SMSAdapter(context, smses, onItemClick);
        mSMSView.setSMSAdapter(adapter);
    }

    @Override
    public void setMenuItems(Menu menu) {
        mSMSView.setMenuItems(menu);
    }

    @Override
    public void deleteSMSConfirm(int position) {
        positionDeleted = position;
        mSMSView.showConfirm();
    }

    @Override
    public void deleteSMS() {
        smsDeleted = adapter.getItem(positionDeleted);
        mSMSModel.deleteSMS(smsDeleted);
        adapter.remove(positionDeleted);
        mSMSView.showTip(R.string.rule_tip_sms_deleted, true);
    }

    @Override
    public void deleteSMSCancel() {
        positionDeleted = -1;
    }

    @Override
    public void restoreSMS() {
        if (-1 != positionDeleted && null != smsDeleted) {
            long newId = mSMSModel.restoreSMS(smsDeleted);
            smsDeleted.setId(newId);
            adapter.add(positionDeleted, smsDeleted);

            positionDeleted = -1;
            smsDeleted = null;
        }
    }

    @Override
    public void importSMSes() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "blocker_sms.csv");
            if (!file.exists() || !file.isFile()) {
                mSMSView.showTip(R.string.menu_import_sms_miss_tip, false);
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

                long id = mSMSModel.saveSMS(sms);
                sms.setId(id);
                adapter.add(0, sms);
            }
            br.close();

            mSMSView.showTip(R.string.menu_import_sms_tip, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportSMSes() {
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

            mSMSView.showTip(R.string.menu_export_sms_tip, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
