package com.example.dublin8community.activities;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.adapters.EventAdapter;
import com.example.dublin8community.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserEventsActivity extends AppCompatActivity {

    private ListView eventsListView;
    private DatabaseReference eventsRef;

    private List<Event> eventList;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_events);

        eventsListView = findViewById(R.id.eventsListView);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventList = new ArrayList<>();

        eventAdapter = new EventAdapter(this, eventList);
        eventsListView.setAdapter(eventAdapter);

        loadEvents();

        eventsListView.setOnItemClickListener((parent, view, position, id) -> {

            Event selectedEvent = eventList.get(position);

            Intent intent = new Intent(UserEventsActivity.this, EventDetailsActivity.class);

            intent.putExtra("eventId", selectedEvent.eventId);

            startActivity(intent);
        });
    }

    private void loadEvents() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                eventList.clear();

                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {

                    Event event = eventSnapshot.getValue(Event.class);

                    if (event != null) {
                        event.eventId = eventSnapshot.getKey();

                        eventList.add(event);
                    }
                }

                eventAdapter.notifyDataSetChanged();

                if (eventList.isEmpty()) {
                    Toast.makeText(UserEventsActivity.this, "No community events available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserEventsActivity.this, "Failed to load events: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
