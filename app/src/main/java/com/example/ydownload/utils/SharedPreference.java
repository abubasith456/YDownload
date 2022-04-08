package com.example.ydownload.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

    private static SharedPreference instance;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public static SharedPreference getInstance() {
        if (instance == null) {
            instance = new SharedPreference();
        }
        return instance;
    }

    public void saveValue(Context context, String key, String value) {
        sharedPref = context.getSharedPreferences("AppFieldValues", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(Context context,String key) {
        SharedPreferences prefs = context.getSharedPreferences("AppFieldValues", Context.MODE_PRIVATE);
        return prefs.getString(key,"");
    }
}
