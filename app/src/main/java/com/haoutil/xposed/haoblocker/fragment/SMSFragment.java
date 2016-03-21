package com.haoutil.xposed.haoblocker.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.activity.SettingsActivity;
import com.haoutil.xposed.haoblocker.adapter.BaseRecycleAdapter;
import com.haoutil.xposed.haoblocker.adapter.SMSAdapter;
import com.haoutil.xposed.haoblocker.model.SMS;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.util.List;

public class SMSFragment extends BaseFragment implements BaseRecycleAdapter.OnItemClick, View.OnClickListener, DialogInterface.OnClickListener {
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
    protected int getLayoutResource() {
        return R.layout.fragment_sms;
    }
}
