package com.haoutil.xposed.haoblocker.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoutil.xposed.haoblocker.R;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    private View view;

    private AlertDialog discardConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.inject(this, view);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    protected abstract int getLayoutResource();

    public abstract void onResetActionBarButtons(boolean isMenuOpen);

    public View getView() {
        return view;
    }

    public void confirm(final DialogInterface.OnClickListener positiveOnClickListener, final DialogInterface.OnClickListener negativeOnClickListener) {
        if (discardConfirm == null) {
            discardConfirm = new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getResources().getString(R.string.discard_dialog_title))
                    .setMessage(getActivity().getResources().getString(R.string.discard_dialog_message))
                    .setPositiveButton(R.string.discard_dialog_button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (positiveOnClickListener != null) {
                                positiveOnClickListener.onClick(dialogInterface, i);
                            }
                        }
                    })
                    .setNegativeButton(R.string.discard_dialog_button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (negativeOnClickListener != null) {
                                negativeOnClickListener.onClick(dialogInterface, i);
                            }
                            discardConfirm.hide();
                        }
                    })
                    .create();
        }
        discardConfirm.show();
    }

    public void setColorSchemeResources(SwipeRefreshLayout layout) {
        layout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
    }
}
