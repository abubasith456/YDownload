package com.example.ydownload.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ydownload.ui.whatsapp.tabLayout.DownloadedFragment;
import com.example.ydownload.ui.whatsapp.tabLayout.ImageFragment;
import com.example.ydownload.ui.whatsapp.tabLayout.VideoFragment;

public class TabAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public TabAdapter(@NonNull FragmentManager fm, Context myContext, int totalTabs) {
        super(fm);
        this.myContext = myContext;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ImageFragment imageFragment = new ImageFragment();
                return imageFragment;
            case 1:
                VideoFragment videoFragment = new VideoFragment();
                return videoFragment;
            case 2:
                DownloadedFragment downloadedFragment = new DownloadedFragment();
                return downloadedFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
