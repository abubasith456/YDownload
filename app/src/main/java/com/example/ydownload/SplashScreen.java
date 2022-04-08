package com.example.ydownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ydownload.databinding.ActivitySplashBinding;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashBinding activitySplashBinding;
    private final Sprite circle = new ThreeBounce();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        activitySplashBinding.spinKit.setIndeterminateDrawable(circle);
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}