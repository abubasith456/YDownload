package com.example.ydownload.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ydownload.R;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class HomeViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private FragmentActivity activity;
    public ObservableField<String> editTextUrl = new ObservableField<>();
    public ObservableField<Boolean> processBar = new ObservableField<>();
    private String newLink;

    public HomeViewModel() {

    }

    public void getActivity(FragmentActivity activity) {
        this.activity = activity;
    }
}
//    public void onDownloadClick(View view) {
//        try {
//
//            if (editTextUrl.get() != null) {
//                processBar.set(true);
//                getYoutubeDownloadUrl(editTextUrl.get());
//            } else {
//                processBar.set(false);
////                Toast.makeText(activity, "Please enter the url", Toast.LENGTH_SHORT).show();
//            }
//
//
////            YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(activity) {
////                @Override
////                public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
////                    if (ytFiles != null) {
////                        int tag = 22;
////                        newLink = ytFiles.get(tag).getUrl();
////                        String title = "Downloading video";
////                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(newLink));
////                        request.setTitle(title);
////                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "youtubeVideo" + ".mp4");
////                        @SuppressLint("ServiceCast") DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
////                        request.allowScanningByMediaScanner();
////                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
////                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
////                        downloadManager.enqueue(request);
////
////                    }
////                }
////            };
////            if (!(editTextUrl.get() == null)) {
////                youTubeUriExtractor.extract(editTextUrl.get());
////            } else {
////                Toast.makeText(activity, "Please enter the URL", Toast.LENGTH_SHORT).show();
////            }
//
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//        }
//    }
//
//    private void getYoutubeDownloadUrl(String youtubeLink) {
//        new YouTubeExtractor(activity) {
//
//            @Override
//            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
//                processBar.set(false);
//
//                if (ytFiles == null) {
//                    // Something went wrong we got no urls. Always check this.
//                    activity.finish();
//                    return;
//                }
//                // Iterate over itags
//                for (int i = 0, itag; i < ytFiles.size(); i++) {
//                    itag = ytFiles.keyAt(i);
//                    // ytFile represents one file with its url and meta data
//                    YtFile ytFile = ytFiles.get(itag);
//
//                    // Just add videos in a decent format => height -1 = audio
//                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
////                        addButtonToMainLayout(vMeta.getTitle(), ytFile);
//                        showDialog(vMeta.getTitle(), ytFile);
//                    }
//                }
//            }
//        }.extract(youtubeLink);
//    }
//
//    private void showDialog(String videoTitle, final YtFile ytfile) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
//        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
//                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
//                ytfile.getFormat().getHeight() + "p";
//        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
//        Button btn = new Button(activity);
//        btn.setText(btnText);
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String filename;
//                if (videoTitle.length() > 55) {
//                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
//                } else {
//                    filename = videoTitle + "." + ytfile.getFormat().getExt();
//                }
//                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
//                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
//                activity.finish();
//            }
//        });
//        LinearLayout linearLayout = dialogView.findViewById(R.id.LinearLayout);
//        linearLayout.addView(btn);
//        dialogBuilder.setView(dialogView);
//
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//    }
//
//    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
//        // Display some buttons and let the user choose the format
//        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
//                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
//                ytfile.getFormat().getHeight() + "p";
//        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
//        Button btn = new Button(activity);
//        btn.setText(btnText);
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String filename;
//                if (videoTitle.length() > 55) {
//                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
//                } else {
//                    filename = videoTitle + "." + ytfile.getFormat().getExt();
//                }
//                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
//                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
//                activity.finish();
//            }
//        });
////        mainLayout.addView(btn);
//    }
//
//    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
//        Uri uri = Uri.parse(youtubeDlUrl);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setTitle(downloadTitle);
//
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//
//        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);
//    }
//
//    private void showFormatDialog() {
//        try {
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
//            LayoutInflater inflater = activity.getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
//            dialogBuilder.setView(dialogView);
//
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//        }
//
//    }


