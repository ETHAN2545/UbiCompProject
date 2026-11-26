package com.example.dublin8community.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventNameInput, descriptionInput;
    private EditText locationNameInput, addressInput;
    private EditText latitudeInput, longitudeInput;

    private Spinner categorySpinner;

    private Button startDateButton, endDateButton, saveEventButton;

    private TextView startDateText, endDateText;

    private DatabaseReference eventsRef;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    private long startTime = 0;
    private long endTime = 0;

    private String eventId;
    private Event existingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventNameInput = findViewById(R.id.eventNameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        locationNameInput = findViewById(R.id.locationNameInput);
        addressInput = findViewById(R.id.addressInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);

        categorySpinner = findViewById(R.id.categorySpinner);

        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        saveEventButton = findViewById(R.id.saveEventButton);

        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);

        String[] categories = {"Music", "Sports", "Culture", "Community", "Education", "Food", "Family"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdapter);

        eventId = getIntent().getStringExtra("eventId");

        if (eventId != null) {
            setTitle("Edit Event");
            saveEventButton.setText("Update Event");
            loadEvent();
        } else {
            setTitle("Add Event");
        }

        startDateButton.setOnClickListener(v -> selectDateTime(true));
        endDateButton.setOnClickListener(v -> selectDateTime(false));

        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void loadEvent() {

        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(AddEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();

                    finish();

                    return;
                }

                existingEvent = snapshot.getValue(Event.class);

                if (existingEvent == null) {
                    Toast.makeText(AddEventActivity.this, "Could not load event", Toast.LENGTH_SHORT).show();

                    finish();

                    return;
                }

                eventNameInput.setText(existingEvent.eventName);
                descriptionInput.setText(existingEvent.description);
                locationNameInput.setText(existingEvent.locationName);
                addressInput.setText(existingEvent.address);
                latitudeInput.setText(String.valueOf(existingEvent.latitude));
                longitudeInput.setText(String.valueOf(existingEvent.longitude));

                for (int i = 0; i < categorySpinner.getCount(); i++) {
                    if (categorySpinner.getItemAtPosition(i).toString().equals(existingEvent.category)) {

                        categorySpinner.setSelection(i);

                        break;
                    }
                }

                startTime = existingEvent.startTime;
                endTime = existingEvent.endTime;

                startCalendar.setTimeInMillis(startTime);
                endCalendar.setTimeInMillis(endTime);

                updateDateText(startDateText, startTime);
                updateDateText(endDateText, endTime);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(AddEventActivity.this, "Error loading event: " + error.getMessage(), Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }

    private void selectDateTime(boolean isStartDate) {

        Calendar calendar = isStartDate ? startCalendar : endCalendar;

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePicker = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (isStartDate) {
                    startTime = calendar.getTimeInMillis();
                    updateDateText(startDateText, startTime);

                } else {
                    endTime = calendar.getTimeInMillis();
                    updateDateText(endDateText, endTime);
                }
                },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();

            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void updateDateText(TextView textView, long timestamp) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        textView.setText(dateFormat.format(new Date(timestamp)));
    }

    private void saveEvent() {

        String eventName = eventNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String locationName = locationNameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        String latitudeString = latitudeInput.getText().toString().trim();
        String longitudeString = longitudeInput.getText().toString().trim();

        String category = categorySpinner.getSelectedItem().toString();

        if (eventName.isEmpty() || description.isEmpty() || locationName.isEmpty() || address.isEmpty() || latitudeString.isEmpty() || longitudeString.isEmpty()) {

            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime == 0 || endTime == 0) {
            Toast.makeText(this, "Please select the event dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime <= startTime) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
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

        if (eventId == null) {

            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            eventId = eventsRef.push().getKey();

            Event event = new Event(
                    eventId,
                    eventName,
                    description,
                    category,
                    locationName,
                    address,
                    latitude,
                    longitude,
                    startTime,
                    endTime,
                    "",
                    currentUserId,
                    System.currentTimeMillis()
            );

            saveToDatabase(event);

        } else {

            Event updatedEvent = new Event(
                    eventId,
                    eventName,
                    description,
                    category,
                    locationName,
                    address,
                    latitude,
                    longitude,
                    startTime,
                    endTime,
                    existingEvent.imageUrl,
                    existingEvent.createdBy,
                    existingEvent.createdAt
            );

            saveToDatabase(updatedEvent);
        }
    }

    private void saveToDatabase(Event event) {

        eventsRef.child(eventId).setValue(event).addOnSuccessListener(unused -> {

            Toast.makeText(AddEventActivity.this, eventId != null ? "Event saved successfully" : "Event saved", Toast.LENGTH_SHORT).show();

            finish();
        })
                .addOnFailureListener(e -> {

                    Toast.makeText(AddEventActivity.this, "Error saving event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
