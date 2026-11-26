package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServicesMapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private DatabaseReference servicesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_map);

        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.servicesMap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        loadServices();
    }

    private void loadServices() {

        servicesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                googleMap.clear();

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                int markerCount = 0;

                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {

                    Service service = serviceSnapshot.getValue(Service.class);

                    if (service != null && Double.isFinite(service.latitude) && Double.isFinite(service.longitude) && service.latitude >= -90 && service.latitude <= 90 && service.longitude >= -180 && service.longitude <= 180 && (service.latitude != 0 || service.longitude != 0)) {

                        LatLng serviceLocation = new LatLng(service.latitude, service.longitude);

                        String serviceName = service.serviceName == null ? "Community Service" : service.serviceName;

                        String address = service.address == null ? "" : service.address;

                        googleMap.addMarker(new MarkerOptions().position(serviceLocation).title(serviceName).snippet(address));

                        boundsBuilder.include(serviceLocation);

                        markerCount++;
                    }
                }

                if (markerCount == 1) {

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(boundsBuilder.build().getCenter(), 14f));

                } else if (markerCount > 1) {

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

                } else {

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.3420, -6.2920), 13f));

                    Toast.makeText(ServicesMapActivity.this, "No services with valid locations available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(ServicesMapActivity.this, "Failed to load services: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
