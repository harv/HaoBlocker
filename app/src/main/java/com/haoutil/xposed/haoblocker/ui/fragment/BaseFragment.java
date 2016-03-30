package com.haoutil.xposed.haoblocker.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;

public abstract class BaseFragment extends Fragment {
    private AlertDialog discardConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(getLayoutResource(), container, false);
    }

    public void confirm(final DialogInterface.OnClickListener positiveOnClickListener, final DialogInterface.OnClickListener negativeOnClickListener) {
        if (discardConfirm == null) {
            discardConfirm = new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getResources().getString(R.string.discard_dialog_title))
                    .setMessage(getActivity().getResources().getString(R.string.discard_dialog_message))
                    .setPositiveButton(R.string.discard_dialog_button_ok, positiveOnClickListener)
                    .setNegativeButton(R.string.discard_dialog_button_cancel, negativeOnClickListener)
                    .create();
        }
        discardConfirm.show();
    }

    protected abstract int getLayoutResource();
}
