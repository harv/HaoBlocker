package com.haoutil.xposed.haoblocker.fragment;

import android.app.Activity;
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
import android.widget.Toast;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.activity.RuleActivity;
import com.haoutil.xposed.haoblocker.adapter.RuleAdapter;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.util.DbManager;

public class RuleFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private DbManager dbManager;

    private RuleAdapter adapter;

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

        menu.findItem(R.id.action_new).setVisible(true);
        menu.findItem(R.id.action_discard).setVisible(showDiscardAction);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(getActivity(), RuleActivity.class);
                intent.putExtra("operation", "add");
                startActivityForResult(intent, 0);
                break;
            case R.id.action_discard:
                dbManager.deleteRule(adapter.getCheckedRules());

                adapter.clearChecked();
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rule, container, false);

        cb_check_all = (CheckBox) view.findViewById(R.id.cb_check_all);
        cb_check_all.setOnClickListener(this);  // do not use setOnCheckedChangeListener, because it will trigger checkAll method of RuleAdapter

        ListView lv_rules = (ListView) view.findViewById(R.id.lv_rules);
        adapter = new RuleAdapter(getActivity().getLayoutInflater(), mHandler, dbManager.getRules(DbManager.TYPE_ALL));
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
        Rule rule = (Rule) adapter.getItem(position);

        Intent intent = new Intent(getActivity(), RuleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("operation", "modify");
        bundle.putInt("position", position);
        bundle.putSerializable("rule", rule);
        intent.putExtras(bundle);

        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Rule rule = (Rule) bundle.get("rule");
            int position = bundle.getInt("position");
            adapter.addItem(rule, position);
            adapter.notifyDataSetChanged();

            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.rule_tip_rule_added), Toast.LENGTH_SHORT).show();
        }
    }
}
