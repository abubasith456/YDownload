package com.example.ydownload.ui.whatsapp;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ydownload.adapter.TabAdapter;
import com.example.ydownload.databinding.WhatsappFragmentBinding;
import com.example.ydownload.utils.Common;
import com.example.ydownload.viewModel.WhatsappViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;

public class WhatsappFragment extends Fragment {

    private static String[] WRITE_EXTERNAL_STORAGE;
    private WhatsappViewModel mViewModel;
    private WhatsappFragmentBinding whatsappFragmentBinding;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage";

    ArrayList filesList = new ArrayList<>();

    public static WhatsappFragment newInstance() {
        return new WhatsappFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        whatsappFragmentBinding = WhatsappFragmentBinding.inflate(inflater, container, false);
        return whatsappFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WhatsappViewModel.class);
        WRITE_EXTERNAL_STORAGE=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission();
        whatsappFragmentBinding.tabLayout.addTab(whatsappFragmentBinding.tabLayout.newTab().setText("Image"));
        whatsappFragmentBinding.tabLayout.addTab(whatsappFragmentBinding.tabLayout.newTab().setText("Video"));
        whatsappFragmentBinding.tabLayout.addTab(whatsappFragmentBinding.tabLayout.newTab().setText("Downloaded"));
        whatsappFragmentBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager(), getContext(), whatsappFragmentBinding.tabLayout.getTabCount());
        whatsappFragmentBinding.viewPager.setAdapter(tabAdapter);
        whatsappFragmentBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(whatsappFragmentBinding.tabLayout));
        whatsappFragmentBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                whatsappFragmentBinding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
//                startActivity(new Intent(this, MainActivity.class));
//                Toast.makeText(getContext(), "Loading..", Toast.LENGTH_SHORT).show();
            } else { //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
//            //below android 11=======
//            startActivity(new Intent(this, MainActivity.class));
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Common.APP_DIR = Environment.getExternalStorageDirectory().getPath() +
                File.separator + "MultiDownload";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                getContext().getSystemService(Context.ACTIVITY_SERVICE);
                getActivity().recreate();
            }
        }
    }

    private boolean arePermissionDenied() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return checkStorageApi30();
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    boolean checkStorageApi30() {
        AppOpsManager appOps = getContext().getSystemService(AppOpsManager.class);
        int mode = appOps.unsafeCheckOpNoThrow(
                MANAGE_EXTERNAL_STORAGE_PERMISSION,
                getContext().getApplicationInfo().uid,
                getContext().getPackageName()
        );
        return mode != AppOpsManager.MODE_ALLOWED;

    }


}