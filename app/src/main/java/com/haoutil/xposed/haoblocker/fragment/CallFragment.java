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
import com.haoutil.xposed.haoblocker.adapter.CallAdapter;
import com.haoutil.xposed.haoblocker.model.Call;
import com.haoutil.xposed.haoblocker.util.BlockerManager;

import java.util.List;

public class CallFragment extends BaseFragment implements BaseRecycleAdapter.OnItemClick, View.OnClickListener, DialogInterface.OnClickListener {
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
    protected int getLayoutResource() {
        return R.layout.fragment_call;
    }
}
