package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.example.dublin8community.MainActivity;
import com.example.dublin8community.activities.UserEventsActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;

public class UserDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button eventsButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        welcomeText = findViewById(R.id.welcomeText);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        welcomeText.setText("Dublin 8 Community");

        eventsButton.setOnClickListener(v -> {

            Intent intent = new Intent(UserDashboardActivity.this, UserEventsActivity.class);

            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        });
    }
}
