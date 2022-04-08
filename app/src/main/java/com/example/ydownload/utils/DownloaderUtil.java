package com.example.ydownload.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;

public class DownloaderUtil {

    public static String RootDirFacebook = "Video downloading";

    public static void download(Context context, String downloadPath, String title, String fileName) {
        Uri uri = Uri.parse(downloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show();
        manager.enqueue(request);

    }
}
