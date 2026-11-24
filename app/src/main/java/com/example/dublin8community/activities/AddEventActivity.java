package com.example.dublin8community.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventNameInput, descriptionInput;
    private EditText locationNameInput, addressInput;
    private EditText latitudeInput, longitudeInput;

    private Spinner categorySpinner;

    private Button startDateButton, endDateButton, saveEventButton;

    private TextView startDateText, endDateText;

    private DatabaseReference eventsRef;

    private Calendar startCalendar, endCalendar;

    private long startTime = 0;
    private long endTime = 0;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        startDateButton.setOnClickListener(v -> pickDateTime(true));
        endDateButton.setOnClickListener(v -> pickDateTime(false));

        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void pickDateTime(boolean isStart) {

        Calendar calendar = isStart ? startCalendar : endCalendar;

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {

                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                updateSelectedDate(isStart);

                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );

                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void updateSelectedDate(boolean isStart) {

        Calendar calendar = isStart ? startCalendar : endCalendar;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String formattedDate = dateFormat.format(calendar.getTime());

        if (isStart) {

            startTime = calendar.getTimeInMillis();
            startDateText.setText(formattedDate);

        } else {

            endTime = calendar.getTimeInMillis();
            endDateText.setText(formattedDate);
        }
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

            Toast.makeText(this, "Please select the event start and end times", Toast.LENGTH_SHORT).show();

            return;
        }

        if (endTime <= startTime) {

            Toast.makeText(this, "The end time must be after the start time", Toast.LENGTH_SHORT).show();

            return;
        }

        double latitude;
        double longitude;

        try {

            latitude = Double.parseDouble(latitudeString);
            longitude = Double.parseDouble(longitudeString);

            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {

                Toast.makeText(this, "Please enter valid coordinates", Toast.LENGTH_SHORT).show();

                return;
            }

        } catch (NumberFormatException e) {

            Toast.makeText(this, "Latitude and longitude must be numbers", Toast.LENGTH_SHORT).show();

            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String eventId = eventsRef.push().getKey();

        if (eventId == null) {
            Toast.makeText(this, "Could not generate event ID", Toast.LENGTH_SHORT).show();
            return;
        }

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
                userId,
                System.currentTimeMillis()
        );

        saveEventButton.setEnabled(false);

        eventsRef.child(eventId).setValue(event).addOnCompleteListener(task -> {

                    saveEventButton.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();

                        finish();

                    } else {

                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";

                        Toast.makeText(this, "Failed to save event: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
