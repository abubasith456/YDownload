package com.example.ydownload;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

//import com.afollestad.materialdialogs.DialogAction;
//import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ydownload.databinding.ActivitySplashBinding;
import com.example.ydownload.messages.NetworkMessage;
import com.example.ydownload.receiver.MyBroadcast;
import com.example.ydownload.services.MyService;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import javax.crypto.SecretKey;

public class SplashScreen extends BaseActivity {

    private ActivitySplashBinding activitySplashBinding;
    private final Sprite circle = new ThreeBounce();
    private final Handler handler = new Handler();
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseFirestore firebaseFirestore;
    private String featureMessage, updateURL, version;
    SecretKey secretKey;
//    MaterialDialog alertDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        firebaseFirestore = FirebaseFirestore.getInstance();
        activitySplashBinding.spinKit.setIndeterminateDrawable(circle);

//        runCodeOneTimeOnly();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getDeviceInfo();
                getFeatureMessage();
            }
        });

        handler.postDelayed(() -> {
            try {
                if (version != null) {
                    if (Integer.parseInt(version) > getCurrentVersionCode()) {
                        if (featureMessage.equals("")) {
                            featureMessage = "Developer can not give features information";
                        }
                        if (updateURL.equals("")) {
                            updateURL = "https://www.google.com/";
                        }
                        onConfirm("Update available!", "Features: " + featureMessage, "UPDATE", "CANCEL", new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    Uri uri = Uri.parse(updateURL); // missing 'http://' will cause crashed
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                    System.exit(1);
                                } catch (Exception e) {
                                    Log.e("Error", e.getMessage());
                                    Toast.makeText(getApplication(), "Something went wrong!.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onNetwork(NetworkMessage message) {
        Log.e("onNetwork", "Called");
        if (!message.isConnected()) {
            onAlertDialog("No internet!", "Please check your internet connection.", "OK",
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            closeOpenedDialogs();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDeviceInfo() {
        try {
            String id = Build.ID;
            String device_name = Build.DEVICE;
            String os = Build.VERSION.BASE_OS;
            String model = Build.MODEL;
            String user = Build.USER;
            String partition = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                partition = Build.Partition.PARTITION_NAME_SYSTEM;
            }
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

            HashMap<String, Object> addFieldInfo = new HashMap<>();
            addFieldInfo.put("deviceId", "" + id);
            addFieldInfo.put("deviceName", "" + device_name);
            addFieldInfo.put("deviceOs", "" + os);
            addFieldInfo.put("deviceModel", "" + model);
            addFieldInfo.put("deviceUser", "" + user);
            addFieldInfo.put("devicePartition", "" + partition);
            addFieldInfo.put("deviceOSVersion", "" + String.valueOf(Build.VERSION.SDK_INT));
            addFieldInfo.put("deviceIP", "" + ip);

            DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("Devices").document(id);
            databaseReference.set(addFieldInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e("Firebase", "Got device details buddy!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Firebase error", e.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

    }

    private void getFeatureMessage() {
        try {
            firebaseFirestore.collection("DownloaderMessage").document("info").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                version = task.getResult().getString("version");
                                featureMessage = task.getResult().getString("feature_message");
                                updateURL = task.getResult().getString("update_url");
                                Log.e("version", "" + version);
                                Log.e("featureMessage", "" + featureMessage);
                            } else {
                                Log.e("Error", task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void showDialog() {
        if (featureMessage.equals("")) {
            featureMessage = "Developer can not give features information";
        }
        if (updateURL.equals("")) {
            updateURL = "https://www.google.com/";
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update available!");
            builder.setMessage("Features: " + featureMessage);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        Uri uri = Uri.parse(updateURL); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        System.exit(1);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        Toast.makeText(getApplication(), "Something went wrong!.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }, 2000);
    }

    private int getCurrentVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return packageInfo.versionCode;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFeatureMessage();
    }

    //    void showMaterialDialog() {
//        if (featureMessage.equals("")) {
//            featureMessage = "Developer can not give features information";
//        }
//        if (updateURL.equals("")) {
//            updateURL = "https://www.google.com/";
//        }
//
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//
//            alertDialog = new MaterialDialog.Builder(getApplicationContext())
//                    .title("Update available!")
//                    .cancelable(false)
//                    .canceledOnTouchOutside(false)
//                    .content(featureMessage)
//                    .contentColor(Color.BLACK)
//                    .typeface(getString(R.string.font_medium), getString(R.string.font_regular))
//                    .canceledOnTouchOutside(false)
//                    .positiveColor(Color.BLACK)
//                    .positiveText("OK")
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@android.support.annotation.NonNull @NonNull MaterialDialog dialog, @android.support.annotation.NonNull @NonNull DialogAction which) {
//                            try {
//                                Uri uri = Uri.parse(updateURL); // missing 'http://' will cause crashed
//                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                startActivity(intent);
//                                System.exit(1);
//                            } catch (Exception e) {
//                                Log.e("Error", e.getMessage());
//                                Toast.makeText(getApplication(), "Something went wrong!.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    })
//                    .build();
//            alertDialog.getTitleView().setTextSize(16);
//            alertDialog.getContentView().setTextSize(14);
//            alertDialog.show();
//
//        }, 2000);
//    }

    private void runCodeOneTimeOnly() {
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(getApplicationContext(), MyService.class));
//        }
//        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

  /*      boolean firstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRun", true);
        if (firstRun){
            //... Display the dialog message here ...
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstRun", false)
                    .commit();
        }*/
    }
}