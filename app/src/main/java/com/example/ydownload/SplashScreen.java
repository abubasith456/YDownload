package com.example.ydownload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.ydownload.databinding.ActivitySplashBinding;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashBinding activitySplashBinding;
    private final Sprite circle = new ThreeBounce();
    private final Handler handler = new Handler();
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseFirestore firebaseFirestore;
    private String featureMessage, updateURL, version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        firebaseFirestore = FirebaseFirestore.getInstance();
        activitySplashBinding.spinKit.setIndeterminateDrawable(circle);

        getFeatureMessage();
        handler.postDelayed(() -> {
            try {
                if (version != null) {
                    if (Integer.parseInt(version) > getCurrentVersionCode()) {
                        showDialog();
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
        }, 4000);
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
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (featureMessage == null) {
                featureMessage = "Developer can not give features information";
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update available!");
            builder.setMessage("Features: " + featureMessage);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        if (updateURL == null) {
                            updateURL = "https://Yahoo.com";
                        }
                        Uri uri = Uri.parse("https://google.com"); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        System.exit(1);
                    } catch (Exception e) {
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
}