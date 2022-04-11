package com.example.ydownload.ui.whatsapp.tabLayout;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ydownload.adapter.VideoAdapter;
import com.example.ydownload.databinding.VideoFragmentBinding;
import com.example.ydownload.model.Status;
import com.example.ydownload.utils.Common;
import com.example.ydownload.viewModel.VideoViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoFragment extends Fragment {

    private VideoViewModel mViewModel;
    private VideoFragmentBinding videoFragmentBinding;
    private final List<Status> videoList = new ArrayList<>();
    private final Handler handler = new Handler();
    private VideoAdapter videoAdapter;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        videoFragmentBinding = VideoFragmentBinding.inflate(inflater, container, false);
        return videoFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getVideoStatus();
        videoFragmentBinding.swipeRefreshLayout.setOnRefreshListener(this::getVideoStatus);
    }

    private void getVideoStatus() {

        if (Common.STATUS_DIRECTORY.exists()) {

            execute(Common.STATUS_DIRECTORY);

        } else if (Common.STATUS_DIRECTORY_NEW.exists()) {

            execute(Common.STATUS_DIRECTORY_NEW);

        } else {
//            Snackbar.make(getView(), "Cannot find whatsapp directory!", Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
            Toast.makeText(getActivity(), "Cannot find the Whatsapp directory!", Toast.LENGTH_SHORT).show();
            videoFragmentBinding.swipeRefreshLayout.setRefreshing(false);
        }

    }

    private void execute(File waFolder) {
        new Thread(() -> {
            File[] statusFiles = waFolder.listFiles();
            videoList.clear();

            if (statusFiles != null && statusFiles.length > 0) {

                Arrays.sort(statusFiles);
                for (File file : statusFiles) {
                    Status status = new Status(file, file.getName(), file.getAbsolutePath());

                    if (status.isVideo()) {
                        videoList.add(status);
                    }

                }

                handler.post(() -> {

                    if (videoList.size() <= 0) {
//                        Snackbar.make(getView(), "Items not found...!", Snackbar.LENGTH_SHORT)
//                                .setAction("Action", null).show();
                        Toast.makeText(getActivity(), "Items not found...!", Toast.LENGTH_SHORT).show();
                    } else {

                    }

                    videoAdapter = new VideoAdapter(videoList, videoFragmentBinding.videosContainer);
                    videoFragmentBinding.recyclerViewVideo.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    videoFragmentBinding.recyclerViewVideo.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();
                });

            } else {

                handler.post(() -> {
//                    Snackbar.make(, "Items not found...!", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED)
//                            .setAction("Action", null).show();
                    Toast.makeText(getActivity(), "Items not found...!", Toast.LENGTH_SHORT).show();
                });

            }
            videoFragmentBinding.swipeRefreshLayout.setRefreshing(false);
        }).start();
    }

}