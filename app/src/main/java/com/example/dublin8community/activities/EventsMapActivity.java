package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Event;
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

public class EventsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.eventsMap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        loadEvents();
    }

    private void loadEvents() {

        eventsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                googleMap.clear();

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                int markerCount = 0;

                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {

                    Event event = eventSnapshot.getValue(Event.class);

                    if (event != null && Double.isFinite(event.latitude) && Double.isFinite(event.longitude) && event.latitude >= -90 && event.latitude <= 90 && event.longitude >= -180 && event.longitude <= 180 && (event.latitude != 0 || event.longitude != 0)) {

                        LatLng eventLocation = new LatLng(event.latitude, event.longitude);

                        String eventName = event.eventName == null ? "Community Event" : event.eventName;

                        String location = event.locationName == null ? "" : event.locationName;

                        googleMap.addMarker(new MarkerOptions().position(eventLocation).title(eventName).snippet(location));

                        boundsBuilder.include(eventLocation);

                        markerCount++;
                    }
                }

                if (markerCount == 1) {

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(boundsBuilder.build().getCenter(), 14f));

                } else if (markerCount > 1) {

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

                } else {

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.3420, -6.2920), 13f));

                    Toast.makeText(EventsMapActivity.this, "No events with valid locations available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(EventsMapActivity.this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
