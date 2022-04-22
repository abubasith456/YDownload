package com.example.ydownload.ui;

import android.text.util.Linkify;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ydownload.R;
import com.example.ydownload.listener.BaseListener;

public class BaseFragment extends Fragment implements BaseListener {

    MaterialDialog progressDialog;
    MaterialDialog confirmDialog;
    MaterialDialog alertDialog;
    MaterialDialog errorDialog;

    @Override
    public void onStartProgress(String message) {
        progressDialog = new MaterialDialog.Builder(getActivity())
                .content(message)
                .contentColor(getResources().getColor(R.color.black))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .show();
    }

    @Override
    public void onStartProgress(String message, int id) {
        progressDialog = new MaterialDialog.Builder(getActivity())
                .content(message)
                .contentColor(getResources().getColor(R.color.black))
                .cancelable(false)
                .widgetColor(getResources().getColor(id))
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .show();
    }

    @Override
    public void onStopProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onAlertDialog(String title, String message, String buttonText, MaterialDialog.SingleButtonCallback callback) {
        try {
            closeOpenedDialogs();
            alertDialog = new MaterialDialog.Builder(getActivity())
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .title(title)
                    .content(message)
                    .contentColor(getResources().getColor(R.color.black))
                    .canceledOnTouchOutside(false)
                    .positiveColor(getResources().getColor(R.color.black))
                    .positiveText(buttonText)
                    .onPositive(callback)
                    .build();
            alertDialog.getTitleView().setTextSize(16);
            alertDialog.getContentView().setTextSize(14);
            alertDialog.show();
        } catch (Exception err) {
            Log.e("BaseFragment:onAlert", err.getMessage());
        }
    }

    @Override
    public void onError(String title, String message) {
        try {
            closeOpenedDialogs();
            errorDialog = new MaterialDialog.Builder(getActivity())
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .title(title)
                    .content(message)
                    .contentColor(getResources().getColor(R.color.black))
                    .canceledOnTouchOutside(false)
                    .positiveColor(getResources().getColor(R.color.black))
                    .positiveText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .build();
            errorDialog.getTitleView().setTextSize(16);
            errorDialog.getContentView().setTextSize(14);
            errorDialog.show();
        } catch (Exception err) {
            Log.e("BaseFragment:onError", err.getMessage());
        }
    }

    @Override
    public void onConfirm(String title, String message, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback positiveCallback, MaterialDialog.SingleButtonCallback negativeCallback) {
        try {
            closeOpenedDialogs();
            confirmDialog = new MaterialDialog.Builder(getActivity())
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .title(title)
                    .content(message)
                    .contentColor(getResources().getColor(R.color.black))
                    .canceledOnTouchOutside(false)
                    .positiveColor(getResources().getColor(R.color.black))
                    .negativeColor(getResources().getColor(R.color.black))
                    .positiveText(positiveText)
                    .negativeText(negativeText)
                    .onPositive(positiveCallback)
                    .onNegative(negativeCallback)
                    .build();
            confirmDialog.getTitleView().setTextSize(16);
            confirmDialog.getContentView().setTextSize(14);
            confirmDialog.show();
        } catch (Exception err) {
            Log.e("BaseFragment:OnError", err.getMessage());
        }
    }

    @Override
    public void closeOpenedDialogs() {
        if (confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }
        onStopProgress();
    }
}
