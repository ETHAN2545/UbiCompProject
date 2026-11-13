package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.example.dublin8community.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        welcomeText = findViewById(R.id.welcomeText);

        welcomeText.setText("Administrator Dashboard");

        Button logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        });
    }
}
