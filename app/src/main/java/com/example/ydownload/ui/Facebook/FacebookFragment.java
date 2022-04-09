package com.example.ydownload.ui.Facebook;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.ydownload.databinding.FragmentFacebookBinding;
import com.example.ydownload.utils.DownloaderUtil;
import com.example.ydownload.utils.SharedPreference;
import com.example.ydownload.utils.Utils;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FacebookFragment extends Fragment {

    private FacebookViewModel mViewModel;
    private FragmentFacebookBinding facebookFragmentBinding;
    private FrameLayout processBar;
    private final Sprite foldCube = new FoldingCube();

    public static FacebookFragment newInstance() {
        return new FacebookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        facebookFragmentBinding = FragmentFacebookBinding.inflate(inflater, container, false);
        return facebookFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FacebookViewModel.class);
        SharedPreference.getInstance().saveValue(getContext(), "button", "download");
        processBar = facebookFragmentBinding.frameLayoutProcessBarFacebook;
        facebookFragmentBinding.spinKit.setIndeterminateDrawable(foldCube);
        facebookFragmentBinding.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance().hideSoftKeyboard(getActivity());
                if (Utils.getInstance().isNetworkConnectionAvailable(getContext())) {
                    String url = facebookFragmentBinding.editText.getText().toString();
                    if (url != null && !url.isEmpty()) {
                        if (url.contains("facebook.com")) {
                            processBar.setVisibility(View.VISIBLE);
                            SharedPreference.getInstance().saveValue(getContext(), "button", "download");
                            new CallGetFbData().execute(facebookFragmentBinding.editText.getText().toString());
                        } else if (url.contains("fb.watch")) {
                            SharedPreference.getInstance().saveValue(getContext(), "button", "download");
                            processBar.setVisibility(View.VISIBLE);
                            openWebView(url);
                        } else {
                            Snackbar.make(view, "Invalid facebook URL!", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(view, "Please enter the URL!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "Please check your internet connection!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        facebookFragmentBinding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance().hideSoftKeyboard(getActivity());
                if (Utils.getInstance().isNetworkConnectionAvailable(getContext())) {
                    String url = facebookFragmentBinding.editText.getText().toString();
                    if (url != null && !url.isEmpty()) {
                        if (url.contains("facebook.com")) {
                            processBar.setVisibility(View.VISIBLE);
                            SharedPreference.getInstance().saveValue(getContext(), "button", "play");
                            new CallGetFbData().execute(facebookFragmentBinding.editText.getText().toString());
                        } else if (url.contains("fb.watch")) {
                            processBar.setVisibility(View.VISIBLE);
                            SharedPreference.getInstance().saveValue(getContext(), "button", "play");
                            openWebView(url);
                        } else {
                            Snackbar.make(view, "Invalid facebook URL!", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(view, "Please enter the URL!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "Please check your internet connection!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openWebView(String url) {
        try {
            facebookFragmentBinding.webView.getSettings().setJavaScriptEnabled(true);
            facebookFragmentBinding.webView.loadUrl(url);
            facebookFragmentBinding.webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    //SHOW LOADING IF IT ISN'T ALREADY VISIBLE
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    new CallGetFbData().execute(url);
                    Log.e("onPageFinished", "" + url);
                }
            });
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            processBar.setVisibility(View.GONE);
        }
    }

    class CallGetFbData extends AsyncTask<String, Void, Document> {

        Document fbDoc;

        @Override
        protected Document doInBackground(String... strings) {
            try {
                fbDoc = Jsoup.connect(strings[0]).get();
            } catch (IOException e) {
                processBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
            return fbDoc;
        }

        @Override
        protected void onPostExecute(Document document) {
            try {
                String videoUrl = document.select("meta[property=\"og:video\"]")
                        .last().attr("content");
                if (!videoUrl.equals("")) {
                    processBar.setVisibility(View.GONE);
                    String button = SharedPreference.getInstance().getValue(getContext(), "button");
                    if (button.equals("download")) {
                        facebookFragmentBinding.editText.setText("");
                        DownloaderUtil.download(getContext(), videoUrl, DownloaderUtil.RootDirFacebook, "facebook" + System.currentTimeMillis() + ".mp4");
                    } else {
                        Uri uri = Uri.parse(videoUrl);
                        facebookFragmentBinding.videoPlayer.setVisibility(View.VISIBLE);
                        facebookFragmentBinding.videoPlayer.setVideoURI(uri);
                        MediaController mediaController = new MediaController(getContext());
                        mediaController.setAnchorView(facebookFragmentBinding.videoPlayer);
                        mediaController.setMediaPlayer(facebookFragmentBinding.videoPlayer);
                        facebookFragmentBinding.videoPlayer.setMediaController(mediaController);
                        facebookFragmentBinding.videoPlayer.start();
                    }
//                DownloaderUtil.download(getContext(), videoUrl, DownloaderUtil.RootDirFacebook, "facebook" + System.currentTimeMillis() + ".mp4");
                }
            } catch (Exception e) {
                processBar.setVisibility(View.GONE);
                Log.e("Error", e.getMessage());
            }
        }
    }

}