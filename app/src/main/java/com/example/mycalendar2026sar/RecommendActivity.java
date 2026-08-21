package com.example.mycalendar2026sar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RecommendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        findViewById(R.id.recommendBackButton).setOnClickListener(v -> finish());
    }
}