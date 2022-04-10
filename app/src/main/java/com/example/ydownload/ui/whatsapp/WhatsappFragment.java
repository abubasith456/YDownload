package com.example.ydownload.ui.whatsapp;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.ydownload.R;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class WhatsappFragment extends Fragment {

    private WhatsappViewModel mViewModel;
//    Toolbar mToolbar;
//    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
//    private StoryAdapter recyclerViewAdapter;
//    public static final int ITEMS_PER_AD = 6;
//    private static final int NATIVE_EXPRESS_AD_HEIGHT = 320;

    ArrayList filesList = new ArrayList<>();

    public static WhatsappFragment newInstance() {
        return new WhatsappFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.whatsapp_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WhatsappViewModel.class);


        File folder = new File(Environment.getExternalStorageDirectory().toString()+"/whatsapp/Media/.Statuses/");
        Log.e("All files", ""+filesList.toString());
//        /storage/emulated/0
        if (folder.exists()) {
            File[] allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                }
            });
            Log.e("All files", allFiles.toString());
        }

    }

}