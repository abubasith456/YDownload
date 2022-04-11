package com.example.ydownload.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.ydownload.R;
import com.example.ydownload.databinding.ActivityMainBinding;
import com.example.ydownload.utils.DownloaderUtil;
import com.example.ydownload.utils.SharedPreference;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private static String urlLink;
    private LinearLayout mainLayout;
    private final Sprite foldCube = new FoldingCube();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainLayout = activityMainBinding.mainLayout;
        activityMainBinding.spinKit.setIndeterminateDrawable(foldCube);
        activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
        SharedPreference.getInstance().saveValue(getApplicationContext(), "button", "play");
        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType())) {

            String intentUrlValue = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            urlLink = intentUrlValue;
            if (intentUrlValue != null) {
                Log.e("Get Url", urlLink);
                if ((intentUrlValue.contains("://youtu.be/") || intentUrlValue.contains("youtube.com/watch?v="))) {
                    activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.VISIBLE);
                    activityMainBinding.frameLayoutFacebook.setVisibility(View.GONE);

                    // We have a valid link
                    getYoutubeDownloadUrl(urlLink);

                } else if (intentUrlValue.contains("facebook.com") || intentUrlValue.contains("fb.watch")) {
                    activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.VISIBLE);
                    activityMainBinding.frameLayoutFacebook.setVisibility(View.VISIBLE);
                    Log.e("Get Url", urlLink);
                    executeFacebookVideo(urlLink);
                } else {
                    Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Log.e("Get url", "null");
            }
        } else if (savedInstanceState != null && urlLink != null) {
            getYoutubeDownloadUrl(urlLink);
        } else {
            finish();
        }

        activityMainBinding.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreference.getInstance().saveValue(getApplicationContext(), "button", "download");
                executeFacebookVideo(urlLink);
                finish();
            }
        });
    }

    private void executeFacebookVideo(String url) {
        try {
            if (url.contains("fb.watch")) {
                openWebView(url);
            } else {
                new CallGetFbData().execute(url);
            }
        } catch (Exception e) {
            Log.e("Error Facebook", e.getMessage());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openWebView(String url) {
        try {
            activityMainBinding.webView.getSettings().setJavaScriptEnabled(true);
            activityMainBinding.webView.loadUrl(url);
            activityMainBinding.webView.setWebViewClient(new WebViewClient() {

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
            activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
        }
    }

    class CallGetFbData extends AsyncTask<String, Void, Document> {

        Document fbDoc;

        @Override
        protected Document doInBackground(String... strings) {
            try {
                fbDoc = Jsoup.connect(strings[0]).get();
            } catch (IOException e) {
                activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
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
                    activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
                    String button = SharedPreference.getInstance().getValue(getApplicationContext(), "button");
                    if (button.equals("download")) {
                        DownloaderUtil.download(getApplicationContext(), videoUrl, DownloaderUtil.RootDirFacebook, "facebook" + System.currentTimeMillis() + ".mp4");
                    } else {
                        Uri uri = Uri.parse(videoUrl);
                        activityMainBinding.videoViewFaceBook.setVisibility(View.VISIBLE);
                        activityMainBinding.videoViewFaceBook.setVideoURI(uri);
                        MediaController mediaController = new MediaController(MainActivity.this);
                        mediaController.setAnchorView(activityMainBinding.videoViewFaceBook);
                        mediaController.setMediaPlayer(activityMainBinding.videoViewFaceBook);
                        activityMainBinding.videoViewFaceBook.setMediaController(mediaController);
                        activityMainBinding.videoViewFaceBook.start();
                    }
                }
            } catch (Exception e) {
                activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
                Log.e("Error", e.getMessage());
            }
        }
    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        try {
            new YouTubeExtractor(this) {

                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
                    if (ytFiles == null) {
                        // Something went wrong we got no urls. Always check this.
                        finish();
                        return;
                    }
                    // Iterate over itags
                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        // ytFile represents one file with its url and meta data
                        YtFile ytFile = ytFiles.get(itag);

                        // Just add videos in a decent format => height -1 = audio
                        if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                            addButtonToMainLayout(vMeta.getTitle(), ytFile);
                        }
                    }
                }
            }.extract(youtubeLink);
        } catch (Exception e) {
            activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
            Log.e("Error", e.getMessage());
        }
    }

    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
        try {
            // Display some buttons and let the user choose the format
            String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                    ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                    ytfile.getFormat().getHeight() + "p";
            btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
            Button btn = new Button(this);
            btn.setText(btnText);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String filename;
                    if (videoTitle.length() > 55) {
                        filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
                    } else {
                        filename = videoTitle + "." + ytfile.getFormat().getExt();
                    }
                    filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
                    DownloaderUtil.download(getApplicationContext(), ytfile.getUrl(), videoTitle, filename);
                    Toast.makeText(getApplicationContext(), "Download will start... Please check your notification bar.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            mainLayout.addView(btn);
        } catch (Exception e) {
            activityMainBinding.frameLayoutProcessBarFacebook.setVisibility(View.GONE);
            Log.e("Error", e.getMessage());
        }

    }
}