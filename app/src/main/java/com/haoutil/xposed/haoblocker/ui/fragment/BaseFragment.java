package com.haoutil.xposed.haoblocker.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.ui.BaseView;

public abstract class BaseFragment extends Fragment implements BaseView {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(getLayoutResource(), container, false);
    }
}
