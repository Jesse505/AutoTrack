package com.example.jesse.autotrackappclick;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("普通 setOnClickListener");
            }
        });
        findViewById(R.id.lambdaButton).setOnClickListener(v -> showToast("lambda表达式绑定onClick"));
    }

    @SensorsDataTrackViewOnClick
    public void xmlOnClick(View view) {
        showToast("android:onClick 绑定 OnClickListener");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
