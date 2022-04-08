package com.example.ydownload.ui.home;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ydownload.databinding.FragmentHomeBinding;
import com.example.ydownload.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeViewModel.getActivity(getActivity());
        binding.setHomeViewModel(homeViewModel);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance().hideSoftKeyboard(getActivity());
                String yLink = binding.editText.getText().toString();
                if (yLink != null && !yLink.isEmpty()) {
                    if ((yLink.contains("://youtu.be/") || yLink.contains("youtube.com/watch?v="))) {
                        binding.processBar.setVisibility(View.VISIBLE);
                        getYoutubeDownloadUrl(binding.editText.getText().toString());
                    } else {
                        Snackbar.make(view, "Not a valid YouTube link!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "Please enter the URL", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            }
        });
    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(getActivity()) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                binding.processBar.setVisibility(View.GONE);

                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
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
    }

    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
        // Display some buttons and let the user choose the format
        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
                ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
        Button btn = new Button(getContext());
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
                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
                Toast.makeText(getContext(), "Download will start... Please check your notification bar.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.mainLayout.addView(btn);
    }

    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        try {
            Uri uri = Uri.parse(youtubeDlUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(downloadTitle);

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}