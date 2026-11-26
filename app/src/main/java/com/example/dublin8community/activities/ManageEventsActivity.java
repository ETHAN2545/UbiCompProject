package com.example.dublin8community.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

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

public class ManageEventsActivity extends AppCompatActivity {

    private ListView eventsListView;
    private Button addEventButton;

    private DatabaseReference eventsRef;

    private List<Event> eventList;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        eventsListView = findViewById(R.id.eventsListView);
        addEventButton = findViewById(R.id.addEventButton);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventList = new ArrayList<>();

        eventAdapter = new EventAdapter(this, eventList);
        eventsListView.setAdapter(eventAdapter);

        addEventButton.setOnClickListener(v ->  {
            Intent intent = new Intent(ManageEventsActivity.this, AddEventActivity.class);

            startActivity(intent);
        });

        loadEvents();

        eventsListView.setOnItemClickListener((parent, view, position, id) -> {

            Event selectedEvent = eventList.get(position);

            showEventOptions(selectedEvent);
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
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(ManageEventsActivity.this, "Failed to retrieve events: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showEventOptions(Event event) {
        String[] options = {"Edit Event", "Delete Event"};

        new AlertDialog.Builder(this).setTitle(event.eventName).setItems(options, (dialog, which) -> {

            if (which == 0) {
                editEvent(event);

            } else {
                deleteEvent(event);
            }
        })
                .setNegativeButton("Cancel", null).show();
    }

    private void editEvent(Event event) {

        Intent intent = new Intent(ManageEventsActivity.this, AddEventActivity.class);

        intent.putExtra("eventId", event.eventId);

        startActivity(intent);
    }

    private void deleteEvent(Event event) {

        new AlertDialog.Builder(this).setTitle("Delete Event").setMessage("Are you sure you want to delete " + event.eventName + "?").setPositiveButton("Delete", (dialog, which) -> {

            eventsRef.child(event.eventId).removeValue().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Toast.makeText(ManageEventsActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ManageEventsActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            });
        })
                .setNegativeButton("Cancel", null).show();
    }
}
