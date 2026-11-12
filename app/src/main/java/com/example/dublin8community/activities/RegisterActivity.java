package com.example.dublin8community.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {

            Toast.makeText(this, "Please complete all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();

            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show();

            return;
        }

        registerButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser == null) {
                            registerButton.setEnabled(true);
                            return;
                        }

                        String userId = firebaseUser.getUid();

                        User user = new User(name, email, "user", System.currentTimeMillis());

                        usersRef.child(userId).setValue(user)
                                .addOnCompleteListener(databaseTask -> {

                                    registerButton.setEnabled(true);

                                    if (databaseTask.isSuccessful()) {

                                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                        mAuth.signOut();
                                        finish();

                                    } else {

                                        Toast.makeText(this, "Account created, but profile could not be saved: " + databaseTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {

                        registerButton.setEnabled(true);

                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
