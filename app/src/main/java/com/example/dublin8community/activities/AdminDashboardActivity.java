package com.example.dublin8community.activities;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.MainActivity;
import com.example.dublin8community.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;

    private Button manageEventsButton;
    private Button manageServicesButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        welcomeText = findViewById(R.id.welcomeText);

        manageEventsButton = findViewById(R.id.manageEventsButton);
        manageServicesButton = findViewById(R.id.manageServicesButton);
        logoutButton = findViewById(R.id.logoutButton);

        welcomeText.setText("Administrator Dashboard");

        manageEventsButton.setText("Manage Events");

        manageEventsButton.setOnClickListener(v -> {

            Intent intent = new Intent(AdminDashboardActivity.this, ManageEventsActivity.class);

            startActivity(intent);
        });

        manageServicesButton.setText("Manage Services");

        manageServicesButton.setOnClickListener(v -> {

            Intent intent = new Intent(AdminDashboardActivity.this, ManageServicesActivity.class);

            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        });
    }
}
