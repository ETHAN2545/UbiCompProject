package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceDetailsActivity extends AppCompatActivity {

    private TextView serviceNameText;
    private TextView categoryText;
    private TextView descriptionText;
    private TextView addressText;
    private TextView openingHoursText;
    private TextView contactEmailText;
    private TextView contactPhoneText;

    private DatabaseReference servicesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        serviceNameText = findViewById(R.id.serviceNameText);
        categoryText = findViewById(R.id.categoryText);
        descriptionText = findViewById(R.id.descriptionText);
        addressText = findViewById(R.id.addressText);
        openingHoursText = findViewById(R.id.openingHoursText);
        contactEmailText = findViewById(R.id.contactEmailText);
        contactPhoneText = findViewById(R.id.contactPhoneText);

        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        String serviceId = getIntent().getStringExtra("serviceId");

        if (serviceId != null) {
            loadServiceDetails(serviceId);
        } else {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadServiceDetails(String serviceId) {

        servicesRef.child(serviceId).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Service service = snapshot.getValue(Service.class);

                        if (service == null) {

                            Toast.makeText(ServiceDetailsActivity.this, "Unable to load service details", Toast.LENGTH_SHORT).show();

                            finish();
                            return;
                        }

                        serviceNameText.setText(service.serviceName);
                        categoryText.setText("Category: " + service.category);
                        descriptionText.setText(service.description);
                        addressText.setText(service.address);
                        openingHoursText.setText("Opening Hours: " + service.openingHours);

                        contactEmailText.setText(service.contactEmail == null || service.contactEmail.isEmpty() ? "Email: Not provided" : "Email: " + service.contactEmail);

                        contactPhoneText.setText(service.contactPhone == null || service.contactPhone.isEmpty() ? "Phone: Not provided" : "Phone: " + service.contactPhone);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        Toast.makeText(ServiceDetailsActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
