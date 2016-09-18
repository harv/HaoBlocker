package com.haoutil.xposed.haoblocker.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseRecycleAdapter.ViewHolder> {
    private Handler mHandler = new Handler();
    private Thread mUiThread = Thread.currentThread();

    protected Context mContext;
    protected List<T> mData;
    private OnItemClick mOnItemClick;

    public BaseRecycleAdapter(Context context, List<T> data, OnItemClick onItemClick) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<T>() : data;
        this.mOnItemClick = onItemClick;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(getItemResource(), parent, false), mOnItemClick);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        onBindItemViewHolder(holder, position);
    }

    public abstract int getItemResource();

    public abstract void onBindItemViewHolder(BaseRecycleAdapter.ViewHolder holder, int position);

    @SuppressWarnings("unused")
    public void add(int index, T elem) {
        mData.add(index, elem);
        notifyChanged();
    }

    @SuppressWarnings("unused")
    public void addAll(List<T> elem) {
        mData.addAll(elem);
        notifyChanged();
    }

    @SuppressWarnings("unused")
    public void remove(T elem) {
        mData.remove(elem);
        notifyChanged();
    }

    @SuppressWarnings("unused")
    public void remove(int index) {
        mData.remove(index);
        notifyChanged();
    }

    @SuppressWarnings("unused")
    public void replace(int index, T elem) {
        mData.set(index, elem);
        notifyChanged();
    }

    @SuppressWarnings("unused")
    public void replaceAll(List<T> elem) {
        mData.clear();
        mData.addAll(elem);
        notifyChanged();
    }

    public void notifyChanged() {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        } else {
            notifyDataSetChanged();
        }
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
