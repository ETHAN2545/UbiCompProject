package com.example.dublin8community.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.adapters.ServiceAdapter;
import com.example.dublin8community.models.Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageServicesActivity extends AppCompatActivity {

    private ListView servicesListView;
    private Button addServiceButton;

    private DatabaseReference servicesRef;

    private List<Service> serviceList;
    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        servicesListView = findViewById(R.id.servicesListView);
        addServiceButton = findViewById(R.id.addServiceButton);

        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        serviceList = new ArrayList<>();

        serviceAdapter = new ServiceAdapter(this, serviceList);
        servicesListView.setAdapter(serviceAdapter);

        addServiceButton.setOnClickListener(v -> {

            Intent intent = new Intent(ManageServicesActivity.this, AddServiceActivity.class);

            startActivity(intent);
        });

        loadServices();

        servicesListView.setOnItemClickListener((parent, view, position, id) -> {

            Service selectedService = serviceList.get(position);

            showServiceOptions(selectedService);
        });
    }

    private void loadServices() {

        servicesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                serviceList.clear();

                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {

                    Service service = serviceSnapshot.getValue(Service.class);

                    if (service != null) {

                        service.serviceId = serviceSnapshot.getKey();

                        serviceList.add(service);
                    }
                }

                serviceAdapter.notifyDataSetChanged();

                if (serviceList.isEmpty()) {

                    Toast.makeText(ManageServicesActivity.this, "No community services available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(ManageServicesActivity.this, "Failed to retrieve services: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showServiceOptions(Service service) {

        String[] options = {"Edit Service", "Delete Service"};

        new AlertDialog.Builder(this).setTitle(service.serviceName).setItems(options, (dialog, which) -> {

            if (which == 0) {
                editService(service);
            } else {
                deleteService(service);
            }
        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void editService(Service service) {

        Intent intent = new Intent(ManageServicesActivity.this, AddServiceActivity.class);

        intent.putExtra("serviceId", service.serviceId);

        startActivity(intent);
    }

    private void deleteService(Service service) {

        new AlertDialog.Builder(this).setTitle("Delete Service").setMessage("Are you sure you want to delete " + service.serviceName + "?").setPositiveButton("Delete", (dialog, which) -> {

            servicesRef.child(service.serviceId).removeValue().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    Toast.makeText(ManageServicesActivity.this, "Service deleted successfully", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(ManageServicesActivity.this, "Failed to delete service", Toast.LENGTH_SHORT).show();
                }
            });
        })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
