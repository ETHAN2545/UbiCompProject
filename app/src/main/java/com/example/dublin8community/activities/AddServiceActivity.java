package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddServiceActivity extends AppCompatActivity {

    private EditText serviceNameInput;
    private EditText descriptionInput;
    private EditText addressInput;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private EditText openingHoursInput;
    private EditText contactEmailInput;
    private EditText contactPhoneInput;

    private Spinner categorySpinner;

    private Button saveServiceButton;

    private DatabaseReference servicesRef;

    private String serviceId;
    private Service existingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        serviceNameInput = findViewById(R.id.serviceNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        addressInput = findViewById(R.id.addressInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        openingHoursInput = findViewById(R.id.openingHoursInput);
        contactEmailInput = findViewById(R.id.contactEmailInput);
        contactPhoneInput = findViewById(R.id.contactPhoneInput);

        categorySpinner = findViewById(R.id.categorySpinner);
        saveServiceButton = findViewById(R.id.saveServiceButton);

        setupCategorySpinner();

        serviceId = getIntent().getStringExtra("serviceId");

        if (serviceId != null) {
            setTitle("Edit Service");
            saveServiceButton.setText("Update Service");
            loadService();
        } else {
            setTitle("Add Service");
        }

        saveServiceButton.setOnClickListener(v -> saveService());
    }

    private void setupCategorySpinner() {

        String[] categories = {
                "Healthcare",
                "Education",
                "Community Centre",
                "Sports",
                "Food Support",
                "Youth Services",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);
    }

    private void loadService() {

        servicesRef.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                existingService = snapshot.getValue(Service.class);

                if (existingService == null) {
                    Toast.makeText(AddServiceActivity.this, "Service not found", Toast.LENGTH_SHORT).show();

                    finish();
                    return;
                }

                serviceNameInput.setText(existingService.serviceName);
                descriptionInput.setText(existingService.description);
                addressInput.setText(existingService.address);
                latitudeInput.setText(String.valueOf(existingService.latitude));
                longitudeInput.setText(String.valueOf(existingService.longitude));
                openingHoursInput.setText(existingService.openingHours);
                contactEmailInput.setText(existingService.contactEmail);
                contactPhoneInput.setText(existingService.contactPhone);

                for (int i = 0; i < categorySpinner.getCount(); i++) {

                    if (categorySpinner.getItemAtPosition(i).toString().equals(existingService.category)) {

                        categorySpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(AddServiceActivity.this, "Error loading service: " + error.getMessage(), Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }

    private void saveService() {

        String serviceName = serviceNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String latitudeString = latitudeInput.getText().toString().trim();
        String longitudeString = longitudeInput.getText().toString().trim();
        String openingHours = openingHoursInput.getText().toString().trim();
        String contactEmail = contactEmailInput.getText().toString().trim();
        String contactPhone = contactPhoneInput.getText().toString().trim();

        String category = categorySpinner.getSelectedItem().toString();

        if (serviceName.isEmpty() || description.isEmpty() || address.isEmpty() || latitudeString.isEmpty() || longitudeString.isEmpty() || openingHours.isEmpty()) {

            Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT).show();

            return;
        }

        double latitude;
        double longitude;

        try {
            latitude = Double.parseDouble(latitudeString);
            longitude = Double.parseDouble(longitudeString);
        } catch (NumberFormatException e) {

            Toast.makeText(this, "Enter valid coordinates", Toast.LENGTH_SHORT).show();

            return;
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {

            Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();

            return;
        }

        if (contactEmail.isEmpty() && contactPhone.isEmpty()) {

            Toast.makeText(this, "Enter a contact email or phone number", Toast.LENGTH_SHORT).show();

            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            Toast.makeText(this, "You must be logged in to save a service", Toast.LENGTH_SHORT).show();

            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (serviceId == null) {

            serviceId = servicesRef.push().getKey();

            if (serviceId == null) {
                Toast.makeText(this, "Could not create service ID", Toast.LENGTH_SHORT).show();
                return;
            }

            existingService = null;
        }

        String createdBy = existingService == null ? currentUserId : existingService.createdBy;

        long createdAt = existingService == null ? System.currentTimeMillis() : existingService.createdAt;

        Service service = new Service(
                serviceId,
                serviceName,
                description,
                category,
                address,
                latitude,
                longitude,
                openingHours,
                contactEmail,
                contactPhone,
                createdBy,
                createdAt
        );

        servicesRef.child(serviceId).setValue(service).addOnSuccessListener(unused -> {

            Toast.makeText(AddServiceActivity.this, existingService == null ? "Service added successfully" : "Service updated successfully", Toast.LENGTH_SHORT).show();

            finish();
        })
                .addOnFailureListener(e -> {

                    Toast.makeText(AddServiceActivity.this, "Error saving service: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
