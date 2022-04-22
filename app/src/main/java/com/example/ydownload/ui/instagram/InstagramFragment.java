package com.example.ydownload.ui.instagram;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.example.ydownload.R;
import com.example.ydownload.databinding.InstagramFragmentBinding;
import com.example.ydownload.viewModel.InstagramViewModel;
import com.google.android.material.snackbar.Snackbar;

public class InstagramFragment extends Fragment {

    private InstagramViewModel mViewModel;
    InstagramFragmentBinding instagramFragmentBinding;
    String url;

    public static InstagramFragment newInstance() {
        return new InstagramFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        instagramFragmentBinding = InstagramFragmentBinding.inflate(inflater, container, false);
        return instagramFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InstagramViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        instagramFragmentBinding.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = instagramFragmentBinding.editText.getText().toString();
                if (TextUtils.isEmpty(url)) {
                    Snackbar.make(view, "Please enter the URL!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else if (URLUtil.isValidUrl(url)) {
                    Snackbar.make(view, "Please enter the valid URL!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else if (URLUtil.isFileUrl(url)) {
                    if (url.endsWith(".jpg")) {
//                        new saveImage(url).execute();
//                    } else {
//                        new saveVideo(url).execute(new String[0]);
                    }
                }
            }
        });
    }
}