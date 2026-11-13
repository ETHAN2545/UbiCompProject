package com.example.dublin8community;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.activities.AdminDashboardActivity;
import com.example.dublin8community.activities.RegisterActivity;
import com.example.dublin8community.activities.UserDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> loginUser());

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Enter your email and password", Toast.LENGTH_SHORT).show();

            return;
        }

        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()) {

                String userId = mAuth.getCurrentUser().getUid();

                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        loginButton.setEnabled(true);

                        if (snapshot.exists()) {
                            String role = snapshot.child("role").getValue(String.class);

                            if ("admin".equals(role)) {
                                openDashboard("admin");

                            } else if ("user".equals(role)) {
                                openDashboard("user");

                            } else {
                                mAuth.signOut();

                                Toast.makeText(MainActivity.this, "Invalid user role", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mAuth.signOut();

                            Toast.makeText(MainActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError error) {
                        loginButton.setEnabled(true);
                        mAuth.signOut();

                        Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                loginButton.setEnabled(true);

                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openDashboard(String role) {

        Intent intent;

        if (role.equals("admin")) {
            intent = new Intent(MainActivity.this, AdminDashboardActivity.class);

        } else {

            intent = new Intent(MainActivity.this, UserDashboardActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
