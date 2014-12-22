package com.haoutil.xposed.haoblocker.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;

import com.haoutil.xposed.haoblocker.R;

public abstract class BaseFragment extends Fragment {

    private AlertDialog discardConfirm;

    public abstract void onResetActionBarButtons(boolean isMenuOpen);

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
