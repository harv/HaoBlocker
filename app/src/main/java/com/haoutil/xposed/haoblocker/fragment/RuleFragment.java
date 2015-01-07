package com.haoutil.xposed.haoblocker.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.haoutil.xposed.haoblocker.event.RuleUpdateEvent;
import com.haoutil.xposed.haoblocker.model.Rule;
import com.haoutil.xposed.haoblocker.util.DbManager;
import com.haoutil.xposed.haoblocker.widget.ImportDialog;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

public class RuleFragment extends BaseFragment {
    private DbManager dbManager;

    private RuleAdapter adapter;

    private LayoutInflater inflater;

    @InjectView(R.id.cb_check_all)
    CheckBox cb_check_all;
    @InjectView(R.id.lv_rules)
    ListView lv_rules;

    private boolean showDiscardAction = false;

    private MenuItem action_new;
    private MenuItem action_discard;
    private MenuItem action_import_call;
    private MenuItem action_import_message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dbManager = new DbManager(getActivity());

        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_actions, menu);

        action_new = menu.findItem(R.id.action_new);
        action_new.setVisible(true);
        action_discard = menu.findItem(R.id.action_discard);
        action_discard.setVisible(showDiscardAction);
        action_import_call = menu.findItem(R.id.action_import_call);
        action_import_call.setVisible(true);
        action_import_message = menu.findItem(R.id.action_import_message);
        action_import_message.setVisible(true);

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
                this.confirm(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbManager.deleteRule(adapter.getCheckedRules());

                        adapter.clearChecked();
                        adapter.notifyDataSetChanged();
                    }
                }, null);
                break;
            case R.id.action_import_call:
                new ImportDialog(getActivity(), DbManager.TYPE_CALL).show();
                break;
            case R.id.action_import_message:
                new ImportDialog(getActivity(), DbManager.TYPE_SMS).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = getView();

        this.inflater = inflater;

        new LoadRuleAdapterTask().execute();

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_rule;
    }

    @OnClick(R.id.cb_check_all)
    public void onClick(View view) {
        switch (view.getId()) {
            // do not use setOnCheckedChangeListener, because it will trigger checkAll method of RuleAdapter
            case R.id.cb_check_all:
                boolean b = ((CheckBox) view).isChecked();

                adapter.checkAll(b);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @OnItemClick(R.id.lv_rules)
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

    @Override
    public void onResetActionBarButtons(boolean isMenuOpen) {
        if (action_new == null || action_discard == null || action_import_call == null || action_import_message == null) {
            return;
        }

        if (isMenuOpen) {
            action_new.setVisible(false);
            action_discard.setVisible(false);
            action_import_call.setVisible(false);
            action_import_message.setVisible(false);
        } else {
            action_new.setVisible(true);
            action_discard.setVisible(showDiscardAction);
            action_import_call.setVisible(true);
            action_import_message.setVisible(true);
        }
    }

    public void onEventMainThread(RuleUpdateEvent event) {
        switch (event.getEvent()) {
            case RuleUpdateEvent.EVENT_HIDE_DISCARD:
                showDiscardAction = false;
                getActivity().invalidateOptionsMenu();
                break;
            case RuleUpdateEvent.EVENT_SHOW_DISCARD:
                showDiscardAction = true;
                getActivity().invalidateOptionsMenu();
                break;
            case RuleUpdateEvent.EVENT_CHECK_NONE:
                cb_check_all.setChecked(false);
                break;
            case RuleUpdateEvent.EVENT_CHECK_ALL:
                cb_check_all.setChecked(true);
                break;
            case RuleUpdateEvent.EVENT_REFRESH_LIST:
                new LoadRuleAdapterTask().execute();
                break;
        }
    }

    private class LoadRuleAdapterTask extends AsyncTask<Void, Void, List<Rule>> {
        @Override
        protected List<Rule> doInBackground(Void... params) {
            return dbManager.getRules(DbManager.TYPE_ALL);
        }

        @Override
        protected void onPostExecute(List<Rule> list) {
            adapter = new RuleAdapter(inflater, list);
            lv_rules.setAdapter(adapter);
        }
    }
}
