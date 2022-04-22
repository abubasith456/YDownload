package com.example.ydownload.viewModel;

import android.app.Application;
import android.util.Log;

import com.jenzz.appstate.AppState;
import com.jenzz.appstate.RxAppStateMonitor;

import rx.functions.Action1;

public class TrustedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RxAppStateMonitor.monitor(this).subscribe(new Action1<AppState>() {
            @Override
            public void call(AppState appState) {
                switch (appState) {
                    case FOREGROUND:
                        Log.e("BGDATA", "Foreground");
//                        HandlerFactory.getInstance().setIsAppBackground(false);
                        break;
                    case BACKGROUND:
                        Log.e("BGDATA", "Background");
//                        HandlerFactory.getInstance().setIsAppBackground(true);
                        break;
                }
            }
        });
    }
}
