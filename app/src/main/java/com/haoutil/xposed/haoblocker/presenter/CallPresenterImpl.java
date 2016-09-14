package com.haoutil.xposed.haoblocker.presenter;

import android.content.Context;
import android.os.Environment;
import android.view.Menu;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.CallModel;
import com.haoutil.xposed.haoblocker.model.CallModelImpl;
import com.haoutil.xposed.haoblocker.model.entity.Call;
import com.haoutil.xposed.haoblocker.ui.CallView;
import com.haoutil.xposed.haoblocker.ui.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.ui.adapter.CallAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CallPresenterImpl implements CallPresenter {
    private CallModel mCallModel;
    private CallView mCallView;

    private CallAdapter adapter;

    private int positionDeleted = -1;
    private Call callDeleted = null;

    public CallPresenterImpl(CallView mCallView) {
        this.mCallView = mCallView;
        mCallModel = new CallModelImpl();
    }

    @Override
    public void init() {
        Context context = mCallView.getApplicationContext();
        mCallModel.init(context);
    }

    @Override
    public void setListItems() {
        Context context = mCallView.getApplicationContext();
        List<Call> calls = mCallModel.getCalls(-1);
        BaseRecycleAdapter.OnItemClick onItemClick = mCallView.getOnItemClick();
        adapter = new CallAdapter(context, calls, onItemClick);
        mCallView.setCallAdapter(adapter);
    }

    @Override
    public void setMenuItems(Menu menu) {
        mCallView.setMenuItems(menu);
    }

    @Override
    public void deleteCallConfirm(int position) {
        positionDeleted = position;
        mCallView.showConfirm();
    }

    @Override
    public void deleteCall() {
        callDeleted = adapter.getItem(positionDeleted);
        mCallModel.deleteCall(callDeleted);
        adapter.remove(positionDeleted);
        mCallView.showTip(R.string.rule_tip_call_deleted, true);
    }

    @Override
    public void deleteCallCancel() {
        positionDeleted = -1;
    }

    @Override
    public void restoreCall() {
        if (-1 != positionDeleted && null != callDeleted) {
            long newId = mCallModel.restoreCall(callDeleted);
            callDeleted.setId(newId);
            adapter.add(positionDeleted, callDeleted);

            positionDeleted = -1;
            callDeleted = null;
        }
    }

    @Override
    public void importCalls() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_call.csv");
                    if (!file.exists() || !file.isFile()) {
                        mCallView.showTip(R.string.menu_import_call_miss_tip, false);
                        return;
                    }
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split(",");
                        long id = Long.valueOf(columns[0]);
                        String caller = columns[1];
                        long created = Long.valueOf(columns[2]);
                        int read = Integer.valueOf(columns[3]);

                        final Call call = new Call();
                        call.setId(id);
                        call.setCaller(caller);
                        call.setCreated(created);
                        call.setRead(read);

                        id = mCallModel.saveCall(call);
                        if (id != -1) {
                            call.setId(id);
                            adapter.add(0, call);
                        }
                    }
                    br.close();

                    mCallView.showTip(R.string.menu_import_call_tip, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void exportCalls() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_call.csv");
                    OutputStream os = new FileOutputStream(file);

                    List<Call> calls = mCallModel.getCalls(-1);
                    StringBuilder sb = new StringBuilder();
                    for (int i = calls.size(); i > 0; i--) {
                        Call call = calls.get(i - 1);
                        sb.append(call.getId());
                        sb.append(",").append(call.getCaller());
                        sb.append(",").append(call.getCreated());
                        sb.append(",").append(call.getRead());
                        sb.append("\n");
                    }
                    byte[] bs = sb.toString().getBytes();
                    os.write(bs, 0, bs.length);
                    os.flush();
                    os.close();

                    mCallView.showTip(R.string.menu_export_call_tip, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
