package com.haoutil.xposed.haoblocker.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.adapter.CallAdapter;
import com.haoutil.xposed.haoblocker.model.Call;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CallFragment extends BaseFragment implements BaseRecycleAdapter.OnItemClick, View.OnClickListener, DialogInterface.OnClickListener, SettingsActivity.OnMenuItemClickListener {
    private SettingsActivity activity;
    private BlockerManager blockerManager;

    private RecyclerView rv_call;
    private CallAdapter adapter;

    private int positionDeleted = -1;
    private Call callDeleted = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
        blockerManager = new BlockerManager(activity);
        blockerManager.readAllCall();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            rv_call = (RecyclerView) view.findViewById(R.id.rv_call);
            rv_call.setLayoutManager(new LinearLayoutManager(activity));

            List<Call> calls = blockerManager.getCalls(-1);
            adapter = new CallAdapter(activity, calls, CallFragment.this);
            rv_call.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.export).setVisible(true);
        menu.findItem(R.id.import0).setVisible(true);
        activity.setOnMenuItemClickListener(this);
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
        if (-1 != positionDeleted && null != callDeleted) {
            long newId = blockerManager.restoreCall(callDeleted);
            callDeleted.setId(newId);
            adapter.add(positionDeleted, callDeleted);

            positionDeleted = -1;
            callDeleted = null;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                callDeleted = adapter.getItem(positionDeleted);
                blockerManager.deleteCall(callDeleted);
                adapter.remove(positionDeleted);
                activity.showTip(R.string.rule_tip_call_deleted, CallFragment.this);
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
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_call.csv");
                    OutputStream os = new FileOutputStream(file);

                    List<Call> calls = blockerManager.getCalls(-1);
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

                    activity.showTipInThread(R.string.menu_export_call_tip);
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
                    File file = new File(Environment.getExternalStorageDirectory(), "blocker_call.csv");
                    if (!file.exists() || !file.isFile()) {
                        activity.showTipInThread(R.string.menu_import_call_miss_tip);
                        return;
                    }
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split(",");
                        String caller = columns[1];
                        long created = Long.valueOf(columns[2]);
                        int read = Integer.valueOf(columns[3]);

                        Call call = new Call();
                        call.setCaller(caller);
                        call.setCreated(created);
                        call.setRead(read);

                        long id = blockerManager.saveCall(call);
                        call.setId(id);
                        adapter.add(0, call);
                    }
                    br.close();

                    activity.showTipInThread(R.string.menu_import_call_tip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_call;
    }
}
