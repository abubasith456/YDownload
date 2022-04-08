package com.example.ydownload.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

public final class Utils {

    private static Utils instance;

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }
    /**
     * Hides soft keyboard.
     * Developed by Abu basith
     *
     * @param activity Activity for getting IMS and current focus.
     */
    public  void hideSoftKeyboard(@NonNull Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception exception) {
            Log.e("Error ==> ", "" + exception);
        }
    }

    /**
     * Show soft keyboard.
     * Developed by Abu basith
     *
     * @param activity Activity for getting IMS and current focus.
     */
    public  void showSoftKeyboard(@NonNull Activity activity, @NonNull EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public  boolean isNetworkConnectionAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) return false;
            NetworkInfo.State network = info.getState();
            return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
        } catch (Exception exception) {
            Log.e("Error ==> ", "" + exception);
        }
        return false;
    }
}
