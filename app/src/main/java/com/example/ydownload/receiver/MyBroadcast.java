package com.example.ydownload.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ydownload.R;
import com.example.ydownload.messages.NetworkMessage;
import com.example.ydownload.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

public class MyBroadcast extends BroadcastReceiver {

    MaterialDialog materialDialog;
    static Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utils.getInstance().isNetworkConnectionAvailable(context)) {
            EventBus.getDefault().postSticky(new NetworkMessage(true));
            Log.e("isNetworkConnection", "Connected");
        } else {
            EventBus.getDefault().postSticky(new NetworkMessage(false));
            Log.e("isNetworkConnection", "Disconnected");
        }
    }

//    public boolean isOnline(Context context) {
//        Log.e("isOnline", "Called");
//        if (Utils.getInstance().isNetworkConnectionAvailable(context)) {
//            Log.e("isOnline", "true");
//            return true;
//
//        } else {
//            Log.e("isOnline", "false");
//            return false;
//        }
//    }
}
