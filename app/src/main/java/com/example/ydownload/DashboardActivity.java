package com.example.ydownload;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.ydownload.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ydownload.databinding.ActivityDashboardBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseFirestore firebaseFirestore;
    private String featureMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarDashboard.toolbar);
        firebaseFirestore = FirebaseFirestore.getInstance();
//        binding.appBarDashboard.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {

//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_facebook, R.id.nav_whatsapp, R.id.nav_about_us)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        getFeatureMessage();
//        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setFetchTimeoutInSeconds(5)
//                .build();
//        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
//
//        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
//            @Override
//            public void onComplete(@NonNull Task<Boolean> task) {
//                if (task.isSuccessful()) {
//                    final String new_version_code = firebaseRemoteConfig.getString("new_version_code");
//                    Log.e("new_version_code", "" + new_version_code);
//                    if (Integer.parseInt(new_version_code) > getCurrentVersionCode()) {
//                        showDialog();
//                    }
//                }
//            }
//        });
    }

    private void getFeatureMessage() {
        try {
            firebaseFirestore.collection("DownloaderMessage").document("message").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                featureMessage = task.getResult().getString("feature_message");
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
            builder.setMessage("Features: "+featureMessage);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("google.com ")));

                    } catch (Exception e) {
                        Toast.makeText(DashboardActivity.this, "Something went wrong!.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Utils.getInstance().hideSoftKeyboard(this);
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        Utils.getInstance().hideSoftKeyboard(this);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }
}