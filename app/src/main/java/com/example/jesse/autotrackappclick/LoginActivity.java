package com.example.jesse.autotrackappclick;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String text = getIntent().getStringExtra("key");
        ((TextView)findViewById(R.id.tv_show)).setText(text);

    }
}
