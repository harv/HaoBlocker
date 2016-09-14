package com.haoutil.xposed.haoblocker.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseRecycleAdapter.ViewHolder> {
    protected Context context;
    protected List<T> data;
    private OnItemClick onItemClick;

    public BaseRecycleAdapter(Context context, List<T> data, OnItemClick onItemClick) {
        this.context = context;
        this.data = data == null ? new ArrayList<T>() : data;
        this.onItemClick = onItemClick;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(getItemResource(), parent, false), onItemClick);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        onBindItemViewHolder(holder, position);
    }

    public abstract int getItemResource();

    public abstract void onBindItemViewHolder(BaseRecycleAdapter.ViewHolder holder, int position);

    @SuppressWarnings("unused")
    public void add(int index, T elem) {
        data.add(index, elem);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    public void addAll(List<T> elem) {
        data.addAll(elem);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    public void remove(T elem) {
        data.remove(elem);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    public void remove(int index) {
        data.remove(index);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    public void replace(int index, T elem) {
        data.set(index, elem);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        data.clear();
        data.addAll(elem);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private SparseArray<View> views = new SparseArray<>();
        private View convertView;
        private OnItemClick onItemClick;

        public ViewHolder(View itemView, OnItemClick onItemClick) {
            super(itemView);
            convertView = itemView;
            convertView.setOnClickListener(this);
            convertView.setOnLongClickListener(this);

            this.onItemClick = onItemClick;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int resId) {
            View v = views.get(resId);
            if (null == v) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }

        @Override
        public void onClick(View v) {
            onItemClick.onItemClick(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onItemClick.onItemLongClick(getLayoutPosition());
            return true;
        }
    }

    public interface OnItemClick {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }
}
