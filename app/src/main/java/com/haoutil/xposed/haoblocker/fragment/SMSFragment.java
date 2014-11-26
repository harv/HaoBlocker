package com.haoutil.xposed.haoblocker.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.activity.SMSActivity;
import com.haoutil.xposed.haoblocker.adapter.SMSAdaptor;
import com.haoutil.xposed.haoblocker.model.SMS;
import com.haoutil.xposed.haoblocker.util.DbManager;

public class SMSFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private DbManager dbManager;

    private SMSAdaptor adapter;

    private CheckBox cb_check_all;

    private boolean showDiscardAction = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showDiscardAction = false;
                    getActivity().invalidateOptionsMenu();
                    break;
                case 1:
                    showDiscardAction = true;
                    getActivity().invalidateOptionsMenu();
                    break;
                case 2:
                    cb_check_all.setChecked(false);
                    break;
                case 3:
                    cb_check_all.setChecked(true);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dbManager = new DbManager(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_actions, menu);

        menu.findItem(R.id.action_discard).setVisible(showDiscardAction);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_discard:
                dbManager.deleteSMS(adapter.getCheckedSMSes());

                adapter.clearChecked();
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms, container, false);

        cb_check_all = (CheckBox) view.findViewById(R.id.cb_check_all);
        cb_check_all.setOnClickListener(this);  // setOnCheckedChangeListener is conflict with item's checkbox's

        ListView lv_rules = (ListView) view.findViewById(R.id.lv_rules);
        adapter = new SMSAdaptor(getActivity().getLayoutInflater(), mHandler, dbManager.getSMSes());
        lv_rules.setAdapter(adapter);
        lv_rules.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_check_all:
                boolean b = ((CheckBox) view).isChecked();

                adapter.checkAll(b);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SMS sms = (SMS) adapter.getItem(position);

        if (sms.getRead() == SMS.SMS_UNREADED) {
            sms.setRead(SMS.SMS_READED);
            dbManager.setRead(sms);
            adapter.notifyDataSetChanged();
        }

        Intent intent = new Intent(getActivity(), SMSActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sms", sms);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
