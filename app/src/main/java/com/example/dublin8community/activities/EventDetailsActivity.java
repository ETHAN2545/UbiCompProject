package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView eventNameText;
    private TextView categoryText;
    private TextView descriptionText;
    private TextView locationText;
    private TextView addressText;
    private TextView startTimeText;
    private TextView endTimeText;

    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventNameText = findViewById(R.id.eventNameText);
        categoryText = findViewById(R.id.categoryText);
        descriptionText = findViewById(R.id.descriptionText);
        locationText = findViewById(R.id.locationText);
        addressText = findViewById(R.id.addressText);
        startTimeText = findViewById(R.id.startTimeText);
        endTimeText = findViewById(R.id.endTimeText);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        String eventId = getIntent().getStringExtra("eventId");

        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadEventDetails(String eventId) {

        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Event event = snapshot.getValue(Event.class);

                if (event == null) {
                    Toast.makeText(EventDetailsActivity.this, "Unable to load event details", Toast.LENGTH_SHORT).show();

                    finish();
                    return;
                }

                eventNameText.setText(event.eventName);
                categoryText.setText("Category: " + event.category);
                descriptionText.setText(event.description);
                locationText.setText(event.locationName);
                addressText.setText(event.address);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                startTimeText.setText("Starts: " + dateFormat.format(new Date(event.startTime)));

                endTimeText.setText("Ends: " + dateFormat.format(new Date(event.endTime)));
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(EventDetailsActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
