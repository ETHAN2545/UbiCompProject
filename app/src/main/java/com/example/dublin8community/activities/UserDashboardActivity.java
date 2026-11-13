package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;

public class UserDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        welcomeText = findViewById(R.id.welcomeText);

        welcomeText.setText("Dublin 8 Community");
    }
}
