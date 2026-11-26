package com.example.dublin8community.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dublin8community.R;
import com.example.dublin8community.models.Service;

import java.util.List;

public class ServiceAdapter extends ArrayAdapter<Service> {

    private Context context;
    private List<Service> services;

    public ServiceAdapter(Context context, List<Service> services) {
        super(context, R.layout.service_list_item, services);

        this.context = context;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.service_list_item, parent, false);
        }

        Service service = services.get(position);

        TextView serviceNameText = view.findViewById(R.id.serviceNameText);
        TextView serviceDetailsText = view.findViewById(R.id.serviceDetailsText);

        serviceNameText.setText(service.serviceName);

        String details = service.category + " | " + service.address;

        serviceDetailsText.setText(details);

        return view;
    }
}
