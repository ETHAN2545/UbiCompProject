package com.example.dublin8community.activities;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class UserServicesActivity extends AppCompatActivity {

    private ListView servicesListView;

    private EditText searchServicesInput;
    private EditText locationFilterInput;

    private Spinner categoryFilterSpinner;

    private Button resetFiltersButton;

    private DatabaseReference servicesRef;

    private List<Service> serviceList;
    private List<Service> filteredServiceList;

    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_services);

        servicesListView = findViewById(R.id.servicesListView);

        searchServicesInput = findViewById(R.id.searchServicesInput);
        locationFilterInput = findViewById(R.id.locationFilterInput);

        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);

        resetFiltersButton = findViewById(R.id.resetFiltersButton);

        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        serviceList = new ArrayList<>();
        filteredServiceList = new ArrayList<>();

        serviceAdapter = new ServiceAdapter(this, filteredServiceList);
        servicesListView.setAdapter(serviceAdapter);

        setupCategoryFilter();

        searchServicesInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterServices();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        locationFilterInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterServices();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        categoryFilterSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {

                        filterServices();
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                }
        );

        resetFiltersButton.setOnClickListener(v -> resetFilters());

        loadServices();

        servicesListView.setOnItemClickListener((parent, view, position, id) -> {

            Service selectedService = filteredServiceList.get(position);

            Intent intent = new Intent(UserServicesActivity.this, ServiceDetailsActivity.class);

            intent.putExtra("serviceId", selectedService.serviceId);

            startActivity(intent);
        });
    }

    private void setupCategoryFilter() {

        String[] categories = {
                "All Categories",
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

        categoryFilterSpinner.setAdapter(adapter);
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

                filterServices();

                if (serviceList.isEmpty()) {

                    Toast.makeText(UserServicesActivity.this, "No community services available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(UserServicesActivity.this, "Failed to load services: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterServices() {

        String searchText = searchServicesInput.getText().toString().trim().toLowerCase();

        String locationText = locationFilterInput.getText().toString().trim().toLowerCase();

        String selectedCategory = categoryFilterSpinner.getSelectedItem().toString();

        filteredServiceList.clear();

        for (Service service : serviceList) {

            boolean matchesName = service.serviceName != null && service.serviceName.toLowerCase().contains(searchText);

            boolean matchesLocation = service.address != null && service.address.toLowerCase().contains(locationText);

            boolean matchesCategory = selectedCategory.equals("All Categories") || selectedCategory.equalsIgnoreCase(service.category);

            if (matchesName && matchesLocation && matchesCategory) {
                filteredServiceList.add(service);
            }
        }

        serviceAdapter.notifyDataSetChanged();
    }

    private void resetFilters() {

        searchServicesInput.setText("");
        locationFilterInput.setText("");
        categoryFilterSpinner.setSelection(0);

        filterServices();
    }
}
