package com.example.ydownload.ui.whatsapp.tabLayout;

import androidx.lifecycle.ViewModelProvider;

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

import com.example.ydownload.R;
import com.example.ydownload.adapter.ImageAdapter;
import com.example.ydownload.databinding.ImageFragmentBinding;
import com.example.ydownload.model.Status;
import com.example.ydownload.utils.Common;
import com.example.ydownload.viewModel.ImageViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageFragment extends Fragment {

    private ImageViewModel mViewModel;
    private ImageFragmentBinding imageFragmentBinding;
    private final Handler handler = new Handler();
    private ImageAdapter imageAdapter;
    private final List<Status> imagesList = new ArrayList<>();

    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        imageFragmentBinding = ImageFragmentBinding.inflate(inflater, container, false);
        return imageFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getImageStatus();
        imageFragmentBinding.swipeRefreshLayout.setOnRefreshListener(this::getImageStatus);
    }

    private void getImageStatus() {

        if (Common.STATUS_DIRECTORY.exists()) {

            execute(Common.STATUS_DIRECTORY);

        } else if (Common.STATUS_DIRECTORY_NEW.exists()) {

            execute(Common.STATUS_DIRECTORY_NEW);

        } else {
//            Toast.makeText(getActivity(), "Cannot find whatsapp directory!", Toast.LENGTH_SHORT).show();
            imageFragmentBinding.messageTextImage.setVisibility(View.VISIBLE);
            imageFragmentBinding.messageTextImage.setText(R.string.cannot_find);
            imageFragmentBinding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void execute(File wAFolder) {
        new Thread(() -> {
            File[] statusFiles;
            statusFiles = wAFolder.listFiles();
            imagesList.clear();

            if (statusFiles != null && statusFiles.length > 0) {

                Arrays.sort(statusFiles);
                for (File file : statusFiles) {
                    Status status = new Status(file, file.getName(), file.getAbsolutePath());

                    if (!status.isVideo() && status.getTitle().endsWith(".jpg")) {
                        imagesList.add(status);
                    }
                }

                handler.post(() -> {

                    if (imagesList.size() <= 0) {
//                        Toast.makeText(getActivity(), "Items not found...!", Toast.LENGTH_SHORT).show();
                        imageFragmentBinding.messageTextImage.setVisibility(View.VISIBLE);
                        imageFragmentBinding.messageTextImage.setText(R.string.file_not_found);
                    } else {
                        imageFragmentBinding.messageTextImage.setVisibility(View.GONE);
                        imageFragmentBinding.messageTextImage.setText("");
                    }

                    imageAdapter = new ImageAdapter(imagesList, imageFragmentBinding.imageContainer);
                    imageFragmentBinding.recyclerViewImage.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    imageFragmentBinding.recyclerViewImage.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
//                    progressBar.setVisibility(View.GONE);
                });

            } else {

                handler.post(() -> {
                    imageFragmentBinding.messageTextImage.setVisibility(View.VISIBLE);
                    imageFragmentBinding.messageTextImage.setText(R.string.file_not_found);
                    Toast.makeText(getActivity(), R.string.file_not_found, Toast.LENGTH_SHORT).show();
                });

            }
            imageFragmentBinding.swipeRefreshLayout.setRefreshing(false);
        }).start();
    }


}