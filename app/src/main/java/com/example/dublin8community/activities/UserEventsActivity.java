package com.example.dublin8community.activities;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

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

    private EditText searchEventsInput;
    private EditText locationFilterInput;

    private Spinner categoryFilterSpinner;

    private Button resetFiltersButton;

    private DatabaseReference eventsRef;

    private List<Event> eventList;
    private List<Event> filteredEventList;

    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_events);

        eventsListView = findViewById(R.id.eventsListView);

        searchEventsInput = findViewById(R.id.searchEventsInput);
        locationFilterInput = findViewById(R.id.locationFilterInput);

        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);

        resetFiltersButton = findViewById(R.id.resetFiltersButton);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

        eventAdapter = new EventAdapter(this, filteredEventList);
        eventsListView.setAdapter(eventAdapter);

        setupCategoryFilter();

        searchEventsInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
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
                filterEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        categoryFilterSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {

                        filterEvents();
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                }
        );

        resetFiltersButton.setOnClickListener(v -> resetFilters());

        loadEvents();

        eventsListView.setOnItemClickListener((parent, view, position, id) -> {

            Event selectedEvent = filteredEventList.get(position);

            Intent intent = new Intent(UserEventsActivity.this, EventDetailsActivity.class);

            intent.putExtra("eventId", selectedEvent.eventId);

            startActivity(intent);
        });
    }

    private void setupCategoryFilter() {

        String[] categories = {
                "All Categories",
                "Music",
                "Sports",
                "Culture",
                "Community",
                "Education",
                "Food",
                "Family"
        };

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryFilterSpinner.setAdapter(categoryAdapter);
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

                filterEvents();

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

    private void filterEvents() {

        String searchText = searchEventsInput.getText().toString().trim().toLowerCase();

        String locationText = locationFilterInput.getText().toString().trim().toLowerCase();

        String selectedCategory = categoryFilterSpinner.getSelectedItem().toString();

        filteredEventList.clear();

        for (Event event : eventList) {

            boolean matchesName = event.eventName != null && event.eventName.toLowerCase().contains(searchText);

            boolean matchesLocation = event.locationName != null && event.locationName.toLowerCase().contains(locationText);

            boolean matchesCategory = selectedCategory.equals("All Categories") || selectedCategory.equalsIgnoreCase(event.category);

            if (matchesName && matchesLocation && matchesCategory) {
                filteredEventList.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
    }

    private void resetFilters() {

        searchEventsInput.setText("");
        locationFilterInput.setText("");
        categoryFilterSpinner.setSelection(0);

        filterEvents();
    }
}
