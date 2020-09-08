package com.example.jesse.autotrackappclick;

import android.content.Intent;
import android.util.Log;

public class IntentUtil {
    public static String getString(Intent intent, String key) {
        Log.i("zyf", "getString key > " + key);
        return intent.getStringExtra(key);
    }
}
