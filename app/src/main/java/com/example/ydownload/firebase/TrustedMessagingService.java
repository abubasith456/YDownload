package com.example.ydownload.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ydownload.DashboardActivity;
import com.example.ydownload.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Abu on 22/04/22.
 */

public class TrustedMessagingService extends FirebaseMessagingService {
    private static final String TAG = "TrustedMessagingService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {

            if (remoteMessage != null && remoteMessage.getData() != null && remoteMessage.getData().containsKey("message")) {
                if (remoteMessage.getData() != null) {
                    String message = remoteMessage.getData().get("message");
                    String[] contents = message.split("\\|");
                    if (contents.length == 2) {
                        //check silent notification for Login user cancel
//                        if (contents[0].trim().equals(INTERRUPT_LOGIN)) {
//                            HandlerFactory.getInstance().setTransactionId(contents[1].trim());
//                            return;
//                        }
                    }
                    sendNotification(message);
                }
            }
        } catch (Exception e) {
            Log.e("onMessageReceived", e.getMessage());
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param message FCM message body received.
     */
    private void sendNotification(String message) {
        try {
            if (message != null) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationChannel mChannel = new NotificationChannel("0", getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                    mChannel.setDescription(message);
                    mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
                    mChannel.setLightColor(Color.WHITE);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "0")

                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[]{100, 200, 100, 500});

                notificationBuilder.setSmallIcon(R.drawable.player);

                showHeadsUpNotification(message, notificationBuilder, notificationManager);
            }
        } catch (Exception err) {
        }
    }

    private void showHeadsUpNotification(String message, NotificationCompat.Builder notificationBuilder, NotificationManager notificationManager) {

        String[] contents = message.split("\\|");
        if (contents.length == 2) {
            notificationBuilder.setContentText(contents[0].trim());
        } else {
            notificationBuilder.setContentText(message);
        }

        Intent notificationIntent = new Intent(this, DashboardActivity.class);
        notificationIntent.putExtra("isFromNotification", true);

        if (contents.length == 2) {
            notificationIntent.putExtra("userIdFromNotification", contents[1].trim());
        }

        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        if(HandlerFactory.getInstance().isShouldShowNotification()) {
//            PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            notificationBuilder.setContentIntent(pIntent);
//        } else {
//            notificationBuilder.setAutoCancel(true);
//            notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));
//        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
//        MainViewModel.MainBaseListener listener = TrustedMessageSignApplication.getInstance(getBaseContext()).getMainBaseListener();
//        if (listener != null) {
//            listener.onRefreshToken(token);
//        }
    }
}
