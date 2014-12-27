package com.haoutil.xposed.haoblocker.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

public class RuleFragment extends BaseFragment {
    private DbManager dbManager;

    private RuleAdapter adapter;

    @InjectView(R.id.cb_check_all)
    CheckBox cb_check_all;
    @InjectView(R.id.lv_rules)
    ListView lv_rules;

    private boolean showDiscardAction = false;

    private MenuItem action_new;
    private MenuItem action_discard;

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = getView();

        adapter = new RuleAdapter(getActivity().getLayoutInflater(), dbManager.getRules(DbManager.TYPE_ALL));
        lv_rules.setAdapter(adapter);

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
        if (action_new == null || action_discard == null) {
            return;
        }

        if (isMenuOpen) {
            action_new.setVisible(false);
            action_discard.setVisible(false);
        } else {
            action_new.setVisible(true);
            action_discard.setVisible(showDiscardAction);
        }
    }

    public void onEvent(RuleUpdateEvent event) {
        switch (event.getWhat()) {
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
}
