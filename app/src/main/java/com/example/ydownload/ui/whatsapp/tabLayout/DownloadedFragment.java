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

import com.example.ydownload.R;
import com.example.ydownload.adapter.FilesAdapter;
import com.example.ydownload.databinding.DownloadedFragmentBinding;
import com.example.ydownload.model.Status;
import com.example.ydownload.utils.Common;
import com.example.ydownload.viewModel.DownloadedViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadedFragment extends Fragment {

    private DownloadedViewModel mViewModel;
    private DownloadedFragmentBinding downloadedFragmentBinding;
    private final List<Status> savedFilesList = new ArrayList<>();
    private final Handler handler = new Handler();
    private FilesAdapter filesAdapter;

    public static DownloadedFragment newInstance() {
        return new DownloadedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        downloadedFragmentBinding = DownloadedFragmentBinding.inflate(inflater, container, false);
        return downloadedFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DownloadedViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getFiles();
        downloadedFragmentBinding.swipeRefreshLayoutFiles.setOnRefreshListener(this::getFiles);
    }

    private void getFiles() {

        final File app_dir = new File(Common.APP_DIR);

        if (app_dir.exists()) {

//            no_files_found.setVisibility(View.GONE);

            new Thread(() -> {
                File[] savedFiles;
                savedFiles = app_dir.listFiles();
                savedFilesList.clear();

                if (savedFiles != null && savedFiles.length > 0) {

                    Arrays.sort(savedFiles);
                    for (File file : savedFiles) {
                        Status status = new Status(file, file.getName(), file.getAbsolutePath());

                        savedFilesList.add(status);
                    }

                    handler.post(() -> {

                        filesAdapter = new FilesAdapter(savedFilesList);
                        downloadedFragmentBinding.recyclerViewFiles.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        downloadedFragmentBinding.recyclerViewFiles.setAdapter(filesAdapter);
                        filesAdapter.notifyDataSetChanged();
//                        progressBar.setVisibility(View.GONE);
                    });

                } else {

                    handler.post(() -> {
//                        progressBar.setVisibility(View.GONE);
//                        no_files_found.setVisibility(View.VISIBLE);
//                        Snackbar.make(getView(), "Dir doest not exists!", Snackbar.LENGTH_SHORT)
//                                .setAction("Action", null).show();
                        Toast.makeText(getActivity(), "File not found...!", Toast.LENGTH_SHORT).show();
                    });

                }
                downloadedFragmentBinding.swipeRefreshLayoutFiles.setRefreshing(false);
            }).start();

        } else {
//            Snackbar.make(getView(), "File not found...!", Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
//            Toast.makeText(getActivity(), "File not found...!", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Dir doest not exists!", Toast.LENGTH_SHORT).show();
        }

    }

}