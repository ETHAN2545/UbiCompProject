package com.example.dublin8community.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        super(context, R.layout.event_list_item, events);

        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.eventNameText);
        TextView eventDetails = view.findViewById(R.id.eventDetailsText);

        eventName.setText(event.eventName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String formattedDate = dateFormat.format(new Date(event.startTime));

        String details = event.category + " | " + event.locationName + "\n" + formattedDate;

        eventDetails.setText(details);

        return view;
    }
}
