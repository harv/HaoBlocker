package com.haoutil.xposed.haoblocker.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoutil.xposed.haoblocker.R;
import com.haoutil.xposed.haoblocker.model.Import;
import com.haoutil.xposed.haoblocker.util.DbManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ImportAdapter extends BaseAdapter {
    private Activity activity;
    private List<Import> list;
    private List<Import> selectedItems;
    private List<String> selectedContacts;

    private Map<String, String> contacts;

    private SimpleDateFormat simpleDateFormat;

    public ImportAdapter(Activity activity, List<Import> list) {
        this.activity = activity;
        this.list = list;
        this.selectedItems = new ArrayList<>();
        this.selectedContacts = new ArrayList<>();
        this.contacts = new HashMap<>();

        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return -1;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View view;
        if (contentView != null) {
            view = contentView;
        } else {
            view = activity.getLayoutInflater().inflate(R.layout.rule_import_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder(view);
        }

        Import item = list.get(position);
        if (item != null) {
            if (item.isSelected()) {
                view.setBackgroundColor(view.getResources().getColor(R.color.textColorDividers));
            } else {
                view.setBackgroundColor(view.getResources().getColor(R.color.textColorPrimary));
            }
            String number = item.getNumber();
            String name = item.getName();
            if (item.getType() == DbManager.TYPE_SMS && !item.isQueried() && TextUtils.isEmpty(name)) {
                if (contacts.containsKey(number)) {
                    item.setName(contacts.get(number));
                } else {
                    Cursor cursor = activity.getContentResolver().query(
                            Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)),
                            new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                            ContactsContract.PhoneLookup.NUMBER + "='" + number + "'",
                            null,
                            null
                    );
                    if (cursor != null && cursor.moveToFirst()) {
                        item.setName(cursor.getString(0));
                        name = item.getName();

                        contacts.put(number, item.getName());
                    } else {
                        contacts.put(number, null);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                item.setQueried(true);
            }

            if (!TextUtils.isEmpty(name)) {
                name = "(" + name + ")";
            } else {
                name = "";
            }
            holder.tv_contact.setText(item.getNumber() + name);
            holder.tv_time.setText(simpleDateFormat.format(new Date(item.getTime())));
            if (!TextUtils.isEmpty(item.getContent())) {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(item.getContent());
            } else {
                holder.tv_content.setVisibility(View.GONE);
            }
        }

        return view;
    }

    public void selectItem(int position) {
        Import item = (Import) getItem(position);
        if (item != null) {
            item.setSelected(!item.isSelected());
            if (item.isSelected()) {
                if (!selectedContacts.contains(item.getNumber())) {
                    selectedContacts.add(item.getNumber());
                    selectedItems.add(item);
                }
            } else {
                if (selectedContacts.contains(item.getNumber())) {
                    selectedContacts.remove(item.getNumber());
                    selectedItems.remove(item);
                }
            }
        }
    }

    public List<Import> getSelectedItems() {
        return selectedItems;
    }

    class ItemViewHolder {
        @InjectView(R.id.tv_contact)
        TextView tv_contact;
        @InjectView(R.id.tv_time)
        TextView tv_time;
        @InjectView(R.id.tv_content)
        TextView tv_content;

        public ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
