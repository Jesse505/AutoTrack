package com.example.jesse.autotrackappclick;

import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
    public static void showToast(Toast toast) {
        TextView tv = toast.getView().findViewById(Resources.getSystem().getIdentifier(
                "message", "id", "android"));
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        Log.i("zyf", "toast show content >>> " + tv.getText());
        toast.show();
    }
}
