package com.example.ydownload.listener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public interface BaseListener {

    void onStartProgress(String message);

    void onStartProgress(String message, int id);

    void onStopProgress();

    void onAlertDialog(String title, String message, String buttonText, MaterialDialog.SingleButtonCallback callback);

    void onError(String title, String message);

    void onConfirm(String title, String message, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback positiveCallback, MaterialDialog.SingleButtonCallback negativeCallback);

    void closeOpenedDialogs();

}
